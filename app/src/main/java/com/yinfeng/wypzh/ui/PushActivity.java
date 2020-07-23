package com.yinfeng.wypzh.ui;


import android.content.Intent;
import android.os.Bundle;

import com.umeng.message.UmengNotifyClickActivity;
import com.umeng.message.entity.UMessage;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.message.PushMessage;
import com.yinfeng.wypzh.ui.login.LoginActivity;
import com.yinfeng.wypzh.ui.login.SycCodeLoginActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.UmUtil;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Asen
 */
public class PushActivity extends UmengNotifyClickActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_push);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        LogUtil.error("推送消息 通知栏消息 " + body);
        try {
            UMessage msg = new UMessage(new JSONObject(body));
            PushMessage pushMessage = UmUtil.convertUmengMsgToPushMsg(msg);
            if (ContextUtils.isLogin(PushActivity.this)) {
                UmUtil.goToMain(this, pushMessage);
            }else{
                Intent toLoginIntent = new Intent(PushActivity.this, SycCodeLoginActivity.class);
                startActivity(toLoginIntent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
