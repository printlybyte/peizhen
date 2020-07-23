package com.yinfeng.wypzh.utils;

import com.yinfeng.wypzh.base.Constants;

import org.simple.eventbus.EventBus;

public class EventUtil {

    /**
     *
     * @param position 0 开始下载 100 下载完成 -1 下载出错
     */
    public static void downloadProgress(int position) {
        EventBus.getDefault().post(position, Constants.EVENTBUS_TAG_DOWNLOAD_PROGRESS);
    }

}
