package cn.itedus.lottery.domain.strategy.service.draw;

import cn.itedus.lottery.domain.strategy.model.req.DrawReq;
import cn.itedus.lottery.domain.strategy.model.res.DrawResult;

/*
 * 对外提供的接口，用于执行抽奖动作
 *@author Levi
 *@create 2023/6/23 16:56
 */
public interface IDrawExec {

    /**
     * 执行抽奖活动，对外暴露
     * @param[1] req 用户传过来的抽奖请求信息，包括用户id，抽奖策略id
     * @return DrawResult
     * @time 2023/6/23 21:49
     */
    DrawResult doDrawExec(DrawReq req);

}
