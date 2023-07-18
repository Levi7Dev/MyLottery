package cn.itedus.lottery.domain.activity.service.partake;

import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.common.Result;
import cn.itedus.lottery.domain.activity.model.req.PartakeReq;
import cn.itedus.lottery.domain.activity.model.res.PartakeResult;
import cn.itedus.lottery.domain.activity.model.res.StockResult;
import cn.itedus.lottery.domain.activity.model.vo.ActivityBillVO;
import cn.itedus.lottery.domain.activity.model.vo.UserTakeActivityVO;
import cn.itedus.lottery.domain.support.ids.IIdGenerator;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 领取活动模板抽象类
 * @author Levi
 * @create 2023/6/30 22:25
 */
public abstract class BaseActivityPartake extends ActivityPartakeSupport implements IActivityPartake{

    @Resource
    private Map<Constants.Ids, IIdGenerator> idGeneratorMap;

    //执行领取活动的方法
    @Override
    public PartakeResult doPartake(PartakeReq req) {
        // 1. 查询是否存在未执行抽奖领取活动单【user_take_activity 存在 state = 0，
        // 领取了但抽奖过程失败的，可以直接返回领取结果继续抽奖
        // 会返回最近参与的活动（选择自增id最大的一条记录，久一定是最新的记录）
        UserTakeActivityVO userTakeActivityVO = this.queryNoConsumedTakeActivityOrder(req.getActivityId(), req.getuId());
        //数据库中有参与过但失败的活动，返回参与活动结果，包括策略id，参与活动的id，继续进行抽奖
        if (null != userTakeActivityVO) {
            return buildPartakeResult(userTakeActivityVO.getStrategyId(), userTakeActivityVO.getTakeId(),
                    Constants.ResponseCode.NOT_CONSUMED_TAKE);
        }

        //2. 领取新的活动，需要查询该活动的基本信息
        //查询活动账单，根据活动id查询活动的信息（包括活动库存，可领取取的次数）
        //根据活动id从activity表查询活动所有信息，但活动库存优先从缓存取
        //根据用户id和活动id从user_take_activity_count表中获取用户参与活动的次数，然后聚合这些信息返回
        //用户可能是第一次参与活动，所以user_take_activity_count表中没有记录，所以会返回null，此时用户可参与次数就是null
        ActivityBillVO activityBillVO = super.queryActivityBill(req);

        //3. 活动信息校验处理（活动库存，状态，日期，个人参与次数）
        Result checkResult = this.checkActivityBill(req, activityBillVO);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(checkResult.getCode())) {
            return new PartakeResult(checkResult.getCode(), checkResult.getInfo());
        }

        //4. 扣减活动库存，通过Redis【活动库存扣减编号，作为锁的Key，缩小颗粒度】 Begin
        //**先扣减活动表activity的库存，然后再采用事务的方式扣减用户参与活动次数，并生成一条参与记录
        //此处扣减活动的库存已经进行了，如果后续第5步出现异常回滚，活动剩余库存不会回滚，已经被扣了一次
        StockResult subtractionActivityResult = this.subtractionActivityStockByRedis(req.getuId(),
                req.getActivityId(), activityBillVO.getStockCount());
        //Result subtractionActivityResult = this.subtractionActivityStock(req);    //从数据库中扣减库存（被redis替代）

        if (!Constants.ResponseCode.SUCCESS.getCode().equals(subtractionActivityResult.getCode())) {
            //失败需要删除对应的锁，让出给其他线程使用，但该用户参与活动失败了，不进行后续操作
            this.recoverActivityCacheStockByRedis(req.getActivityId(), subtractionActivityResult.getStockKey(),
                    subtractionActivityResult.getCode());
            return new PartakeResult(subtractionActivityResult.getCode(), subtractionActivityResult.getInfo());
        }

        //5. 领取活动信息，即在user_take_activity_count表中将用户可参与次数减一，在user_take_activity表中增加一条用户参与活动的记录
        //使用雪花算法生成takeId，不使用数据库自增主键，项目采用了分库分表，数据库自增主键会重复，分布式场景下都不能使用自增id
        Long takeId = idGeneratorMap.get(Constants.Ids.SnowFlake).nextId();
        Result grabResult = this.grabActivity(req, activityBillVO, takeId);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(grabResult.getCode())) {
            return new PartakeResult(grabResult.getCode(), grabResult.getInfo());
        }

        //6. 扣减活动库存，通过Redis End；（第4步在缓存中对活动使用库存+1，并设置了对应使用库存的分布式锁，第5步活动领取完成后需要删除该分布式锁）
        //具体数据库中活动库存扣减交给MQ异步进行
        this.recoverActivityCacheStockByRedis(req.getActivityId(), subtractionActivityResult.getStockKey(),
                Constants.ResponseCode.SUCCESS.getCode());

        return buildPartakeResult(activityBillVO.getStrategyId(), takeId, activityBillVO.getStockCount(),
                subtractionActivityResult.getStockSurplusCount(), Constants.ResponseCode.SUCCESS);
    }

    /**
     * 查询是否存在未执行的领奖活动单
     * state = 0，领取了抽奖活动当失败了，再次抽奖时直接返回领取结果直接抽奖
     * @param activityId
     * @param uId
     * @return
     */
    protected abstract UserTakeActivityVO queryNoConsumedTakeActivityOrder(Long activityId, String uId);

    /**
     * 活动信息校验处理【活动库存，状态，日期，个人参与次数】
     * @param partakeReq 参与活动请求
     * @param billVO     活动账单
     * @return           校验结果
     */
    protected abstract Result checkActivityBill(PartakeReq partakeReq, ActivityBillVO billVO);

    /**
     * 扣减活动库存
     * @param partakeReq 参与活动请求
     * @return           扣减结果
     */
    protected abstract Result subtractionActivityStock(PartakeReq partakeReq);

    /**
     * 扣减活动库存，通过redis
     * @param uId
     * @param activityId
     * @param stockCount    总库存
     * @return              扣减结果
     */
    protected abstract StockResult subtractionActivityStockByRedis(String uId, Long activityId, Integer stockCount);

    /**
     * 恢复活动库存，通过Redis
     * 【如果活动异常，则需要进行缓存库存恢复，只保证不超卖的特性，不保证一定能恢复占用库存，另外最终可以由任务进行补偿库存】
     *
     * @param activityId 活动ID
     * @param tokenKey   分布式 KEY 用于清理
     * @param code       状态
     */
    protected abstract void recoverActivityCacheStockByRedis(Long activityId, String tokenKey, String code);

    /**
     * 领取活动
     * @param partakeReq 参与活动请求
     * @return           领取结果
     */
    protected abstract Result grabActivity(PartakeReq partakeReq, ActivityBillVO billVO, Long takeId);

    /**
     * 封装结果【返回的策略ID，用于继续完成抽奖步骤】
     *
     * @param strategyId        策略ID
     * @param takeId            领取ID
     * @param stockCount        库存
     * @param stockSurplusCount 剩余库存
     * @param code              状态码
     * @return 封装结果
     */
    private PartakeResult buildPartakeResult(Long strategyId, Long takeId, Integer stockCount, Integer stockSurplusCount, Constants.ResponseCode code) {
        PartakeResult partakeResult = new PartakeResult(code.getCode(), code.getInfo());
        partakeResult.setStrategyId(strategyId);
        partakeResult.setTakeId(takeId);
        partakeResult.setStockCount(stockCount);
        partakeResult.setStockSurplusCount(stockSurplusCount);
        return partakeResult;
    }

    /**
     * 封装结果【返回的策略ID，用于继续完成抽奖步骤】
     *
     * @param strategyId 策略ID
     * @param takeId     领取ID
     * @param code       状态码
     * @return 封装结果
     */
    private PartakeResult buildPartakeResult(Long strategyId, Long takeId, Constants.ResponseCode code) {
        PartakeResult partakeResult = new PartakeResult(code.getCode(), code.getInfo());
        partakeResult.setStrategyId(strategyId);
        partakeResult.setTakeId(takeId);
        return partakeResult;
    }
}
