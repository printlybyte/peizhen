package com.yinfeng.wypzh.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.umeng.message.PushAgent;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.login.IMLogInfo;
import com.yinfeng.wypzh.http.LoginApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.dialog.PermissionTipDialog;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.IMUtil;
import com.yinfeng.wypzh.utils.NetUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ToastUtil;

import org.simple.eventbus.EventBus;

import io.realm.Realm;
import retrofit2.Response;

public abstract class BaseActivity extends SupportActivity {

    protected View mRootView;
    protected Dialog mLoadingDialog = null;
    protected Realm mRealm;

    private boolean isGetIMTokening = false;
    private boolean isGalleryOk = false;
    private boolean isCaptureOk = false;
    private boolean isAudio = false;
    private PermissionTipDialog permissionTipDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = getLayoutInflater().inflate(getContentLayout(), null);
        setContentView(mRootView);
        mLoadingDialog = DialogHelper.getLoadingDialog(this);
        bindView(mRootView, savedInstanceState);
        initData();
        setListener();
        initEventBus();
        PushAgent.getInstance(this).onAppStart();
    }

    protected abstract void bindView(View mRootView, Bundle savedInstanceState);

    protected abstract void setListener();

    protected abstract void initData();

    protected abstract int getContentLayout();

    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindToLifecycle();
    }

    protected void showLoadingDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.show();
    }

    protected void showLoadingDialog(String str) {
        if (mLoadingDialog != null) {
            TextView tv = (TextView) mLoadingDialog.findViewById(R.id.tv_load_dialog);
            tv.setText(str);
            mLoadingDialog.show();
        }
    }

    protected void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected void checkNetValidAndToast() {
        if (!NetUtil.isNetworkAvailable(this)) {
            ToastUtil.getInstance().showShort(this, "请检查网络设置");
        } else {
            ToastUtil.getInstance().showShort(this, "操作失败,请重试");
        }
    }

    protected void checkNetValidAndToast(int httpCode, int errCode, String errMsg) {
        if (!NetUtil.isNetworkAvailable(this)) {
            ToastUtil.getInstance().showShort(this, "请检查网络设置");
        } else {
            if (TextUtils.isEmpty(errMsg)) {
                ToastUtil.getInstance().showShort(this, "操作失败,请重试");
            } else {
                ToastUtil.getInstance().showShort(this, errMsg);
            }
        }
    }

    protected void initEventBus() {
        EventBus.getDefault().register(this);
    }

    protected void unregistEvenBus() {
        EventBus.getDefault().unregister(this);
    }

    protected void initRealm() {
        mRealm = Realm.getDefaultInstance();
    }

    protected void closeRealm() {
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
    }

    @Override
    protected void onDestroy() {
        closeRealm();
        unregistEvenBus();
        super.onDestroy();
    }

//    @SuppressLint("CheckResult")
//    protected void requestIMessagePermissions(final String account) {
//        {
//            RxPermissions rxPermission = new RxPermissions(this);
//            rxPermission.requestEach(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.RECORD_AUDIO
//            ).subscribe(new Consumer<Permission>() {
//                @Override
//                public void accept(Permission permission) throws Exception {
//                    if (permission.granted) {
//                        // 用户已经同意该权限
//                        if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
//                            isGalleryOk = true;
//                        }
//                        if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
//                            isCaptureOk = true;
//                        }
//                        if (TextUtils.equals(Manifest.permission.RECORD_AUDIO, permission.name)) {
//                            isAudio = true;
//                        }
//                        if (isCaptureOk && isGalleryOk && isAudio) {
////                                isCanIntoMsgPage = true;
//                            openMsg(account);
//                        }
//                    } else if (permission.shouldShowRequestPermissionRationale) {
//                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                    } else {
//                        // 用户拒绝了该权限，并且选中『不再询问』
//                        LogUtil.error("订单子页面", permission.name + " is denied.");
//                        String permissionName = "";
//                        if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
//                            permissionName = "相册";
//                        }
//                        if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
//                            permissionName = "摄像头";
//                        }
//                        if (TextUtils.equals(Manifest.permission.RECORD_AUDIO, permission.name)) {
//                            permissionName = "麦克风";
//                        }
//                        permissionTipDialog = DialogHelper.getPermissionTipDialog(BaseActivity.this, permissionName);
//                        permissionTipDialog.show();
//                    }
//                }
//            });
//
//
//        }
//
//    }

    protected void openMsg(String account) {
        if (checkIMCloudState()) {
            if (!TextUtils.isEmpty(account))
                NimUIKit.startP2PSession(this, account);
        } else {
            ToastUtil.getInstance().showShort(this, "消息服务器未登录");
        }

    }

    protected boolean checkIMCloudState() {
        UserInfo userInfo = SFUtil.getInstance().getUserInfo(this);
        String imAccount = userInfo.getimAccount();
        String imToken = userInfo.getImToken();
        if (TextUtils.isEmpty(imAccount) || TextUtils.isEmpty(imToken)) {
            doGetIcloudToken();
            return false;
        } else {
            StatusCode status = NIMClient.getStatus();
            if (status == StatusCode.LOGINED) {
                NimUIKit.setAccount(imAccount);
                return true;
            } else {
                if (status == StatusCode.LOGINING || status == StatusCode.SYNCING)
                    return false;
                LoginInfo mLogInfo = IMUtil.loginInfo(this, imAccount, imToken);
                doLogIMCloud(mLogInfo);
                return false;
            }

        }
    }

    private void doGetIcloudToken() {
        if (isGetIMTokening)
            return;
        isGetIMTokening = true;
        LoginApi.getInstance().getIMcloudLoginInfo()
                .compose(RxSchedulers.<Response<BaseBean<IMLogInfo>>>applySchedulers())
                .compose(this.<Response<BaseBean<IMLogInfo>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<IMLogInfo>>() {
                    @Override
                    public void success(BaseBean<IMLogInfo> result) {
                        isGetIMTokening = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            IMLogInfo logInfo = result.getResult();
                            LoginInfo mLogInfo = IMUtil.loginInfo(BaseActivity.this, logInfo.getAccid(), logInfo.getToken());
                            doLogIMCloud(mLogInfo);
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetIMTokening = false;
                    }
                });
    }

    private void doLogIMCloud(LoginInfo mLogInfo) {
        AbortableFuture abortableFuture = IMUtil.loginIM(mLogInfo, new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(final LoginInfo loginInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfo userInfo = SFUtil.getInstance().getUserInfo(BaseActivity.this);
                        userInfo.setimAccount(loginInfo.getAccount());
                        userInfo.setImToken(loginInfo.getToken());
                        SFUtil.getInstance().setUserInfo(BaseActivity.this, userInfo);
                    }
                });

            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }


}
