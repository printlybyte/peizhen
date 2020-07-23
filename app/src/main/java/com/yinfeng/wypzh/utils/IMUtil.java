package com.yinfeng.wypzh.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.api.model.user.IUserInfoProvider;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.MixPushConfig;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.ui.MainActivity;

import java.util.List;


/**
 * @author Asen
 */
public class IMUtil {
    /**
     * @param context
     */
    public static void initIMSDK(Context context, LoginInfo imLogInfo) {
        Context mContext = context.getApplicationContext();
        SDKOptions imOption = options(mContext);
        NIMClient.init(mContext, imLogInfo, imOption);
        if (NIMUtil.isMainProcess(mContext)) {
            // 在主进程中初始化UI组件，判断所属进程方法请参见demo源码。
            NimUIKit.init(mContext);
        }
    }

    public static void logoutIM() {
        NIMClient.getService(AuthService.class).logout();
    }

    /**
     * IM 手动login
     *
     * @param callback
     */
    public static AbortableFuture<LoginInfo> loginIM(LoginInfo imLogInfo, RequestCallback<LoginInfo> callback) {
        AbortableFuture<LoginInfo> abortableFuture = NIMClient.getService(AuthService.class).login(imLogInfo);
        abortableFuture.setCallback(callback);
        return abortableFuture;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    public static LoginInfo loginInfo(Context context, String account, String token) {
        // 从本地读取上次登录成功时保存的用户登录信息
        com.yinfeng.wypzh.bean.UserInfo userInfo = SFUtil.getInstance().getUserInfo(context);
        boolean isComplete = userInfo.isComplete();
        if (isComplete && !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    // 如果返回值为 null，则全部使用默认参数。
    //配置参数列表说明，见下连接
    // https://github.com/netease-im/NIM_Android_UIKit/blob/master/documents/Uikit%E5%85%A8%E5%B1%80%E9%85%8D%E7%BD%AE%E9%A1%B9%E4%BB%8B%E7%BB%8D.md
    public static SDKOptions options(Context context) {
        final com.yinfeng.wypzh.bean.UserInfo mUserInfo = SFUtil.getInstance().getUserInfo(context);
        SDKOptions options = new SDKOptions();
        options.asyncInitSDK = true;
        options.reducedIM = true;
//        options.appKey = ""
        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = MainActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.mipmap.ic_launcher;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用采用默认路径作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        String sdkPath = FileUtils.initRootFilePath(context) + "/nim"; // 可以不设置，那么将采用默认路径
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = ContextUtils.getSreenWidth(context) / 3;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {

            @Override
            public UserInfo getUserInfo(String account) {
                String imAccount = mUserInfo.getimAccount();
                String imToken = mUserInfo.getImToken();
                boolean isComplete = mUserInfo.isComplete();
                final String userAccount = mUserInfo.getAccountId();
                final String name = mUserInfo.getName();
                final String profile = mUserInfo.getProfile();
                if (isComplete && !TextUtils.isEmpty(imAccount) && !TextUtils.isEmpty(imToken))
                    return new UserInfo() {
                        @Override
                        public String getAccount() {
                            return userAccount;
                        }

                        @Override
                        public String getName() {
                            return name;
                        }

                        @Override
                        public String getAvatar() {
                            return profile;
                        }
                    };
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String
                    sessionId, SessionTypeEnum sessionType) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String
                    sessionId) {
                return null;
            }
        }

        ;
        return options;
    }

    public static void initUiKit(Context context) {
//        NimUIKit.init(context.getApplicationContext());
    }

//    /**
//     * 配置 APP 保存图片/语音/文件/log等数据的目录
//     * 这里示例用SD卡的应用扩展存储目录
//     */
//   public static String getAppCacheDir(Context context) {
//        String storageRootPath = null;
//        try {
//            // SD卡应用扩展存储区(APP卸载后，该目录下被清除，用户也可以在设置界面中手动清除)，请根据APP对数据缓存的重要性及生命周期来决定是否采用此缓存目录.
//            // 该存储区在API 19以上不需要写权限，即可配置 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="18"/>
//            if (context.getExternalCacheDir() != null) {
//                storageRootPath = context.getExternalCacheDir().getCanonicalPath();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (TextUtils.isEmpty(storageRootPath)) {
//            // SD卡应用公共存储区(APP卸载后，该目录不会被清除，下载安装APP后，缓存数据依然可以被加载。SDK默认使用此目录)，该存储区域需要写权限!
//            storageRootPath = Environment.getExternalStorageDirectory() + "/" + DemoCache.getContext().getPackageName();
//        }
//
//        return storageRootPath;
//    }
}
