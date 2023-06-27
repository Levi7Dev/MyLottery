package cn.itedus.lottery.domain.award.service.factory;

import cn.itedus.lottery.domain.award.service.goods.IDistributionGoods;
import org.springframework.stereotype.Service;

/**
 * 商品配送简单工厂，获取配送服务
 */
@Service
public class DistributionGoodsFactory extends GoodsConfig{

    public IDistributionGoods getDistributionGoodsService (Integer awardType) {
        return goodsMap.get(awardType);
    }

}
