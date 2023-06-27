package cn.itedus.lottery.domain.award.service.factory;

import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.domain.award.service.goods.IDistributionGoods;
import cn.itedus.lottery.domain.award.service.goods.impl.CouponGoods;
import cn.itedus.lottery.domain.award.service.goods.impl.DescGoods;
import cn.itedus.lottery.domain.award.service.goods.impl.PhysicalGoods;
import cn.itedus.lottery.domain.award.service.goods.impl.RedeemCodeGoods;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置各类奖品
 */
public class GoodsConfig {

    protected static Map<Integer, IDistributionGoods> goodsMap = new ConcurrentHashMap<>();

    @Resource
    private CouponGoods couponGoods;

    @Resource
    private DescGoods descGoods;

    @Resource
    private PhysicalGoods physicalGoods;

    @Resource
    private RedeemCodeGoods redeemCodeGoods;

    @PostConstruct
    public void init() {
        goodsMap.put(Constants.AwardType.CouponGoods.getCode(), couponGoods);
        goodsMap.put(Constants.AwardType.DESC.getCode(), descGoods);
        goodsMap.put(Constants.AwardType.CouponGoods.getCode(), redeemCodeGoods);
        goodsMap.put(Constants.AwardType.PhysicalGoods.getCode(), physicalGoods);
    }
}
