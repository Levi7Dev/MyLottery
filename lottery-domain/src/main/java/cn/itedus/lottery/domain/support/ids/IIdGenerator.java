package cn.itedus.lottery.domain.support.ids;

/**
 * ID生成接口
 */
public interface IIdGenerator {

    /**
     * 获取ID
     * 1.雪花算法，用于生成单号
     * 2.日期算法，用于生成活动编号类，生成数字串较短
     * 3.随机算法，用于生成策略ID
     * @return ID
     */
    long nextId();

}
