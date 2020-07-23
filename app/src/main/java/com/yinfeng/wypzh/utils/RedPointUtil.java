package com.yinfeng.wypzh.utils;

import com.yinfeng.wypzh.base.Constants;

import org.simple.eventbus.EventBus;

/**
 * @author Asen
 */
public class RedPointUtil {

    /**
     * 显示订单5个子页面顶部导航红点
     *
     * @param position
     */
    public static void showOrderDot(int position) {
        EventBus.getDefault().post(position, Constants.EVENTBUS_TAG_ORDER_REDPOINT_SHOW);
        if (position > 0) {
            hideOrderDot(position - 1);
        }
    }

    public static void hideOrderDot(int position) {
        EventBus.getDefault().post(position, Constants.EVENTBUS_TAG_ORDER_REDPOINT_HIDE);
    }

    /**
     * 显示主页面底部导航栏红点
     *
     * @param position
     */
    public static void showBottomDot(int position) {
        EventBus.getDefault().post(position, Constants.EVENTBUS_TAG_BOTTOM_REDPOINT_SHOW);
    }

    public static void hideBottomDot(int position) {
        EventBus.getDefault().post(position, Constants.EVENTBUS_TAG_BOTTOM_REDPOINT_HIDE);
    }

    public static void showUpdateVersionDot() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_REDPOINT_VERSION_UPDATE_SHOW);
    }

    public static void hideUpdateVersionDot() {
        EventBus.getDefault().post("default", Constants.EVENTBUS_TAG_REDPOINT_VERSION_UPDATE_HIDE);
    }
}
