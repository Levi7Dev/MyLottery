package cn.itedus.lottery.domain.strategy.service.draw;

import cn.itedus.lottery.domain.strategy.service.algorithm.IDrawAlgorithm;


import cn.itedus.lottery.common.Constants;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 *抽奖活动的配置
 *@author Levi
 *@create 2023/6/23 17:01
 */
public class DrawConfig {
    @Resource
    private IDrawAlgorithm entiretyRateRandomDrawAlgorithm;

    @Resource
    private IDrawAlgorithm singleRateRandomDrawAlgorithm;

    protected static Map<Integer,IDrawAlgorithm> drawAlgorithmGroup = new ConcurrentHashMap<>();

    /**
     * PostConstruct是Java自带的注解，在方法上加该注解会在项目启动的时候执行该方法，
     * 也可以理解为在spring容器初始化的时候执行该方法
     * @time 2023/6/23 17:05
     */
    @PostConstruct
    public void init() {
        //项目启动时将抽奖策略配置到map中
        drawAlgorithmGroup.put(Constants.StrategyMode.ENTIRETY.getCode(), entiretyRateRandomDrawAlgorithm);
        drawAlgorithmGroup.put(Constants.StrategyMode.SINGLE.getCode(), singleRateRandomDrawAlgorithm);
    }

}
