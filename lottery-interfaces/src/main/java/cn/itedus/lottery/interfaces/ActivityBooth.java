package cn.itedus.lottery.interfaces;

import cn.itedus.lottery.common.Constants;
import cn.itedus.lottery.common.Result;
import cn.itedus.lottery.infrastructure.dao.IActivityDao;
import cn.itedus.lottery.infrastructure.po.Activity;
import cn.itedus.lottery.rpc.IActivityBooth;
import cn.itedus.lottery.rpc.dto.ActivityDto;
import cn.itedus.lottery.rpc.req.ActivityReq;
import cn.itedus.lottery.rpc.res.ActivityRes;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 * 活动展台
 */
//dubbo中的注解，将该类注册到注册中心
@Service
public class ActivityBooth implements IActivityBooth {

    //自动注入
    @Resource
    private IActivityDao activityDao;

    @Override
    public ActivityRes queryActivityById(ActivityReq req) {
        //封装mybaits从数据库获取返回的数据，IActivityDao被@Mapper注解修饰，会调用mybatis查数据
        Activity activity = activityDao.queryActivityById(req.getActivityId());
        //进一步封装数据，需要远程发送到请求端，参数需要实现Serializable接口
        ActivityDto activityDto = new ActivityDto();
        activityDto.setActivityId(activity.getActivityId());
        activityDto.setActivityName(activity.getActivityName());
        activityDto.setActivityDesc(activity.getActivityDesc());
        activityDto.setBeginDateTime(activity.getBeginDateTime());
        activityDto.setEndDateTime(activity.getEndDateTime());
        activityDto.setStockCount(activity.getStockCount());
        activityDto.setTakeCount(activity.getTakeCount());

        return new ActivityRes(
                new Result(Constants.ResponseCode.SUCCESS.getCode(),
                        Constants.ResponseCode.SUCCESS.getInfo()),
                activityDto);
    }
}
