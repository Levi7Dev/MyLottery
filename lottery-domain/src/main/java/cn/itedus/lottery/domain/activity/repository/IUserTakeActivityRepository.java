package cn.itedus.lottery.domain.activity.repository;

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

}
