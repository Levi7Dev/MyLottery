package cn.itedus.lottery.domain.activity.service.partake;

import cn.itedus.lottery.domain.activity.model.req.PartakeReq;
import cn.itedus.lottery.domain.activity.model.vo.ActivityBillVO;
import cn.itedus.lottery.domain.activity.repository.IActivityRepository;

import javax.annotation.Resource;

/**
 * 活动领取操作，一些通用的操作
 * @author Levi
 * @create 2023/6/30 22:14
 */
public class ActivityPartakeSupport {

    @Resource
    private IActivityRepository activityRepository;

    protected ActivityBillVO queryActivityBill(PartakeReq req) {
        return activityRepository.queryActivityBill(req);
    }

}
