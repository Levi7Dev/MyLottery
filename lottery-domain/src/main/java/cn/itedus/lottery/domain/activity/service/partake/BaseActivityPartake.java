package cn.itedus.lottery.domain.activity.service.partake;

import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.common.Result;
import cn.itedus.lottery.domain.activity.model.req.PartakeReq;
import cn.itedus.lottery.domain.activity.model.res.PartakeResult;
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
            return buildPartakeResult(userTakeActivityVO.getStrategyId(), userTakeActivityVO.getTakeId());
        }

        //2. 领取新的活动，需要查询该活动的基本信息
        //查询活动账单，根据活动id查询活动的信息（包括活动库存，可领取取的次数）
        ActivityBillVO activityBillVO = super.queryActivityBill(req);

        //3. 活动信息校验处理（活动库存，状态，日期，个人参与次数）
        Result checkResult = this.checkActivityBill(req, activityBillVO);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(checkResult.getCode())) {
            return new PartakeResult(checkResult.getCode(), checkResult.getInfo());
        }

        //4. 扣减库存活动【目前为直接对配置库中的 lottery.activity 直接操作表扣减库存，后续优化为Redis扣减】
        //此处扣减活动的库存已经进行了，如果后续第5步出现异常回滚，活动剩余库存不会回滚，已经被扣了一次
        Result subtractionActivityResult = this.subtractionActivityStock(req);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(subtractionActivityResult.getCode())) {
            return new PartakeResult(subtractionActivityResult.getCode(), subtractionActivityResult.getInfo());
        }

        //5. 领取活动信息，即在user_take_activity_count表中将用户可参与次数减一，在user_take_activity表中增加一条用户参与活动的记录
        Long takeId = idGeneratorMap.get(Constants.Ids.SnowFlake).nextId();
        Result grabResult = this.grabActivity(req, activityBillVO, takeId);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(grabResult.getCode())) {
            return new PartakeResult(grabResult.getCode(), grabResult.getInfo());
        }

        return buildPartakeResult(activityBillVO.getStrategyId(), takeId);
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
     * 领取活动
     * @param partakeReq 参与活动请求
     * @return           领取结果
     */
    protected abstract Result grabActivity(PartakeReq partakeReq, ActivityBillVO billVO, Long takeId);

    /**
     * 封装结果，返回策略ID，用于继续完成抽奖步骤
     * @param strategyId
     * @param takeId
     * @return
     */
    private PartakeResult buildPartakeResult(Long strategyId, Long takeId) {
        PartakeResult partakeResult = new PartakeResult(Constants.ResponseCode.SUCCESS.getCode(), Constants.ResponseCode.SUCCESS.getInfo());
        partakeResult.setStrategyId(strategyId);
        partakeResult.setTakeId(takeId);
        return partakeResult;
    }
}
