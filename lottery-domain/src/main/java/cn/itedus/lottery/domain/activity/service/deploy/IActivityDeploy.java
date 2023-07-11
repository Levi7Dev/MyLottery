package cn.itedus.lottery.domain.activity.service.deploy;

import cn.itedus.lottery.domain.activity.model.req.ActivityConfigReq;
import cn.itedus.lottery.domain.activity.model.vo.ActivityVO;

import java.util.List;

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

    /**
     * 扫描待处理的活动列表，状态为：通过、活动中
     *
     * 通过 -> 时间符合时 -> 活动中
     * 活动中 -> 时间到期时 -> 关闭
     *
     * @param id    活动表的自增id，不是活动id
     * @return      待处理的活动集合
     */
    List<ActivityVO> scanToDoActivityList(Long id);

}
