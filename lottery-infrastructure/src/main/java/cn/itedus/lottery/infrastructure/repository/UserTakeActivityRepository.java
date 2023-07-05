package cn.itedus.lottery.infrastructure.repository;

import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.domain.activity.model.vo.DrawOrderVO;
import cn.itedus.lottery.domain.activity.model.vo.UserTakeActivityVO;
import cn.itedus.lottery.domain.activity.repository.IUserTakeActivityRepository;
import cn.itedus.lottery.infrastructure.dao.IUserStrategyExportDao;
import cn.itedus.lottery.infrastructure.dao.IUserTakeActivityCountDao;
import cn.itedus.lottery.infrastructure.dao.IUserTakeActivityDao;
import cn.itedus.lottery.infrastructure.po.UserStrategyExport;
import cn.itedus.lottery.infrastructure.po.UserTakeActivity;
import cn.itedus.lottery.infrastructure.po.UserTakeActivityCount;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 用户参与活动仓储
 */
@Component
public class UserTakeActivityRepository implements IUserTakeActivityRepository {

    @Resource
    private IUserTakeActivityCountDao userTakeActivityCountDao;

    @Resource
    private IUserTakeActivityDao userTakeActivityDao;

    @Resource
    private IUserStrategyExportDao userStrategyExportDao;


    /**
     * 扣减个人活动参与次数，数据库活动表中take_count代表每人可 参与次数
     * @param activityId        活动id
     * @param activityName      活动名字
     * @param takeCount         活动个人可领取次数
     * @param userTakeLeftCount 活动个人剩余领取次数
     * @param uId               用户id
     * @param partakeDate       领取时间
     * @return
     */
    @Override
    public int subtractionLeftCount(Long activityId, String activityName, Integer takeCount, Integer userTakeLeftCount, String uId, Date partakeDate) {
        //为空说明用户首次参与该活动，所以数据库剩余可领取次数为null
        if (null == userTakeLeftCount) {
            UserTakeActivityCount userTakeActivityCount = new UserTakeActivityCount();
            userTakeActivityCount.setuId(uId);
            userTakeActivityCount.setActivityId(activityId);
            userTakeActivityCount.setTotalCount(takeCount);
            //设置可领取次数为每人可领取次数-1
            userTakeActivityCount.setLeftCount(takeCount - 1);
            userTakeActivityCountDao.insert(userTakeActivityCount);
            return 1;
        } else {
            UserTakeActivityCount userTakeActivityCount = new UserTakeActivityCount();
            userTakeActivityCount.setuId(uId);
            userTakeActivityCount.setActivityId(activityId);
            return userTakeActivityCountDao.updateLeftCount(userTakeActivityCount);
        }
    }

    /**
     *
     * @param activityId        活动id
     * @param activityName      活动名字
     * @param takeCount         活动个人可领取次数
     * @param userTakeLeftCount 活动个人剩余领取次数
     * @param uId               用户id
     * @param partakeDate       领取时间
     * @param takeId            领取id
     */
    @Override
    public void takeActivity(Long activityId, String activityName, Long strategyId, Integer takeCount, Integer userTakeLeftCount, String uId, Date partakeDate, Long takeId) {
        UserTakeActivity userTakeActivity = new UserTakeActivity();
        userTakeActivity.setuId(uId);
        userTakeActivity.setTakeId(takeId);
        userTakeActivity.setActivityId(activityId);
        userTakeActivity.setActivityName(activityName);
        userTakeActivity.setTakeDate(partakeDate);
        //用户剩余可参与活动次数==null，
        if (null == userTakeLeftCount) {
            //user_take_activity表中的take_count代表领取次数
            userTakeActivity.setTakeCount(1);
        } else {
            //更新已领取次数为：活动每个人可领取次数-用户剩余领取次数+1
            userTakeActivity.setTakeCount(takeCount - userTakeLeftCount + 1);
        }
        userTakeActivity.setStrategyId(strategyId);
        userTakeActivity.setState(Constants.TaskState.NO_USED.getCode());
        //uuid用来防重，会拼接已经参与的次数的信息
        String uuid = uId + "_" + activityId + "_" + userTakeActivity.getTakeCount();
        userTakeActivity.setUuid(uuid);

        userTakeActivityDao.insert(userTakeActivity);
    }

    /**
     * 锁定领取记录，也就是改变数据库对应的state字段，标记是否领取
     * @param uId
     * @param activityId
     * @param takeId
     * @return
     */
    @Override
    public int lockTackActivity(String uId, Long activityId, Long takeId) {
        UserTakeActivity userTakeActivity = new UserTakeActivity();
        userTakeActivity.setuId(uId);
        userTakeActivity.setActivityId(activityId);
        userTakeActivity.setTakeId(takeId);
        return userTakeActivityDao.lockTackActivity(userTakeActivity);
    }

    @Override
    public void saveUserStrategyExport(DrawOrderVO drawOrder) {
        UserStrategyExport userStrategyExport = new UserStrategyExport();
        userStrategyExport.setuId(drawOrder.getuId());
        userStrategyExport.setActivityId(drawOrder.getActivityId());
        userStrategyExport.setOrderId(drawOrder.getOrderId());
        userStrategyExport.setStrategyId(drawOrder.getStrategyId());
        userStrategyExport.setStrategyMode(drawOrder.getStrategyMode());
        userStrategyExport.setGrantType(drawOrder.getGrantType());
        userStrategyExport.setGrantDate(drawOrder.getGrantDate());
        userStrategyExport.setGrantState(drawOrder.getGrantState());
        userStrategyExport.setAwardId(drawOrder.getAwardId());
        userStrategyExport.setAwardType(drawOrder.getAwardType());
        userStrategyExport.setAwardName(drawOrder.getAwardName());
        userStrategyExport.setAwardContent(drawOrder.getAwardContent());
        userStrategyExport.setUuid(String.valueOf(drawOrder.getOrderId()));
        userStrategyExportDao.insert(userStrategyExport);
    }

    @Override
    public UserTakeActivityVO queryNoConsumedTakeActivityOrder(Long activityId, String uId) {
        UserTakeActivity userTakeActivity = new UserTakeActivity();
        userTakeActivity.setuId(uId);
        userTakeActivity.setActivityId(activityId);
        UserTakeActivity noConsumedTakeActivityOrder = userTakeActivityDao.queryNoConsumedTakeActivityOrder(userTakeActivity);

        //未查询到符合的领取单，直接返回null
        if (null == noConsumedTakeActivityOrder) {
            return null;
        }

        UserTakeActivityVO userTakeActivityVO = new UserTakeActivityVO();
        userTakeActivityVO.setActivityId(noConsumedTakeActivityOrder.getActivityId());
        userTakeActivityVO.setTakeId(noConsumedTakeActivityOrder.getTakeId());
        userTakeActivityVO.setStrategyId(noConsumedTakeActivityOrder.getStrategyId());
        userTakeActivityVO.setState(noConsumedTakeActivityOrder.getState());

        return userTakeActivityVO;
    }
}
