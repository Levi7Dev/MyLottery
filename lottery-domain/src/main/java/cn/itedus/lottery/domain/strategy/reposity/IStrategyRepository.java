package cn.itedus.lottery.domain.strategy.reposity;

import cn.itedus.lottery.domain.strategy.model.aggregates.StrategyRich;
import cn.itedus.lottery.infrastructure.po.Award;

/*
 *@author Levi
 *@create 2023/6/23 17:23
 */
public interface IStrategyRepository {

    StrategyRich queryStrategyRich(Long strategyId);

    Award queryAwardInfo(String AwardId);

}
