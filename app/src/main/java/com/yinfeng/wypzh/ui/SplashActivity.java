package com.yinfeng.wypzh.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.inapp.InAppMessageManager;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.service.UmengNotificationService;
import com.yinfeng.wypzh.ui.login.LoginActivity;
import com.yinfeng.wypzh.ui.login.SycCodeLoginActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.SFUtil;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import cn.beecloud.BeeCloud;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashLauncher);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (SFUtil.getInstance().getIsFirstOpen(this)) {
            toGuide();
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
//                String token = ContextUtils.getToken(SplashActivity.this);
//                if (!TextUtils.isEmpty(token)) {
                    if (ContextUtils.isLogin(SplashActivity.this)) {
//                    initRealm();
//                    initBeeCloudForPay();
//                    initUmeng();
                        toMain();
                    } else {
                        toLogin();
                    }
                }
            }, 600);
        }

        //创建线程池 ThreadPoolExecutor 速度快，内存小
        //执行Application  init 中的耗时操作 在splash显示的这几秒的时间里
    }

    private void initBeeCloudForPay() {
        BeeCloud.setAppIdAndSecret(Constants.BEECLOUD_APPID, Constants.BEECLOUD_APPSCRET);
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }

    private void initUmeng() {
        UMConfigure.init(this, Constants.UMENG_KEY, Constants.UMENG_CHANNEL_UMENG, Constants.UMENG_DEVICE_TYPE, Constants.UMENG_MESSAGE_SECRET);
        InAppMessageManager.getInstance(this).setInAppMsgDebugMode(true);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setNoDisturbMode(22, 0, 6, 30);
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER); //声音
        mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SERVER);//呼吸灯
        mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SERVER);//振动
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                LogUtil.error("MyApplication umeng 注册成功 devicetoken :" + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtil.error("MyApplication umeng 注册失败" + s + "  |  " + s1);
            }
        });

        //魅族通道
        MeizuRegister.register(this, Constants.UMENG_MEIZU_APPID, Constants.UMENG_MEIZU_APPKEY);
        //HUAWEI
        HuaWeiRegister.register(this);
//        XIAOMI
        MiPushRegistar.register(this, Constants.UMENG_XIAOMI_ID, Constants.UMENG_XIAOMI_KEY);

        mPushAgent.setPushIntentServiceClass(UmengNotificationService.class);

        //统计
//        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        MobclickAgent.setCatchUncaughtExceptions(true);
    }

    private void toMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void toLogin() {
//        Intent intent = new Intent(this, LoginActivity.class);
        Intent intent = new Intent(this, SycCodeLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void toGuide() {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
        finish();
    }
}
