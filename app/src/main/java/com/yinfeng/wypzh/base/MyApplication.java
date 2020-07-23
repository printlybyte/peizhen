package com.yinfeng.wypzh.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.MixPushConfig;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.service.UmengNotificationService;
import com.yinfeng.wypzh.utils.IMUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.SFUtil;


import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import cn.beecloud.BeeCloud;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    private static MyApplication instance;
    private String token;//判断登录
    private boolean isComplete;//判断登录以及是否需要完善个人信息
    private String userAgentStr;//接口请求 header 公共参数

    static {//static 代码段可以防止内存泄露
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initRealm();
        initBeeCloudForPay();
        initUmeng();
        initIM();
        Hawk.init(this).build();

    }

    private void initUmeng() {
//        String token = ContextUtils.getToken(this);
//        boolean isComplete = ContextUtils.getCompleteSate(this);
//        if (TextUtils.isEmpty(token) || !isComplete) {
//            ContextUtils.closeUmengPush(this);
//            return;
//        }
//        UMConfigure.init(this, Constants.UMENG_KEY
//                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.init(this, Constants.UMENG_KEY, Constants.UMENG_CHANNEL_UMENG, Constants.UMENG_DEVICE_TYPE, Constants.UMENG_MESSAGE_SECRET);
        //分享
        PlatformConfig.setWeixin(Constants.UMENG_SHARE_WX_APPID, Constants.UMENG_SHARE_WX_APPSCRET);
        //豆瓣RENREN平台目前只能在服务器端配置
//        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
//        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
//        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
//        PlatformConfig.setAlipay("2015111700822536");
//        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
//        PlatformConfig.setPinterest("1439206");
//        PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
//        PlatformConfig.setDing("dingoalmlnohc0wggfedpk");
//        PlatformConfig.setVKontakte("5764965","5My6SNliAaLxEm3Lyd9J");
//        PlatformConfig.setDropbox("oz8v5apet3arcdy","h7p2pjbzkkxt02a");
//        PlatformConfig.setYnote("9c82bf470cba7bd2f1819b0ee26f86c6ce670e9b");

        //推送
//        InAppMessageManager.getInstance(this).setInAppMsgDebugMode(true);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //通知栏显示通知数目
//        mPushAgent.setDisplayNotificationNumber(1);
        //前台不显示
//        mPushAgent.setNotificaitonOnForeground(false);
        //注册推送服务，每次调用register方法都会回调该接口
        //SDK默认在“23:00”到“7:00”之间收到通知消息时不响铃
        mPushAgent.setNoDisturbMode(22, 0, 6, 30);
        //关闭免打扰模式：
//        mPushAgent.setNoDisturbMode(0, 0, 0, 0);

        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER); //声音
        mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SERVER);//呼吸灯
        mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SERVER);//振动
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                //如需手动获取device token，可以调用mPushAgent.getRegistrationId()方法（需在注册成功后调用）。
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
    }

    private void initIM() {
        UserInfo info = SFUtil.getInstance().getUserInfo(this);
        String account = info.getimAccount();
        String token = info.getImToken();
        LoginInfo loginInfo = IMUtil.loginInfo(this, account, token);
        IMUtil.initIMSDK(this, loginInfo);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getUserAgentStr() {
        return userAgentStr;
    }

    public void setUserAgentStr(String userAgentStr) {
        this.userAgentStr = userAgentStr;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    private void    initBeeCloudForPay() {
        BeeCloud.setAppIdAndSecret(Constants.BEECLOUD_APPID, Constants.BEECLOUD_APPSCRET);
    }

    private void initRealm() {
        Realm.init(this);
        //它会创建一个叫default.realm的文件, 放在Context.getFilesDir()的目录下.
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        //只存于内存
//        RealmConfiguration config = new RealmConfiguration.Builder().inMemory().build();
        Realm.setDefaultConfiguration(config);
//        Realm.getDefaultInstance()取到的就是这个默认配置对应的实例.
        //    /**
//     * 自定义realm配置
//     * 是可以有多个配置, 访问多个Realm实例的.
//     */
//    private void defineRealmConfig() {
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name("myrealm.realm")
//                .encryptionKey(getKey())
//                .schemaVersion(42)
//                .modules(new MySchemaModule())
//                .migration(new MyMigration())
//                .build();
//    }
    }

    public void cleanAllConfig() {
        token = null;
        isComplete = false;
        userAgentStr = null;
    }
}
