package cn.itedus.lottery.domain.strategy.reposity;

import cn.itedus.lottery.domain.strategy.model.aggregates.StrategyRich;
import cn.itedus.lottery.infrastructure.po.Award;

import java.util.List;

/*
 *@author Levi
 *@create 2023/6/23 17:23
 */
public interface IStrategyRepository {

    StrategyRich queryStrategyRich(Long strategyId);

    Award queryAwardInfo(String AwardId);

    List<String> queryNoStockStrategyAwardList(Long strategyId);

    /**
     * 扣减库存
     * @param[1] strategyId
     * @param[2] awardId
     * @return boolean
     * @time 2023/6/26 23:06
     */
    boolean deductStock(Long strategyId, String awardId);

}
