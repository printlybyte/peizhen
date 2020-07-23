package com.yinfeng.wypzh.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.ui.MainActivity;

import org.simple.eventbus.EventBus;

import static com.yinfeng.wypzh.ui.MainActivity.KEY_NEED_SWITCH_POSITION;
import static com.yinfeng.wypzh.ui.MainActivity.KEY_SWITCH_ORDERFRAGMENT_POSITION;

/**
 * @author Asen
 */
public class OrderUtil {

    public static void getNoticeInMsg() {
        EventBus.getDefault().post("default_content", Constants.EVENTBUS_TAG_GET_MSG_NOTICE);
    }
    public static void waiterHasArrived(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_HAS_ARRIVED);
    }

    public static void addOrderWaitReceive(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_WAIT_RECEIVER);
    }

    public static void deleteOrderWaitReceive(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_WAIT_RECEIVER);

    }

    public static void addOrderWaitService(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_WAIT_SERVICE);

    }

    public static void deleteOrderWaitService(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_WAIT_SERVICE);
    }

    public static void addOrderServicing(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_SERVICING);
    }

    public static void deleteOrderServicing(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_SERVICING);
    }

    public static void addOrderWaitComment(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_WAIT_COMMENT);
    }

    public static void deleteOrderWaitComment(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_WAIT_COMMENT);
    }

    public static void addOrderComplete(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_COMPLETE);
    }

    public static void deleteOrderComplete(String orderId) {
        EventBus.getDefault().post(orderId, Constants.EVENTBUS_TAG_ORDER_REFRESH_COMPLETE);
    }

    /**
     * 主页面倒计时 定时刷新 订单子页面的倒数计时
     */
    public static void refreshOrderWaitServiceAndServicing() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_MAIN_TIME_TASK_ORDER_REFRESH);
    }

    /**
     * 开启主页面倒计时
     */
    public static void startTimeTaskMain() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_MAIN_TIME_TASK_START);
    }

    /**
     * 关闭主页面倒计时
     */
    public static void stopTimeTaskMain() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_MAIN_TIME_TASK_STOP);
    }

    /**
     * 开启主页面 等待接单订单列表轮询
     */
    public static void startLoopWaitReceive() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_MAIN_LOOP_WAIT_RECEIVE_START);

    }

    /**
     * 停止主页面 等待接单订单列表轮询
     */
    public static void stopLoopWaitReceive() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_MAIN_LOOP_WAIT_RECEIVE_STOP);
    }

    /**
     * 开启主页面 等待到达订单列表轮询
     */
    public static void startLoopWaitService() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_MAIN_LOOP_WAIT_ARRIVE_START);

    }

    /**
     * 停止主页面 等待到达订单列表轮询
     */
    public static void stopLoopWaitService() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_MAIN_LOOP_WAIT_ARRIVE_STOP);
    }

    /**
     * 开启主页面 延时等待回复订单列表轮询
     */
    public static void startLoopRenewReply() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_MAIN_LOOP_RENEW_REPLY_START);

    }

    /**
     * 停止主页面 延时等待回复订单列表轮询
     */
    public static void stopLoopRenewReply() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_MAIN_LOOP_RENEW_REPLY_STOP);
    }


    public static void toOrderListFragment(Context context, int orderFragmentPostion) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY_NEED_SWITCH_POSITION, 1);
        intent.putExtra(KEY_SWITCH_ORDERFRAGMENT_POSITION, orderFragmentPostion);
        context.startActivity(intent);
    }

}
