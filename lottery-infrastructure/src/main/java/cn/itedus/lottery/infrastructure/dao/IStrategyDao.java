package cn.itedus.lottery.infrastructure.dao;

import cn.itedus.lottery.infrastructure.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

/*
 *
 *@author Levi
 *@create 2023/6/23 17:30
 */
@Mapper
public interface IStrategyDao {

    Strategy queryStrategy(Long strategyId);

    void insert(Strategy strategy);
}
