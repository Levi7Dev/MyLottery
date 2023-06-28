package cn.itedus.lottery.domain.activity.service.deploy;

import cn.itedus.lottery.domain.activity.model.req.ActivityConfigReq;

/**
 * 部署活动配置接口
 */
public interface IActivityDeploy {

    /**
     * 创建活动信息
     * @param req
     */
    void createActivity(ActivityConfigReq req);

    /**
     * 修改活动信息
     * @param req
     */
    void updateActivity(ActivityConfigReq req);

}
