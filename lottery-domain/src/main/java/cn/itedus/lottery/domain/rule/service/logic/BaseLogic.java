package cn.itedus.lottery.domain.rule.service.logic;

import cn.itedus.lottery.domain.rule.model.req.DecisionMatterReq;
import cn.itedus.lottery.domain.rule.model.vo.TreeNodeLineVO;
import cn.itedus.lottery.common.Constants;

import java.util.List;

/**
 * 规则基础抽象类
 * @author Levi
 * @create 2023/7/7 14:25
 */
public abstract class BaseLogic implements LogicFilter {

    @Override
    public Long filter(String matterValue, List<TreeNodeLineVO> treeNodeLineInfoList) {
        for (TreeNodeLineVO nodeLine : treeNodeLineInfoList) {
            //判断从map中取出的matterValue是否满足决策树中节点连线的决策限制，返回满足的节点，即走到下一个节点（一个节点对应多个子节点，但只有一个满足条件）
            if (decisionLogic(matterValue, nodeLine)) {
                return nodeLine.getNodeIdTo();
            }
        }
        return Constants.Global.TREE_NULL_NODE;
    }

    @Override
    public abstract String matterValue(DecisionMatterReq decisionMatter);

    private boolean decisionLogic(String matterValue, TreeNodeLineVO nodeLineVO) {
        switch (nodeLineVO.getRuleLimitType()) {
            case Constants.RuleLimitType.EQUAL:
                return matterValue.equals(nodeLineVO.getRuleLimitValue());
            case Constants.RuleLimitType.GT:
                return Double.parseDouble(matterValue) > Double.parseDouble(nodeLineVO.getRuleLimitValue());
            case Constants.RuleLimitType.LT:
                return Double.parseDouble(matterValue) < Double.parseDouble(nodeLineVO.getRuleLimitValue());
            case Constants.RuleLimitType.GE:
                return Double.parseDouble(matterValue) >= Double.parseDouble(nodeLineVO.getRuleLimitValue());
            case Constants.RuleLimitType.LE:
                return Double.parseDouble(matterValue) <= Double.parseDouble(nodeLineVO.getRuleLimitValue());
            default:
                return false;
        }
    }

}
