package cn.itedus.lottery.infrastructure.dao;

import cn.itedus.lottery.infrastructure.po.RuleTreeNodeLine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 规则树节点连线
 * @author Levi
 * @create 2023/7/7 16:50
 */
@Mapper
public interface RuleTreeNodeLineDao {

    /**
     *  查询规则树节点连线集合
     * @param req
     * @return
     */
    List<RuleTreeNodeLine> queryRuleTreeNodeLineList(RuleTreeNodeLine req);

    /**
     * 查询规则树连线数量
     * @param treeId 规则树ID
     * @return
     */
    int queryTreeNodeLineCount(Long treeId);

}
