package cn.itedus.lottery.domain.award.repository;


/**
 *  奖品表仓储服务接口
 *  分库分表中用户中奖记录
 */
public interface IOrderRepository {

    /**
     * 更新奖品发放状态，***export表中grant_state字段状态
     * @param uId           用户id
     * @param orderId       订单id
     * @param awardId       奖品id
     * @param grantState    奖品状态
     */
    void updateUserAwardState(String uId, Long orderId, String awardId, Integer grantState);

}
