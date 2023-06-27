package cn.itedus.lottery.domain.award.service.goods;

import cn.itedus.lottery.domain.award.model.req.GoodsReq;
import cn.itedus.lottery.domain.award.model.res.DistributionRes;

/**
 * 抽奖结果货物配送接口
 */
public interface IDistributionGoods {

    /**
     * 奖品配送接口，奖品类型（1:文字描述、2:兑换码、3:优惠券、4:实物奖品）
     * @param req
     * @return
     */
    DistributionRes doDistribution(GoodsReq req);

    Integer getDistributionGoodsName();

}
