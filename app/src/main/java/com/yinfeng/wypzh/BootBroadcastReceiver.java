package com.yinfeng.wypzh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yinfeng.wypzh.ui.SplashActivity;

/**
 * ============================================
 * 描  述：
 * 包  名：com.yinfeng.wypzh
 * 类  名：BootBroadcastReceiver
 * 创建人：liuguodong
 * 创建时间：2019/8/25 16:51
 * ============================================
 **/
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startActivity(new Intent(context, SplashActivity.class));
    }
}
