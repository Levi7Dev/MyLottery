package cn.itedus.lottery.domain.support.ids.policy;

import cn.itedus.lottery.domain.support.ids.IIdGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

/**
 * 随机生成ID
 * 使用工具类生成 org.apache.commons.lang3.RandomStringUtils
 */
@Component
public class RandomNumeric implements IIdGenerator {

    @Override
    public synchronized long nextId() {
        return Long.parseLong(RandomStringUtils.randomNumeric(11));
    }
}
