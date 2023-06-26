package cn.itedus.lottery.domain.strategy.model.aggregates;

import cn.itedus.lottery.infrastructure.po.Strategy;
import cn.itedus.lottery.infrastructure.po.StrategyDetail;

import java.util.List;

/*
 *@title StrategyRich
 *@description
 *@author Levi
 *@create 2023/6/23 15:40
 */
public class StrategyRich {
    // 策略ID
    private Long strategyId;
    // 策略配置
    private Strategy strategy;
    // 策略明细，同一个策略下可能有多个策略明细记录，用list存储
    private List<StrategyDetail> strategyDetailList;

    public StrategyRich() {
    }

    public StrategyRich(Long strategyId, Strategy strategy, List<StrategyDetail> strategyDetailList) {
        this.strategyId = strategyId;
        this.strategy = strategy;
        this.strategyDetailList = strategyDetailList;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<StrategyDetail> getStrategyDetailList() {
        return strategyDetailList;
    }

    public void setStrategyDetailList(List<StrategyDetail> strategyDetailList) {
        this.strategyDetailList = strategyDetailList;
    }

    @Override
    public String toString() {
        return "StrategyRich{" +
                "strategyId=" + strategyId +
                ", strategy=" + strategy +
                ", strategyDetailList=" + strategyDetailList +
                '}';
    }
}
