package com.yinfeng.wypzh.base;

import com.umeng.commonsdk.UMConfigure;

/**
 * 常量类
 */
public class Constants {
    public static final String UMENG_TAG_ONLINE = "ONLINE";
    public static final String UMENG_TAG_MEMBER = "MEMBER";

    public static final String DOWNLOAD_APK_NAME = "wypzh.apk";

    //阿里云存储
    public static final String OSS_ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    public static final String OSS_STS_SERVER = "http://47.104.76.32:8011/common/ossToken";
    public static final String OSS_BUCKET_NAME = "madou";
    public static final String OSS_ROOT_PATH = "https://madou.oss-cn-beijing.aliyuncs.com/";


    //	状态 SUBMIT:提交订单	PAID:已支付	TAKE:已接单	SERVICE:服务中	COMPLETE:完成
    //  CANCEL:取消	ERROR:异常
    public static final String ORDER_STATE_SUBMIT = "SUBMIT";//订单状态 提交订单
    public static final String ORDER_STATE_PAID = "PAID";//订单状态 已支付
    public static final String ORDER_STATE_TAKE = "TAKE";//订单状态 已接单
    public static final String ORDER_STATE_SERVICE = "SERVICE";//订单状态 服务中
    public static final String ORDER_STATE_COMPLETE = "COMPLETE";//订单状态
    public static final String ORDER_STATE_CANCEL = "CANCEL";//订单状态 取消
    public static final String ORDER_STATE_ERROR = "ERROR";//订单状态 异常

    //等待服务订单状态
    public static final String ORDER_WAIT_SERVICE_ARRIVE = "ARRIVED";

    //延时订单状态
    public static final String ORDER_RENEW_SUBMIT = "SUBMIT";
    public static final String ORDER_RENEW_PAID = "PAID";
    public static final String ORDER_RENEW_AGREE = "AGREE";//同意 陪诊员同意
    public static final String ORDER_RENEW_REJECT = "REJECT";//拒绝 陪诊员拒绝
    public static final String ORDER_RENEW_OVERTIME = "OVERTIME"; //超时 陪诊员超时未处理
    public static final String ORDER_RENEW_CANCEL = "CANCEL";  //会员取消申请
    public static final String ORDER_RENEW_ERROR = "ERROR";


    /**
     * NEW_ORDER("新订单","","有新订单，请及时查看。"),
     * UNPAID("取消订单","","您的订单已超过20分钟未支付，已取消订单。"),
     * TIMEOUT("未接单提醒","","您的订单已超过20分钟未接单，请耐心等待。"),
     * ARRIVED("服务开始提醒","","您好，陪诊员已确认到达，请尽快确认并开始服务。"),
     * RECEIPT("接单提醒","","已接单，请保持通讯畅通，陪诊员将尽快与您联系。"),
     * CANCEL_ORDER("取消订单","","抱歉，你的订单：{code}。用户由于计划有变取消了订单，请知晓。"),//+短信
     * OVERTIME("超时提醒","","距离服务结束还有20分钟，请注意时间安排或申请加时。"),//+短信
     * NEW_OVERTIME("延时申请","","您有一条延时申请，请及时处理。"),//+短信
     * APPLY_AGREE("延时确认","","您好，陪诊员已同意您的延时服务申请。"),
     * APPLY_REFUSE("申请结果","","抱歉，陪诊员拒绝了你的延时申请。"),
     * DESIGNATE("派单提醒","","您有一条工单，需及时处理。"),//+短信
     * HEAD_OF_TIME("服务即将开始","","你好，您的订单服务即将开始，请尽快到达指定地点并开始服务。"),
     * WAITER_EXAMINE_AGREE("信息审核","","您的信息已审核通过"),
     * WAITER_EXAMINE_REFUSE("信息审核","","您的信息未通过审核，请确认信息正确性。");
     * POSITION_ANOMALY("位置异常","","您已超出订单服务范围，请及时返回。"),
     * WAITER_CANCEL("取消订单","","目前陪诊员繁忙，暂无人接单，请稍后重试。");
     */

    public static final String NOTIF_ORDER_STATE_TIMEOUT = "TIMEOUT";
    public static final String NOTIF_ORDER_STATE_RECEIPT = "RECEIPT";
    public static final String NOTIF_ORDER_STATE_ARRIVED = "ARRIVED";
    public static final String NOTIF_ORDER_STATE_HEAD_OF_TIME = "HEAD_OF_TIME";
    public static final String NOTIF_ORDER_STATE_OVERTIME = "OVERTIME";
    public static final String NOTIF_ORDER_STATE_APPLY_AGREE = "APPLY_AGREE";
    public static final String NOTIF_ORDER_STATE_APPLY_REFUSE = "APPLY_REFUSE";
    public static final String NOTIF_ORDER_STATE_WAITER_CANCEL = "WAITER_CANCEL";


    //事件分发

    public static final String EVENTBUS_TAG_GET_MSG_NOTICE = "get_msg_notice";//消息页 订单提醒

    public static final String EVENTBUS_TAG_HAS_ARRIVED = "waiter_has_arrived";

    public static final String EVENTBUS_TAG_UPDATE_USERINFO = "update_userinfo";

    public static final String EVENTBUS_TAG_ORDER_REFRESH_WAIT_RECEIVER = "order_refresh_wait_receiver";
    public static final String EVENTBUS_TAG_ORDER_REFRESH_WAIT_SERVICE = "order_refresh_wait_service";
    public static final String EVENTBUS_TAG_ORDER_REFRESH_SERVICING = "order_refresh_servicing";
    public static final String EVENTBUS_TAG_ORDER_REFRESH_WAIT_COMMENT = "order_refresh_wait_comment";
    public static final String EVENTBUS_TAG_ORDER_REFRESH_COMPLETE = "order_refresh_complete";

    public static final String EVENTBUS_TAG_ORDER_REDPOINT_SHOW = "order_redpoint_show";
    public static final String EVENTBUS_TAG_ORDER_REDPOINT_HIDE = "order_redpoint_hide";
    public static final String EVENTBUS_TAG_BOTTOM_REDPOINT_SHOW = "bottom_redpoint_show";
    public static final String EVENTBUS_TAG_BOTTOM_REDPOINT_HIDE = "bottom_redpoint_hide";
    public static final String EVENTBUS_TAG_REDPOINT_VERSION_UPDATE_SHOW = "version_update_show";
    public static final String EVENTBUS_TAG_REDPOINT_VERSION_UPDATE_HIDE = "version_update_hide";

    public static final String EVENTBUS_TAG_MAIN_LOOP_WAIT_RECEIVE_START = "main_loop_wait_receive_start";
    public static final String EVENTBUS_TAG_MAIN_LOOP_WAIT_RECEIVE_STOP = "main_loop_wait_receive_stop";
    public static final String EVENTBUS_TAG_MAIN_LOOP_WAIT_ARRIVE_START = "main_loop_wait_arrive_start";
    public static final String EVENTBUS_TAG_MAIN_LOOP_WAIT_ARRIVE_STOP = "main_loop_wait_arrive_stop";
    public static final String EVENTBUS_TAG_MAIN_LOOP_RENEW_REPLY_START = "main_loop_renew_reply_start";
    public static final String EVENTBUS_TAG_MAIN_LOOP_RENEW_REPLY_STOP = "main_loop_renew_reply_stop";

    public static final String EVENTBUS_TAG_MAIN_TIME_TASK_START = "main_timetask_start";
    public static final String EVENTBUS_TAG_MAIN_TIME_TASK_STOP = "main_timetask_stop";
    public static final String EVENTBUS_TAG_MAIN_TIME_TASK_ORDER_REFRESH = "main_timetask_order_refresh";

    public static final String EVENTBUS_TAG_DOWNLOAD_PROGRESS = "download_tag_progress";

    //接诊人详情key
    public static final String KEY_WAITERINFO_DETAIL = "key_waiter_detail";
    //订单详情key
    public static final String KEY_ORDER_DETAIL = "key_order_detail";
    //订单id
    public static final String KEY_ORDER_ID = "key_order_id";

    public static final int MESSAGE_FRAGMENT_HEAD = 0;//消息列表第一个Item 订单提醒
    public static final int MESSAGE_FRAGMENT_MESSAGE = 1;//消息列表 普通消息


    //======================
    public static final String BEECLOUD_APPID = "dbc950ad-82c2-4a02-8f8b-2a2864efcaea";
    public static final String BEECLOUD_APPSCRET = "430da70b-2029-4d9c-af4d-93ca9efe89b9";
    public static final String BEECLOUD_WX_APPID = "wx5e6c5b15eb3f1b4b";
    public static final String BEECLOUD_PAY_ORDER_COMMON = "ORDER";
    public static final String BEECLOUD_PAY_ORDER_RENEW = "OVERTIME";


    public static final String UMENG_KEY = "5b921767f29d98749800001b";
    public static final String UMENG_SHARE_WX_APPID = "wx5e6c5b15eb3f1b4b";
    public static final String UMENG_SHARE_WX_APPSCRET = "b8735b5a2b76eb57857d422675105895";

    public static final String UMENG_MESSAGE_SECRET = "53243e245bafa7c856a5ba92831b5e50";
    public static final String UMENG_CHANNEL_UMENG = "umeng";
    public static final int UMENG_DEVICE_TYPE = UMConfigure.DEVICE_TYPE_PHONE;

    public static final String UMENG_XIAOMI_ID = "2882303761517868663";
    public static final String UMENG_XIAOMI_KEY = "5431786854663";
//    public static final String UMENG_XIAOMI_SECRET = "";

    public static final String UMENG_MEIZU_APPID = "1002092";
    public static final String UMENG_MEIZU_APPKEY = "3bcbf93f9fdd4241a5c8f35e90c9af3a";

    public static final String UMEGN_ALIAS_TYPE_WAITER = "WAITER";
    public static final String UMEGN_ALIAS_TYPE_MEMBER = "MEMBER";

    //=====umeng  message  key=====
    public static final String KEY_INTENT_FROM_NOTIF = "key_intent_isfromnotif";
    public static final String KEY_UMENG_ORDERID = "key_umeng_orderid";
    public static final String KEY_UMENG_PUSHMSG = "key_umeng_pushmsg";
    public static final String KEY_UMENG_IS_LOCAL = "key_umeng_islocal";
}
