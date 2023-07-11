package cn.itedus.lottery.domain.activity.service.partake;

import cn.itedus.lottery.common.Result;
import cn.itedus.lottery.domain.activity.model.req.PartakeReq;
import cn.itedus.lottery.domain.activity.model.res.PartakeResult;
import cn.itedus.lottery.domain.activity.model.vo.DrawOrderVO;
import cn.itedus.lottery.domain.activity.model.vo.InvoiceVO;

import java.util.List;

/**
 * 抽奖活动参与接口
 */
public interface IActivityPartake {

    /**
     * 参与活动
     * @param req 入参，封装了用户id，活动id，参与时间
     * @return 领取结果
     */
    PartakeResult doPartake(PartakeReq req);

    /**
     * 保存奖品单
     * @param drawOrderVO
     * @return
     */
    Result recordDrawOrder(DrawOrderVO drawOrderVO);

    /**
     * 更新mq状态
     * @param uId
     * @param orderId
     * @param mqState
     */
    void updateInvoiceMqState(String uId, Long orderId, Integer mqState);

    /**
     * 扫描发货单MQ状态，即中奖记录表中mq状态（0未发送、1发送成功、2发送失败）
     * 把发送失败的记录重新再发一次
     *
     * @param dbCount   指定的分库
     * @param tbCount   指定的分表
     * @return  需要重发的发货单集合
     */
    List<InvoiceVO> scanInvoiceMqState(int dbCount, int tbCount);

}
