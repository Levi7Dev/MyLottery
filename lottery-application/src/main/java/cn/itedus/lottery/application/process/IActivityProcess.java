package cn.itedus.lottery.application.process;

import cn.itedus.lottery.application.process.req.DrawProcessReq;
import cn.itedus.lottery.application.process.res.DrawProcessResult;
import cn.itedus.lottery.application.process.res.RuleQuantificationCrowdResult;
import cn.itedus.lottery.domain.rule.model.req.DecisionMatterReq;

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

    /**
     * 规则量化人群，返回可参与的活动id
     * @param req 规则请求
     * @return    用户可以参与的活动id
     */
    RuleQuantificationCrowdResult doRuleQuantificationCrowd(DecisionMatterReq req);

}
