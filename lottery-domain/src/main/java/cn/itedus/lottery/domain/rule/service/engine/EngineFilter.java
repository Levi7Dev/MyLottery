package cn.itedus.lottery.domain.rule.service.engine;

import cn.itedus.lottery.domain.rule.model.req.DecisionMatterReq;
import cn.itedus.lottery.domain.rule.model.res.EngineResult;

/**
 * 规则过滤器
 * @author Levi
 * @create 2023/7/7 15:05
 */
public interface EngineFilter {

    /**
     * 规则过滤器
     * @param matterReq 规则决策物料
     * @return          规则决策结果
     */
    EngineResult process(final DecisionMatterReq matterReq);

}
