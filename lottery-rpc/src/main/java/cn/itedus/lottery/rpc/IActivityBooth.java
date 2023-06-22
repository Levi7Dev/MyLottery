package cn.itedus.lottery.rpc;

import cn.itedus.lottery.rpc.req.ActivityReq;
import cn.itedus.lottery.rpc.res.ActivityRes;

/**
 * 由于 RPC 接口在通信的过程中，需要提供接口的描述文件，也就是接口的定义信息。
 * 所以把所有的 RPC 接口定义都放到 lottery-rpc 模块下，
 * 这种方式的使用让外部就只依赖这样一个 pom 配置引入的 Jar 包即可。
 */

/**
 * 活动展台接口类，用于包装活动的创建、查询、修改、审核的接口。
 * 1.创建活动
 * 2.更新活动
 * 3.查询活动
 */
public interface IActivityBooth {

    ActivityRes queryActivityById(ActivityReq req);

}
