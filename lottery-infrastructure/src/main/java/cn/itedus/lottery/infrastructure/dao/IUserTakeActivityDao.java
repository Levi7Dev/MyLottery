package cn.itedus.lottery.infrastructure.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.itedus.lottery.infrastructure.po.UserTakeActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户参与活动表
 * 使用分库
 */
@Mapper
public interface IUserTakeActivityDao {

    /**
     * 插入用户参与活动信息
     * DBRouter是分库注解，key 是入参对象中的属性（用户id），用于提取作为分库分表路由字段使用，即该用户会存入哪个数据库哪张表
     * @param userTakeActivity
     */
    @DBRouter(key = "uId")
    void insert(UserTakeActivity userTakeActivity);

}
