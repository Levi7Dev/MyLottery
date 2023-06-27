package cn.itedus.lottery.domain.award.service.goods.impl;

import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.domain.award.model.req.GoodsReq;
import cn.itedus.lottery.domain.award.model.res.DistributionRes;
import cn.itedus.lottery.domain.award.service.goods.DistributionBase;
import cn.itedus.lottery.domain.award.service.goods.IDistributionGoods;

/**
 * 优惠券商品
 */
public class CouponGoods extends DistributionBase implements IDistributionGoods {

    @Override
    public DistributionRes doDistribution(GoodsReq req) {
        super.updateUserAwardState(
                req.getuId(),
                req.getOrderId(),
                req.getAwardId(),
                Constants.AwardState.SUCCESS.getCode(),
                Constants.AwardState.SUCCESS.getInfo()
        );

        return new DistributionRes(
                req.getuId(),
                Constants.AwardState.SUCCESS.getCode(),
                Constants.AwardState.SUCCESS.getInfo()
        );
    }

    @Override
    public Integer getDistributionGoodsName() {
        return Constants.AwardType.CouponGoods.getCode();
    }
}
