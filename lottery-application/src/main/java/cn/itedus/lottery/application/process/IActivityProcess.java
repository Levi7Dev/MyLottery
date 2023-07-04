package cn.itedus.lottery.application.process;

import cn.itedus.lottery.application.process.req.DrawProcessReq;
import cn.itedus.lottery.application.process.res.DrawProcessResult;

/**
 * 抽奖流程编排接口
 * @author Levi
 * @create 2023/7/4 21:48
 */
public interface IActivityProcess {

    /**
     * 执行抽奖流程
     * @param req
     * @return
     */
    DrawProcessResult doDrawProcess(DrawProcessReq req);

}
