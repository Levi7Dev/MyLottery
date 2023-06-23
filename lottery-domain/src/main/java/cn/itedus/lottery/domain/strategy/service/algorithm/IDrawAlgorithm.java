package cn.itedus.lottery.domain.strategy.service.algorithm;

import cn.itedus.lottery.domain.strategy.model.vo.AwardRateInfo;

import java.util.List;


public interface IDrawAlgorithm {

    /**
     * 程序启动时初始化概率元祖，在初始化完成后使用过程中不允许修改元祖数据
     * @param[1] strategyId 策略id
     * @param[2] awardRateInfoList  奖品概率配置集合
     * @time 2023/6/23 15:58
     */
    void initRateTuple(Long strategyId, List<AwardRateInfo> awardRateInfoList);

    /**
     * 判断是否已经做了数据初始化
     * @param[1] strateId
     * @return boolean
     * @time 2023/6/23 16:01
     */
    boolean isExistRateTuple(Long strategyId);

    /**
     * 无论任何一种抽奖算法的使用，都以这个接口作为标准的抽奖接口进行抽奖
     * 生成随机数，索引到对应奖品信息返回结果
     * @param[1] strategyId 策略id
     * @param[2] excludeAwardIds    不能作为奖品的id
     * @return String   中奖结果
     * @time 2023/6/23 15:18
     */
    String randomDraw(Long strategyId, List<String> excludeAwardIds);

}
