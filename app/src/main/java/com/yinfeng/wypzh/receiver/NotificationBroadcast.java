package com.yinfeng.wypzh.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umeng.message.UTrack;
import com.umeng.message.common.Const;
import com.umeng.message.entity.UMessage;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.message.PushMessage;
import com.yinfeng.wypzh.service.UmNotifHandService;
import com.yinfeng.wypzh.ui.MainActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.OrderUtil;
import com.yinfeng.wypzh.utils.UmUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationBroadcast extends BroadcastReceiver {

    public static final String EXTRA_KEY_ACTION = "ACTION";
    public static final String EXTRA_KEY_MSG = "MSG";
    public static final int ACTION_CLICK = 10;
    public static final int ACTION_DISMISS = 11;
    public static final int ACTION_CLICK_LOCAL = 12;
    public static final int ACTION_DISMISS_LOCAL = 13;
    public static final int EXTRA_ACTION_NOT_EXIST = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
//        String message = intent.getStringExtra(EXTRA_KEY_MSG);
        PushMessage message = (PushMessage) intent.getSerializableExtra(EXTRA_KEY_MSG);
        int action = intent.getIntExtra(EXTRA_KEY_ACTION,
                EXTRA_ACTION_NOT_EXIST);
        switch (action) {
            case ACTION_DISMISS:
                LogUtil.error("推送消息 ACTION_DISMISS ");

                break;
            case ACTION_CLICK:
                LogUtil.error("推送消息 ACTION_CLICK ");
                UmUtil.goToMain(context, message);
                break;
            case ACTION_CLICK_LOCAL:
                LogUtil.error("推送消息 ACTION_CLICK_LOCAL ");

                break;
            case ACTION_DISMISS_LOCAL:
                LogUtil.error("推送消息 ACTION_DISMISS_LOCAL ");

                break;
        }
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String message = intent.getStringExtra(EXTRA_KEY_MSG);
//        int action = intent.getIntExtra(EXTRA_KEY_ACTION,
//                EXTRA_ACTION_NOT_EXIST);
//        try {
//            UMessage msg;
//            switch (action) {
//                case ACTION_DISMISS:
//                    msg = (UMessage) new UMessage(new JSONObject(message));
//                    Log.i(TAG, "dismiss notification");
//                    UTrack.getInstance(context).setClearPrevMessage(true);
//                    UTrack.getInstance(context).trackMsgDismissed(msg);
//                    break;
//                case ACTION_CLICK:
//                    msg = (UMessage) new UMessage(new JSONObject(message));
//                    Log.i(TAG, "click notification");
//                    UTrack.getInstance(context).setClearPrevMessage(true);
//                    UmNotifHandService.oldMessage = null;
//                    UTrack.getInstance(context).trackMsgClick(msg);
//                    break;
//                case ACTION_LOCAL:
//                    Log.i(TAG, "click notification local");
//
//                    break;
//            }
//            //
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
