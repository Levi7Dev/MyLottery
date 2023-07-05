package cn.itedus.lottery.domain.activity.service.partake.impl;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.common.Result;
import cn.itedus.lottery.domain.activity.model.req.PartakeReq;
import cn.itedus.lottery.domain.activity.model.vo.ActivityBillVO;
import cn.itedus.lottery.domain.activity.model.vo.DrawOrderVO;
import cn.itedus.lottery.domain.activity.model.vo.UserTakeActivityVO;
import cn.itedus.lottery.domain.activity.repository.IUserTakeActivityRepository;
import cn.itedus.lottery.domain.activity.service.partake.BaseActivityPartake;
import cn.itedus.lottery.domain.support.ids.IIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 活动参与具体功能实现
 * @author Levi
 * @create 2023/6/30 22:44
 */
@Service
public class ActivityPartakeImpl extends BaseActivityPartake {

    private Logger logger = LoggerFactory.getLogger(ActivityPartakeImpl.class);

    @Resource
    private IUserTakeActivityRepository userTakeActivityRepository;

    @Resource
    private Map<Constants.Ids, IIdGenerator> idGeneratorMap;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private IDBRouterStrategy dbRouter;

    /**
     * 活动信息校验处理，把活动库存、状态、日期、个人参与次数
     * @param partakeReq 参与活动请求
     * @param billVO     活动账单，数据库中数据
     * @return
     */
    @Override
    protected Result checkActivityBill(PartakeReq partakeReq, ActivityBillVO billVO) {
        //校验：活动状态
        if (!Constants.ActivityState.DOING.getCode().equals(billVO.getState())) {
            logger.warn("当前活动非可用，state：{}", billVO.getState());
            return Result.buildResult(Constants.ResponseCode.UN_ERROR, "当前状态不可用");
        }

        //校验：活动日期，billVO是从数据库查出来的活动数据，partakeReq包含了请求时间，时间为：new now()
        if (billVO.getBeginDateTime().after(partakeReq.getPartakeDate()) || billVO.getEndDateTime().before(partakeReq.getPartakeDate())) {
            logger.warn("活动时间范围非可用 beginDateTime：{} endDateTime：{}", billVO.getBeginDateTime(), billVO.getEndDateTime());
            return Result.buildResult(Constants.ResponseCode.UN_ERROR, "活动时间范围非可用");
        }

        //校验库存
        if (billVO.getStockSurplusCount() <= 0) {
            logger.warn("活动剩余库存非可用 stockSurplusCount：{}", billVO.getStockSurplusCount());
            return Result.buildResult(Constants.ResponseCode.UN_ERROR, "活动剩余库存非可用");
        }

        //校验：个人库存 - 个人活动可领取次数
        if (billVO.getUserTakeLeftCount() <= 0) {
            logger.warn("个人领取次数非可用，userTakeLeftCount：{}", billVO.getUserTakeLeftCount());
            return Result.buildResult(Constants.ResponseCode.UN_ERROR, "个人领取次数非可用");
        }

        return Result.buildSuccessResult();
    }

    @Override
    protected Result subtractionActivityStock(PartakeReq partakeReq) {
        //相应的活动库存-1
        int count = activityRepository.subtractionActivityStock(partakeReq.getActivityId());

        if (0 == count) {
            logger.error("扣减活动库存失败 activityId：{}", partakeReq.getActivityId());
            return Result.buildResult(Constants.ResponseCode.NO_UPDATE);
        }
        return Result.buildSuccessResult();
    }

    @Override
    protected UserTakeActivityVO queryNoConsumedTakeActivityOrder(Long activityId, String uId) {
        return userTakeActivityRepository.queryNoConsumedTakeActivityOrder(activityId, uId);
    }

    /**
     * 领取活动
     * @param partakeReq 参与活动请求
     * @return           领取结果
     */
    @Override
    protected Result grabActivity(PartakeReq partakeReq, ActivityBillVO billVO, Long takeId) {
        try {
            //是编程式处理分库分表，如果在不需要使用事务的场景下，直接使用注解配置到DAO方法上即可。两个方式不能混用
            //计算该用户产生的结果会落到哪个数据库上
            dbRouter.doRouter(partakeReq.getuId());
            //编程式事务，用的就是路由中间件提供的事务对象，通过这样的方式也可以更加方便的处理细节的回滚，而不需要抛异常处理。
            return transactionTemplate.execute(transactionStatus -> {
                try {
                    //扣减个人可参与次数
                    int updateCount = userTakeActivityRepository.subtractionLeftCount(
                            billVO.getActivityId(),
                            billVO.getActivityName(),
                            billVO.getTakeCount(),
                            billVO.getUserTakeLeftCount(),
                            partakeReq.getuId(),
                            partakeReq.getPartakeDate());

                    if (0 == updateCount) {
                        transactionStatus.setRollbackOnly();
                        logger.error("领取活动，扣减个人已参与次数失败，activityId：{}，uId：{}", partakeReq.getActivityId(), partakeReq.getuId());
                        return Result.buildResult(Constants.ResponseCode.NO_UPDATE);
                    }
                    //插入领取活动信息
                    userTakeActivityRepository.takeActivity(
                            billVO.getActivityId(),
                            billVO.getActivityName(),
                            billVO.getStrategyId(),
                            billVO.getTakeCount(),
                            billVO.getUserTakeLeftCount(),
                            partakeReq.getuId(),
                            partakeReq.getPartakeDate(), takeId);
                } catch (DuplicateKeyException e) {
                    transactionStatus.setRollbackOnly();
                    logger.error("领取活动，唯一索引冲突，activityId：{}，uId：{}", partakeReq.getActivityId(), partakeReq.getuId(), e);
                    return Result.buildResult(Constants.ResponseCode.INDEX_DUP);
                }
                return Result.buildSuccessResult();
            });
        } finally {
            dbRouter.clear();
        }
    }

    /**
     * 保存奖品单
     * @param drawOrderVO
     * @return
     */
    @Override
    public Result recordDrawOrder(DrawOrderVO drawOrderVO) {
        try {
            dbRouter.doRouter(drawOrderVO.getuId());
            return transactionTemplate.execute(status -> {
                try {
                    //锁定领取记录
                    int lockCount = userTakeActivityRepository.lockTackActivity(drawOrderVO.getuId(), drawOrderVO.getActivityId(), drawOrderVO.getTakeId());
                    if (0 == lockCount) {
                        status.setRollbackOnly();
                        logger.error("记录中奖单，个人参与活动抽奖已消耗完 activityId：{} uId：{}", drawOrderVO.getActivityId(), drawOrderVO.getuId());
                        return Result.buildResult(Constants.ResponseCode.NO_UPDATE);
                    }

                    //保存抽奖信息
                    userTakeActivityRepository.saveUserStrategyExport(drawOrderVO);
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    logger.error("领取活动，唯一索引冲突 activityId：{} uId：{}", drawOrderVO.getActivityId(), drawOrderVO.getuId(), e);
                    return Result.buildResult(Constants.ResponseCode.INDEX_DUP);
                }
                return Result.buildSuccessResult();
            });
        } finally {
            dbRouter.clear();
        }
    }
}
