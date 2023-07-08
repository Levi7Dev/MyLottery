package cn.itedus.lottery.domain.strategy.service.algorithm;


import cn.itedus.lottery.domain.strategy.model.vo.AwardRateVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 *@title BaseAlgorithm
 *@description  共用的逻辑算法
 *@author Levi
 *@create 2023/6/23 15:28
 */
public abstract class BaseAlgorithm implements IDrawAlgorithm{

    //斐波那契散列增量，逻辑：黄金分割点：(√5 - 1) / 2 = 0.6180339887，Math.pow(2, 32) * 0.6180339887 = 0x61c88647
    private final int HASH_INCREMENT = 0x61c88647;
    //初始数组长度
    private final int RATE_TUPLE_LENGTH = 128;
    //存放概率与奖品对应的散列结果
    protected Map<Long, String[]> rateTupleMap = new ConcurrentHashMap<>();
    //奖品区间概率值
    protected Map<Long, List<AwardRateVO>> awardRateInfoMap = new ConcurrentHashMap<>();


    @Override
    public void initRateTuple(Long strategyId, List<AwardRateVO> awardRateVOList) {
        //保存奖品概率信息
        awardRateInfoMap.put(strategyId, awardRateVOList);
        String[] rateTuple = rateTupleMap.computeIfAbsent(strategyId, k -> new String[RATE_TUPLE_LENGTH]);

        int cursorVal = 0;
        for (AwardRateVO awardRateVO : awardRateVOList) {
            int rateVal = awardRateVO.getAwardRate().multiply(new BigDecimal(100)).intValue();
            //循环填充概率范围值
            for (int i = cursorVal + 1; i <= rateVal + cursorVal; i++) {
                rateTuple[hashIdx(i)] = awardRateVO.getAwardId();
            }
            cursorVal += rateVal;
        }
    }

    @Override
    public boolean isExistRateTuple(Long strategyId) {
        return rateTupleMap.containsKey(strategyId);
    }

    /**
     * 斐波那契（Fibonacci）散列法，计算哈希索引下标值
     * @param[1] val
     * @return int
     * @time 2023/6/23 16:10
     */
    protected int hashIdx(int val) {
        int hashCode = val * HASH_INCREMENT + HASH_INCREMENT;
        return hashCode & (RATE_TUPLE_LENGTH - 1);
    }
}
