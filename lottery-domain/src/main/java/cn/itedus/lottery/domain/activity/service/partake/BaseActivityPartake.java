package cn.itedus.lottery.domain.activity.service.partake;

import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.common.Result;
import cn.itedus.lottery.domain.activity.model.req.PartakeReq;
import cn.itedus.lottery.domain.activity.model.res.PartakeResult;
import cn.itedus.lottery.domain.activity.model.vo.ActivityBillVO;

/**
 * 领取活动模板抽象类
 * @author Levi
 * @create 2023/6/30 22:25
 */
public abstract class BaseActivityPartake extends ActivityPartakeSupport implements IActivityPartake{

    @Override
    public PartakeResult doPartake(PartakeReq req) {
        //查询活动账单
        ActivityBillVO activityBillVO = super.queryActivityBill(req);

        //活动信息校验处理（活动库存，状态，日期，个人参与次数）
        Result checkResult = this.checkActivityBill(req, activityBillVO);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(checkResult.getCode())) {
            return new PartakeResult(checkResult.getCode(), checkResult.getInfo());
        }

        //扣减库存活动【目前为直接对配置库中的 lottery.activity 直接操作表扣减库存，后续优化为Redis扣减】
        Result subtractionActivityResult = this.subtractionActivityStock(req);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(subtractionActivityResult.getCode())) {
            return new PartakeResult(subtractionActivityResult.getCode(), subtractionActivityResult.getInfo());
        }

        //领取活动信息【个人用户把活动信息写入用户表】
        Result grabResult = this.grabActivity(req);
        if (!Constants.ResponseCode.SUCCESS.getCode().equals(grabResult.getCode())) {
            return new PartakeResult(grabResult.getCode(), grabResult.getInfo());
        }

        //封装结果【返回策略id，用于继续完成抽奖步骤】
        PartakeResult partakeResult = new PartakeResult(Constants.ResponseCode.SUCCESS.getCode(), Constants.ResponseCode.SUCCESS.getInfo());
        partakeResult.setStrategyId(activityBillVO.getStrategyId());

        return partakeResult;
    }

    /**
     * 活动信息校验处理【活动库存，状态，日期，个人参与次数】
     * @param partakeReq 参与活动请求
     * @param billVO     活动账单
     * @return           校验结果
     */
    protected abstract Result checkActivityBill(PartakeReq partakeReq, ActivityBillVO billVO);

    /**
     * 扣减活动库存
     * @param partakeReq 参与活动请求
     * @return           扣减结果
     */
    protected abstract Result subtractionActivityStock(PartakeReq partakeReq);

    /**
     * 领取活动
     * @param partakeReq 参与活动请求
     * @return           领取结果
     */
    protected abstract Result grabActivity(PartakeReq partakeReq);
}
