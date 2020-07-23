package com.yinfeng.wypzh.http.common;

import android.accounts.Account;

/**
 * 请求接口 通用字段
 */
public class ApiContents {
    /**
     * 请求成功码
     */
    public static final int CODE_REQUEST_SUCCESS = 200;
    public static final int CODE_REQUEST_DO_ORDER_ERR = 500;
    /**
     * oss token过期 图片上传
     */
    public static final int CODE_REQUEST_INVALID = 701;

    /**
     * 账号异常
     */
    public static final int CODE_REQUEST_ACCOUNT_EXCEPTION = 601;

    /**
     * 权限 不足
     */
    public static final int INSUFFICIENT_PERMISSIONS = 501;


    /**
     * 没有会员信息
     */
    public static final int CODE_REQUEST_NO_MEMBER_INFORMATION = 602;


    public static final int CODE_TOKEN_INVALID = 601;//TOKEN 失效
    //    public static final String BASE_URL = "http://192.168.16.59:8011/admin/";
    public static final String BASE_URL = "http://api.peizhen.yinfengnet.cn/admin/";
    public static final String BASE_URL_AUTH = "http://192.168.1.143:8011/admin/";

    public static final String USER_TYPE = "MEMBER";//陪诊员取消值为：WAITER 会员取消值为：MEMBER

    public static final String ORDER_EVESTATE_WAIT = "WAIT";//订单评论状态
    public static final String ORDER_EVESTATE_COMPLETE = "COMPLETE";//订单评论状态

    public static final int CODE_DENY_CANCLE_REPEAT = 502;//(502,"不能重复取消")
    public static final int CODE_DENY_CANCLE_SERVICING = 501;//(501,"订单服务中，不允许取消")
    public static final int CODE_DENY_CANCLE_COMPLETE = 503;//(503,"订单已完成，不允许取消")


    public static final String CONTENT_TYPE_JSON = "application/json";

}
