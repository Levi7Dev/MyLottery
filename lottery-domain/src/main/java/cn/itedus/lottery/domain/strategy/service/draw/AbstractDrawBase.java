package cn.itedus.lottery.domain.strategy.service.draw;

import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.domain.strategy.model.aggregates.StrategyRich;
import cn.itedus.lottery.domain.strategy.model.req.DrawReq;
import cn.itedus.lottery.domain.strategy.model.res.DrawResult;
import cn.itedus.lottery.domain.strategy.model.vo.*;
import cn.itedus.lottery.domain.strategy.service.algorithm.IDrawAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/*
 *@author Levi
 *@create 2023/6/23 17:01
 */
public abstract class AbstractDrawBase extends DrawStrategySupport implements IDrawExec{

    private Logger logger = LoggerFactory.getLogger(AbstractDrawBase.class);


    @Override
    public DrawResult doDrawExec(DrawReq req) {
        //1.获取抽奖策略，会包含具体的抽奖算法标号（strategyRich会把策略表和策略明细表中的信息聚合起来）
        //返回策略信息，包含策略对应的抽奖算法；策略id对应的策略详情列表，策略详情包含奖品id，名称，库存，*奖品中奖概率*
        StrategyRich strategyRich = super.queryStrategyRich(req.getStrategyId());
        StrategyBriefVO strategy = strategyRich.getStrategy();

        //2.校验抽奖策略是否已经初始化到内存
        this.checkAndInitRateData(req.getStrategyId(), strategy.getStrategyMode(), strategyRich.getStrategyDetailList());

        //3.获取无库存奖品列表，根据策略id查找符合条件的奖品id集合（该策略对应的奖品都会被排除掉），包括：奖品库存为空、风控策略、临时调整等
        //查询策略id对应的策略详情信息，将剩余库存为0的奖品id返回，奖品都没有库存了肯定不能参与抽奖
        List<String> excludeAwardIds = this.queryExcludeAwardIds(req.getStrategyId());

        //4.执行抽奖算法，将不参与抽奖的奖品id列表传入（即没有库存的奖品id集合）
        //返回中奖的奖品id
        String awardId = this.drawAlgorithm(req.getStrategyId(), drawAlgorithmGroup.get(strategy.getStrategyMode()), excludeAwardIds);

        //5.封装中奖结果
        return buildDrawResult(req.getuId(), req.getStrategyId(), awardId, strategy);
    }

    public void checkAndInitRateData(Long strategyId, Integer strategyMode, List<StrategyDetailBriefVO> strategyDetailList) {
        //获取具体的抽奖算法
        IDrawAlgorithm drawAlgorithm = drawAlgorithmGroup.get(strategyMode);

        //已经初始化过数据，不必重复初始化
        if (drawAlgorithm.isExist(strategyId)) {
            return;
        }

        //从策略明细列表中获取奖品ID和对应的中奖概率，用于后续初始化
        List<AwardRateVO> awardRateVOList = new ArrayList<>(strategyDetailList.size());
        for (StrategyDetailBriefVO strategyDetail : strategyDetailList) {
            awardRateVOList.add(new AwardRateVO(strategyDetail.getAwardId(), strategyDetail.getAwardRate()));
        }
        //根据中奖概率初始化中奖比率元祖
        drawAlgorithm.initRateTuple(strategyId, strategyMode, awardRateVOList);
    }

    /**
     * 获取不在抽奖范围内的列表，包括：奖品库存为空、风控策略、临时调整等，
     * 这类数据是含有业务逻辑的，所以需要由具体的实现方决定
     * @param[1] strategyId
     * @return List<String>
     * @time 2023/6/26 22:40
     */
    protected abstract List<String> queryExcludeAwardIds(Long strategyId);

    /**
     * 执行抽奖算法，抽象方法，具体实现交给业务
     * @param strategyId      策略ID
     * @param drawAlgorithm   抽奖算法模型
     * @param excludeAwardIds 无库存的奖品ID集合
     * @return 中奖奖品ID
     */
    protected abstract String drawAlgorithm(Long strategyId, IDrawAlgorithm drawAlgorithm, List<String> excludeAwardIds);

    /**
     * 包装抽奖结果
     *
     * @param uId        用户ID
     * @param strategyId 策略ID
     * @param awardId    奖品ID，null 情况：并发抽奖情况下，库存临界值1 -> 0，会有用户中奖结果为 null
     * @return 中奖结果
     */
    private DrawResult buildDrawResult(String uId, Long strategyId, String awardId, StrategyBriefVO strategy) {
        if (null == awardId) {
            logger.info("执行策略抽奖完成【未中奖】，用户：{} 策略ID：{}", uId, strategyId);
            return new DrawResult(uId, strategyId, Constants.DrawState.FAIL.getCode());
        }
        //根据中奖奖品id，查询奖品信息
        AwardBriefVO award = super.queryAwardInfoByAwardId(awardId);
        DrawAwardVO drawAwardVO = new DrawAwardVO(uId, award.getAwardId(), award.getAwardType(),
                                                    award.getAwardName(), award.getAwardContent());
        //设置策略模式，发奖方式，时间等
        drawAwardVO.setStrategyMode(strategy.getStrategyMode());
        drawAwardVO.setGrantType(strategy.getGrantType());
        drawAwardVO.setGrantDate(strategy.getGrantDate());
        logger.info("执行策略抽奖完成【已中奖】，用户：{} 策略ID：{} 奖品ID：{} 奖品名称：{}", uId, strategyId, awardId, award.getAwardName());

        return new DrawResult(uId, strategyId, Constants.DrawState.SUCCESS.getCode(), drawAwardVO);
    }

}
