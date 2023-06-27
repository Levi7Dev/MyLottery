package cn.itedus.lottery.domain.award.service.factory;

import cn.itedus.lottery.domain.award.service.goods.IDistributionGoods;

/**
 * 商品配送简单工厂，获取配送服务
 */
public class DistributionGoodsFactory extends GoodsConfig{

    public IDistributionGoods getDistributionGoodsService (Integer awardType) {
        return goodsMap.get(awardType);
    }

}
