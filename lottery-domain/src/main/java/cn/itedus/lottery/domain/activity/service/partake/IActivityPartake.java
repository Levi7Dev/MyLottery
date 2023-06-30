package cn.itedus.lottery.domain.activity.service.partake;

import cn.itedus.lottery.domain.activity.model.req.PartakeReq;
import cn.itedus.lottery.domain.activity.model.res.PartakeResult;

/**
 * 抽奖活动参与接口
 */
public interface IActivityPartake {

    /**
     * 参与活动
     * @param req 入参，封装了用户id，活动id，参与时间
     * @return 领取结果
     */
    PartakeResult doPartake(PartakeReq req);

}
