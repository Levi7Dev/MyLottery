package cn.itedus.lottery.infrastructure.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import cn.itedus.lottery.infrastructure.po.UserStrategyExport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户策略计算结果表
 * 分表
 * DBRouterStrategy注解为分表注解
 * 配置后会通过数据库路由组件把sql语句添加上分表字段，比如表 user 修改为 user_003
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserStrategyExportDao {

    /**
     * 插入一条用户策略计算结果数据
     * 插入操作需要用到分库分表
     * @param userStrategyExport 入参
     */
    @DBRouter(key = "uId")
    void insert(UserStrategyExport userStrategyExport);

    /**
     * 查询数据
     * @param uId 用户id
     * @return 用户策略
     */
    @DBRouter
    UserStrategyExport queryUserStrategyExportByUId(String uId);

    /**
     * 更新发奖状态
     * @param userStrategyExport 发奖信息
     */
    @DBRouter
    void updateUserAwardState(UserStrategyExport userStrategyExport);

    /**
     * 更新发送MQ状态
     * @param userStrategyExport 发奖信息
     */
    @DBRouter
    void updateInvoiceMqState(UserStrategyExport userStrategyExport);

    /**
     * 扫描发货单 MQ 状态，把未发送 MQ 的单子扫描出来，做补偿
     *
     * @return 发货单
     */
    List<UserStrategyExport> scanInvoiceMqState();

}
