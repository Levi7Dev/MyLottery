package cn.itedus.lottery.test;

import cn.itedus.lottery.domain.strategy.model.req.DrawReq;
import cn.itedus.lottery.domain.strategy.service.draw.IDrawExec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/*
 *@author Levi
 *@create 2023/6/24 9:52
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringRunnerTest {

    private Logger logger = LoggerFactory.getLogger(SpringRunnerTest.class);

    @Resource
    private IDrawExec drawExec;


    @Test
    public void test_drawExec() {
        //drawExec.doDrawExec(new DrawReq("Levi", 10001l));
        drawExec.doDrawExec(new DrawReq("A", 10002l));
        drawExec.doDrawExec(new DrawReq("B", 10001l));
        drawExec.doDrawExec(new DrawReq("C", 10002l));
    }

}
