package cn.itedus.lottery.domain.rule.service.engine.impl;

import cn.itedus.lottery.domain.rule.model.aggregates.TreeRuleRich;
import cn.itedus.lottery.domain.rule.model.req.DecisionMatterReq;
import cn.itedus.lottery.domain.rule.model.res.EngineResult;
import cn.itedus.lottery.domain.rule.model.vo.TreeNodeVO;
import cn.itedus.lottery.domain.rule.repository.IRuleRepository;
import cn.itedus.lottery.domain.rule.service.engine.EngineBase;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 规则引擎处理器
 * @author Levi
 * @create 2023/7/7 16:12
 */
@Service("ruleEngineHandle")
public class RuleEngineHandle extends EngineBase {

    @Resource
    private IRuleRepository ruleRepository;

    @Override
    public EngineResult process(DecisionMatterReq matterReq) {
        //决策规则树
        TreeRuleRich treeRuleRich = ruleRepository.queryTreeRuleRich(matterReq.getTreeId());
        if (null == treeRuleRich) {
            throw new RuntimeException("Tree Rule is null");
        }

        //决策节点
        TreeNodeVO treeNodeInfo = engineDecisionMaker(treeRuleRich, matterReq);

        //决策结果
        return new EngineResult(matterReq.getUserId(), treeNodeInfo.getTreeId(),
                treeNodeInfo.getTreeNodeId(), treeNodeInfo.getNodeValue());
    }
}
