package cn.itedus.lottery.test.application;

import cn.itedus.lottery.application.process.IActivityProcess;
import cn.itedus.lottery.application.process.req.DrawProcessReq;
import cn.itedus.lottery.application.process.res.DrawProcessResult;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityProcessTest {

    private Logger logger = LoggerFactory.getLogger(ActivityProcessTest.class);

    @Resource
    private IActivityProcess activityProcess;

    @Test
    public void test_doDrawProcess() {
        DrawProcessReq req = new DrawProcessReq();
        //请求头里面主要包含用户id和参与的活动id信息
        req.setuId("kafka");
        req.setActivityId(100001L);
        DrawProcessResult drawProcessResult = activityProcess.doDrawProcess(req);

        logger.info("请求入参：{}", JSON.toJSONString(req));
        logger.info("测试结果：{}", JSON.toJSONString(drawProcessResult));
    }

}
