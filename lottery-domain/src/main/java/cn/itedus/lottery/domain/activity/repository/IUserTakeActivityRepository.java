package cn.itedus.lottery.domain.activity.repository;

import cn.itedus.lottery.domain.activity.model.vo.DrawOrderVO;
import cn.itedus.lottery.domain.activity.model.vo.UserTakeActivityVO;

import java.util.Date;

/**
 * 用户参与活动仓储接口
 * @author Levi
 * @create 2023/6/30 22:46
 */
public interface IUserTakeActivityRepository {

    /**
     * 扣减个人活动参与次数
     * @param activityId        活动id
     * @param activityName      活动名字
     * @param takeCount         活动个人可领取次数
     * @param userTakeLeftCount 活动个人剩余领取次数
     * @param uId               用户id
     * @param partakeDate       领取时间
     * @return                  更新结果
     */
    int subtractionLeftCount(Long activityId, String activityName,
                             Integer takeCount, Integer userTakeLeftCount,
                             String uId, Date partakeDate);

    /**
     * 领取活动
     * @param activityId        活动id
     * @param activityName      活动名字
     * @param takeCount         活动个人可领取次数
     * @param userTakeLeftCount 活动个人剩余领取次数
     * @param uId               用户id
     * @param partakeDate       领取时间
     * @param takeId            领取id
     */
    void takeActivity(Long activityId, String activityName,
                      Integer takeCount, Integer userTakeLeftCount,
                      String uId, Date partakeDate, Long takeId);

    /**
     * 锁定活动领取记录
     * @param uId
     * @param activityId
     * @param takeId
     * @return
     */
    int lockTackActivity(String uId, Long activityId, Long takeId);

    /**
     * 保存抽奖信息
     * @param drawOrderVO
     */
    void saveUserStrategyExport(DrawOrderVO drawOrderVO);

    /**
     * 查询是否存在未执行抽奖领取活动单【user_take_activity 存在 state = 0，
     * 领取了但抽奖过程失败的，可以直接返回领取结果继续抽奖】
     * @param activityId
     * @param uId
     * @return
     */
    UserTakeActivityVO queryNoConsumedTakeActivityOrder(Long activityId, String uId);
}
