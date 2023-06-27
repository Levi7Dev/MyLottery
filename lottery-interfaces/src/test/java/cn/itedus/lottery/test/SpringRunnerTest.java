package cn.itedus.lottery.test;

import com.alibaba.fastjson.JSON;
import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.domain.award.model.req.GoodsReq;
import cn.itedus.lottery.domain.award.model.res.DistributionRes;
import cn.itedus.lottery.domain.award.service.factory.DistributionGoodsFactory;
import cn.itedus.lottery.domain.award.service.goods.IDistributionGoods;
import cn.itedus.lottery.domain.strategy.model.req.DrawReq;
import cn.itedus.lottery.domain.strategy.model.res.DrawResult;
import cn.itedus.lottery.domain.strategy.model.vo.DrawAwardInfo;
import cn.itedus.lottery.domain.strategy.service.draw.IDrawExec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/*
 *@author Levi
 *@create 2023/6/24 9:52
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringRunnerTest {

    private Logger logger = LoggerFactory.getLogger(SpringRunnerTest.class);

    @Resource
    private IDrawExec drawExec;

    @Resource
    private DistributionGoodsFactory distributionGoodsFactory;


    @Test
    public void test_drawExec() {
        drawExec.doDrawExec(new DrawReq("Levi", 10001l));
        drawExec.doDrawExec(new DrawReq("A", 10002l));
        drawExec.doDrawExec(new DrawReq("B", 10001l));
        drawExec.doDrawExec(new DrawReq("C", 10002l));
    }

    @Test
    public void test_award() {
        //执行抽奖
        DrawResult drawResult = drawExec.doDrawExec(new DrawReq("Levi", 10001L));
        //中奖状态：0未中奖、1已中奖、2兜底奖 Constants.DrawState
        Integer drawState = drawResult.getDrawState();
        //没中奖直接返回了，后续发货流程不会执行
        if (Constants.DrawState.FAIL.getCode().equals(drawState)) {
            logger.info("未中奖 DrawAwardInfo is null");
            return;
        }
        //中奖则查找中奖信息
        DrawAwardInfo drawAwardInfo = drawResult.getDrawAwardInfo();
        GoodsReq goodsReq = new GoodsReq(drawResult.getuId(), "2109313442431",
                drawAwardInfo.getAwardId(), drawAwardInfo.getAwardName(), drawAwardInfo.getAwardContent());
        //根据awardType（1:文字描述、2:兑换码、3:优惠券、4:实物奖品） 从抽奖工厂中获取对应的发奖操作
        IDistributionGoods distributionGoodsService = distributionGoodsFactory.getDistributionGoodsService(drawAwardInfo.getAwardType());
        DistributionRes distributionRes = distributionGoodsService.doDistribution(goodsReq);

        logger.info("测试结果：{}", JSON.toJSONString(distributionRes));
    }

}
