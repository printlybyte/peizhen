package com.yinfeng.wypzh.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.umeng.message.entity.UMessage;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.message.PushMessage;
import com.yinfeng.wypzh.receiver.NotificationBroadcast;
import com.yinfeng.wypzh.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Asen
 */
public class UmUtil {
    public static void showNotificationLocal(Context context, PushMessage msg) {
        int id = msg.getNotifId();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setContentTitle(msg.getTitle())
                .setContentText(msg.getText())
                .setTicker(msg.getTicker())
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.umeng_push_notification_default_small_icon)
                .setAutoCancel(true);
        Notification notification = mBuilder.getNotification();
        PendingIntent clickPendingIntent = getClickPendingIntentLocal(context, msg);
        PendingIntent dismissPendingIntent = getDismissPendingIntentLocal(context, msg);
        notification.deleteIntent = dismissPendingIntent;
        notification.contentIntent = clickPendingIntent;
        manager.notify(id, notification);
    }

    public static PendingIntent getDismissPendingIntentLocal(Context context, PushMessage msg) {
        Intent deleteIntent = new Intent();
        deleteIntent.setClass(context, NotificationBroadcast.class);
        deleteIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG, msg);
        deleteIntent.putExtra(
                NotificationBroadcast.EXTRA_KEY_ACTION,
                NotificationBroadcast.ACTION_DISMISS_LOCAL);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context,
                (int) (System.currentTimeMillis() + 1),
                deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return deletePendingIntent;
    }

    public static PendingIntent getClickPendingIntentLocal(Context context, PushMessage msg) {
        Intent clickIntent = new Intent();
        clickIntent.setClass(context, NotificationBroadcast.class);
        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_MSG, msg);
        clickIntent.putExtra(NotificationBroadcast.EXTRA_KEY_ACTION,
                NotificationBroadcast.ACTION_CLICK_LOCAL);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,
                (int) (System.currentTimeMillis()),
                clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return clickPendingIntent;
    }

//    public static PushMessage convertUmengMsgToPushMsg(UMessage uMessage) {
//        if (uMessage != null) {
//            try {
//                JSONObject rawJson = uMessage.getRaw();
////                String msg_id = rawJson.getString("msg_id");
//                JSONObject bodyJson = rawJson.getJSONObject("body");
//                JSONObject extraJson = rawJson.getJSONObject("extra");
//                String ticker = bodyJson.getString("ticker");
//                String text = bodyJson.getString("text");
//                String title = bodyJson.getString("title");
//
//                String orderID = extraJson.getString("orderID");
//                String pushType = extraJson.getString("pushType");
//
//                PushMessage pushMessage = new PushMessage();
//                pushMessage.setTicker(ticker);
//                pushMessage.setText(text);
//                pushMessage.setTitle(title);
//                pushMessage.setOrderID(orderID);
//                pushMessage.setPushType(pushType);
//                pushMessage.setNotifId(getPushNotifId(orderID));
//                return pushMessage;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

    public static PushMessage convertUmengMsgToPushMsg(UMessage uMessage) {
        if (uMessage != null) {
            try {
                JSONObject rawJson = uMessage.getRaw();
//                String msg_id = rawJson.getString("msg_id");
                JSONObject bodyJson = rawJson.getJSONObject("body");
                String ticker = bodyJson.getString("ticker");
                String text = bodyJson.getString("text");
                String title = bodyJson.getString("title");
                PushMessage pushMessage = new PushMessage();
                pushMessage.setTicker(ticker);
                pushMessage.setText(text);
                pushMessage.setTitle(title);
                pushMessage.setNotifId(999);
                if (rawJson.has("extra")){
                    JSONObject extraJson = rawJson.getJSONObject("extra");
                    if (extraJson.has("orderID")){
                        String orderID = extraJson.getString("orderID");
                        pushMessage.setOrderID(orderID);
                        pushMessage.setNotifId(getPushNotifId(orderID));
                    }
                    if (extraJson.has("pushType")){
                        String pushType = extraJson.getString("pushType");
                        pushMessage.setPushType(pushType);
                    }
                    if (extraJson.has("id")) {
                        String renewId = extraJson.getString("id");
                        pushMessage.setId(renewId);
                    }
                }
                return pushMessage;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static PushMessage assembleLocalPushMsg(String orderId, String title, String text) {
        PushMessage message = new PushMessage();
        message.setTicker(title);
        message.setText(text);
        message.setTitle(title);
        message.setOrderID(orderId);
        message.setNotifId(getLocalNotifId(orderId));
        return message;
    }

    public static int getLocalNotifId(String orderId) {
        return (orderId + "local").hashCode();
    }

    public static int getPushNotifId(String orderId) {
        return orderId.hashCode();
    }

    /**
     * 移除 通知
     *
     * @param context
     */
    public static void removeAllNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    public static void removeNotification(Context context, int notifId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notifId);
    }

    public static void goToMain(Context context, PushMessage message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//service 中开启 栈中无此类
        intent.putExtra(Constants.KEY_INTENT_FROM_NOTIF, true);
        intent.putExtra(Constants.KEY_UMENG_PUSHMSG, message);
        intent.putExtra(Constants.KEY_UMENG_IS_LOCAL, false);
        context.startActivity(intent);
    }
}
