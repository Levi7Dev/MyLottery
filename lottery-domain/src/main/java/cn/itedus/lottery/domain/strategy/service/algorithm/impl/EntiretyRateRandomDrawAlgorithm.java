package cn.itedus.lottery.domain.strategy.service.algorithm.impl;


import cn.itedus.lottery.domain.strategy.model.vo.AwardRateInfo;
import cn.itedus.lottery.domain.strategy.service.algorithm.BaseAlgorithm;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/*
 *@title DefaultRateRandomDrawAlgorithm
 *@description
 *@author Levi
 *@create 2023/6/23 15:31
 */
@Component("entiretyRateRandomDrawAlgorithm")
public class EntiretyRateRandomDrawAlgorithm extends BaseAlgorithm {

    /**
     * 首先要从总的中奖列表中排除掉那些被排除掉的奖品，这些奖品会涉及到概率的值重新计算。
     * 如果排除后剩下的奖品列表小于等于1，则可以直接返回对应信息
     * 接下来就使用随机数工具生产一个100内的随值与奖品列表中的值进行循环比对，算法时间复杂度O(n)
     * @param[1] strategyId
     * @param[2] excludeAwardIds
     * @return String
     * @time 2023/6/23 16:30
     */
    @Override
    public String randomDraw(Long strategyId, List<String> excludeAwardIds) {
        BigDecimal differenceDenominator = BigDecimal.ZERO;

        //排除不在抽奖范围的奖品id
        List<AwardRateInfo> differenceAwardRateList = new ArrayList<>();
        List<AwardRateInfo> awardRateIntervalValList = awardRateInfoMap.get(strategyId);
        for (AwardRateInfo awardRateInfo : awardRateIntervalValList) {
            String awardId = awardRateInfo.getAwardId();
            if (excludeAwardIds.contains(awardId)) {
                continue;
            }
            differenceAwardRateList.add(awardRateInfo);
            differenceDenominator = differenceDenominator.add(awardRateInfo.getAwardRate());
        }

        //前置判断
        if (differenceAwardRateList.size() == 0) return "";
        if (differenceAwardRateList.size() == 1) return differenceAwardRateList.get(0).getAwardId();

        //获取随机概率值
        SecureRandom secureRandom = new SecureRandom();
        int randomVal = secureRandom.nextInt(100) + 1;

        //循环获取奖品
        String awardId = "";
        int cursorVal = 0;
        for (AwardRateInfo awardRateInfo : differenceAwardRateList) {
            int rateVal = awardRateInfo.getAwardRate()
                    .divide(differenceDenominator, 2, BigDecimal.ROUND_UP)
                    .multiply(new BigDecimal(100))
                    .intValue();
            if (randomVal <= cursorVal + rateVal) {
                awardId = awardRateInfo.getAwardId();
                break;
            }cursorVal += rateVal;
        }

        return awardId;
    }
}
