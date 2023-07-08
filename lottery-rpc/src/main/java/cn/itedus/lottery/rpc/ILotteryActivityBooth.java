package cn.itedus.lottery.rpc;

import cn.itedus.lottery.rpc.req.DrawReq;
import cn.itedus.lottery.rpc.req.QuantificationDrawReq;
import cn.itedus.lottery.rpc.res.DrawRes;

/**
 * 抽奖活动展台接口
 * @author Levi
 * @create 2023/7/8 15:21
 */
public interface ILotteryActivityBooth {

    /**
     * 指定活动抽奖
     * @param req 请求参数
     * @return    抽奖结果
     */
    DrawRes doDraw(DrawReq req);

    /**
     * 量化人群抽奖
     * @param quantificationDrawReq 请求参数
     * @return                      抽奖结果
     */
    DrawRes doQuantificationDraw(QuantificationDrawReq quantificationDrawReq);

}
