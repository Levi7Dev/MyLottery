package cn.itedus.lottery.domain.strategy.service.draw;

import cn.itedus.lottery.domain.strategy.model.req.DrawReq;
import cn.itedus.lottery.domain.strategy.model.res.DrawResult;

/*
 * 执行抽奖
 *@author Levi
 *@create 2023/6/23 16:56
 */
public interface IDrawExec {

    DrawResult doDrawExec(DrawReq req);

}
