package cn.itedus.lottery.domain.activity.model.req;

import java.util.Date;

/**
 * @description: 参与活动请求
 * 主要包含活动的用户id，用户参与的活动id，以及参与活动的时间（也就是点击的那一刻）
 */
public class PartakeReq {

    /** 用户ID */
    private String uId;
    /** 活动ID */
    private Long activityId;
    /** 活动领取时间 */
    private Date partakeDate;

    public PartakeReq() {
    }

    public PartakeReq(String uId, Long activityId) {
        this.uId = uId;
        this.activityId = activityId;
        //参与时间就是点击活动时的时间
        this.partakeDate = new Date();
    }

    public PartakeReq(String uId, Long activityId, Date partakeDate) {
        this.uId = uId;
        this.activityId = activityId;
        this.partakeDate = partakeDate;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Date getPartakeDate() {
        return partakeDate;
    }

    public void setPartakeDate(Date partakeDate) {
        this.partakeDate = partakeDate;
    }
}
