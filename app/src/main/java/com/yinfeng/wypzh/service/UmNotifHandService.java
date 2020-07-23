package com.yinfeng.wypzh.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import com.google.gson.JsonObject;
import com.umeng.message.UTrack;
import com.umeng.message.entity.UMessage;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.message.PushMessage;
import com.yinfeng.wypzh.receiver.NotificationBroadcast;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.UmUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class UmNotifHandService extends Service {
    public UmNotifHandService() {
    }

    private static final String TAG = UmNotifHandService.class.getName();
//    public static UMessage oldMessage = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String message = intent.getStringExtra("UmengMsg");
        LogUtil.error("推送消息到达：" + message);
        try {
            UMessage msg = new UMessage(new JSONObject(message));
            PushMessage pushMessage = UmUtil.convertUmengMsgToPushMsg(msg);
//            if (oldMessage != null) {
//                UTrack.getInstance(getApplicationContext()).setClearPrevMessage(true);
//                UTrack.getInstance(getApplicationContext()).trackMsgDismissed(oldMessage);
//            }
//            showNotification(msg);
            if (pushMessage != null && ContextUtils.isLogin(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showNotifcationWithChannel(pushMessage);
                } else {
                    showNotification(pushMessage);
                }
                OrderUtil.getNoticeInMsg();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotifcationWithChannel(PushMessage msg) {
        {
            int notifId = msg.getNotifId();
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String channelId = "wypzhChannel";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "无忧陪诊";
            // 用户可以看到的通知渠道的描述
            String description = "接单提醒通知渠道";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 400, 500, 400, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            manager.createNotificationChannel(mChannel);

            Notification.Builder mBuilder = new Notification.Builder(this);
            mBuilder.setContentTitle(msg.getTitle())
                    .setContentText(msg.getText())
                    .setTicker(msg.getTicker())
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.umeng_push_notification_default_small_icon)
                    .setChannelId(channelId)
                    .setAutoCancel(true);
            Notification notification = mBuilder.build();
            PendingIntent clickPendingIntent = getClickPendingIntent(this, msg);
            PendingIntent dismissPendingIntent = getDismissPendingIntent(this, msg);
            notification.deleteIntent = dismissPendingIntent;
            notification.contentIntent = clickPendingIntent;
            notification.defaults = Notification.DEFAULT_ALL;
            manager.notify(notifId, notification);
        }
    }

    private void showNotification(PushMessage msg) {
        int id = msg.getNotifId();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setContentTitle(msg.getTitle())
                .setContentText(msg.getText())
                .setTicker(msg.getTicker())
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.umeng_push_notification_default_small_icon)
                .setAutoCancel(true);
        Notification notification = mBuilder.build();
        PendingIntent clickPendingIntent = getClickPendingIntent(this, msg);
        PendingIntent dismissPendingIntent = getDismissPendingIntent(this, msg);
        notification.deleteIntent = dismissPendingIntent;
        notification.contentIntent = clickPendingIntent;
        notification.defaults = Notification.DEFAULT_ALL;
        manager.notify(id, notification);
    }

    public PendingIntent getClickPendingIntent(Context context, PushMessage msg) {
        Intent clickIntent = new Intent();
        clickIntent.setClass(context, NotificationBroadcast.class);
        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG, msg);
        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_ACTION,
                NotificationBroadcast.ACTION_CLICK);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,
                (int) (System.currentTimeMillis()),
                clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        return clickPendingIntent;
    }

    public PendingIntent getDismissPendingIntent(Context context, PushMessage msg) {
        Intent deleteIntent = new Intent();
        deleteIntent.setClass(context, NotificationBroadcast.class);
        deleteIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG, msg);
        deleteIntent.putExtra(
                NotificationBroadcast.EXTRA_KEY_ACTION,
                NotificationBroadcast.ACTION_DISMISS);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context,
                (int) (System.currentTimeMillis() + 1),
                deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return deletePendingIntent;
    }
//    private void showNotification(UMessage msg) {
//        JSONObject jsonObject = msg.getRaw();
//        LogUtil.error("推送消息到达：" + jsonObject.toString());
//        int id = new Random(System.nanoTime()).nextInt();
//        oldMessage = msg;
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancelAll();
//        Notification.Builder mBuilder = new Notification.Builder(this);
//        mBuilder.setContentTitle(msg.title)
//                .setContentText(msg.text)
//                .setTicker(msg.ticker)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.umeng_push_notification_default_small_icon)
//                .setAutoCancel(true);
//        Notification notification = mBuilder.getNotification();
//        PendingIntent clickPendingIntent = getClickPendingIntent(this, msg);
//        PendingIntent dismissPendingIntent = getDismissPendingIntent(this, msg);
//        notification.deleteIntent = dismissPendingIntent;
//        notification.contentIntent = clickPendingIntent;
//        manager.notify(id, notification);
//    }
//
//    public PendingIntent getClickPendingIntent(Context context, UMessage msg) {
//        Intent clickIntent = new Intent();
//        clickIntent.setClass(context, NotificationBroadcast.class);
//        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG,
//                msg.getRaw().toString());
//        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_ACTION,
//                NotificationBroadcast.ACTION_CLICK);
//        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,
//                (int) (System.currentTimeMillis()),
//                clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        return clickPendingIntent;
//    }
//
//    public PendingIntent getDismissPendingIntent(Context context, UMessage msg) {
//        Intent deleteIntent = new Intent();
//        deleteIntent.setClass(context, NotificationBroadcast.class);
//        deleteIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG,
//                msg.getRaw().toString());
//        deleteIntent.putExtra(
//                NotificationBroadcast.EXTRA_KEY_ACTION,
//                NotificationBroadcast.ACTION_DISMISS);
//        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context,
//                (int) (System.currentTimeMillis() + 1),
//                deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        return deletePendingIntent;
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
