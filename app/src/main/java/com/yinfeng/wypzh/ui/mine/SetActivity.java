package com.yinfeng.wypzh.ui.mine;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UpdateVersionBean;
import com.yinfeng.wypzh.download.DownLoadManager;
import com.yinfeng.wypzh.http.LoginApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.login.FillInfoActivity;
import com.yinfeng.wypzh.ui.login.RegisterActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DataCleanManager;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.RedPointUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.BottomBarTab;
import com.yinfeng.wypzh.widget.TopBar;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.IllegalFormatCodePointException;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class SetActivity extends BaseActivity {
    private SmartRefreshLayout mSmartRefreshLayout;
    private TopBar mTopBar;
    private LinearLayout llUserInfo, llCleanCache, llAbout;
    private RelativeLayout rlCheckVersion;
    private TextView tvCheckVersion;
    private ImageView ivRedPoint;
    private Button btExit;
    private boolean isCleancaching = false;
    private boolean isCheckVersioning = false;
    private final String checkVersionStr = "检查更新";
    private final String downloadingStr = "正在下载...";

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("设置");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });

        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setEnableRefresh(false);

        llUserInfo = mRootView.findViewById(R.id.llUserInfo);
        llCleanCache = mRootView.findViewById(R.id.llCleanCache);
        llAbout = mRootView.findViewById(R.id.llAbout);
        rlCheckVersion = mRootView.findViewById(R.id.rlCheckVersion);
        tvCheckVersion = mRootView.findViewById(R.id.tvCheckVersion);
        ivRedPoint = mRootView.findViewById(R.id.ivRedPoint);
        btExit = mRootView.findViewById(R.id.btExit);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(llUserInfo).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        goToFillInfoActivity();
                    }
                });
        RxView.clicks(llCleanCache).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        doCleanCache();
                    }
                });
        RxView.clicks(llAbout).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        AboutAppActivity.activityStart(SetActivity.this);
                    }
                });
        RxView.clicks(rlCheckVersion).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        doCheckVersion(false);
                    }
                });
        RxView.clicks(btExit).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        showLogOutDialog();

                    }
                });
    }

    private void showLogOutDialog() {
        final MaterialDialog dialog = DialogHelper.getMaterialDialogQuick(this, "确认退出登录？");
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
                ContextUtils.kickOut(SetActivity.this);
                ContextUtils.logOut(SetActivity.this);
            }
        });
        dialog.show();
    }

    private void doCheckVersion(final boolean isJustCheck) {
        if (isCheckVersioning) {
            showLoadingDialog("正在检查版本..");
            return;
        }
        isCheckVersioning = true;
//        showLoadingDialog("正在检查版本..");
        LoginApi.getInstance().checkVersion()
                .compose(RxSchedulers.<Response<BaseBean<UpdateVersionBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<UpdateVersionBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<UpdateVersionBean>>(this) {
                    @Override
                    public void success(BaseBean<UpdateVersionBean> result) {
                        isCheckVersioning = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            UpdateVersionBean bean = result.getResult();
                            String versionCodeStr = bean.getVersionCode();
                            int versionCodeNet = Integer.parseInt(versionCodeStr);
                            int versionCodeLocal = ContextUtils.getLocalVersion(SetActivity.this);

                            if (versionCodeNet > versionCodeLocal) {
                                if (isJustCheck) {
                                    ivRedPoint.setVisibility(View.VISIBLE);
                                } else {
                                    if (DownLoadManager.getInstance().isDownLoading()) {
                                        ToastUtil.getInstance().showShort(SetActivity.this, "正在后台下载中，请耐心等待");
                                    } else {
                                        showDownLoadDialog(SetActivity.this, bean);
                                    }
                                }
                            } else {
                                if (!isJustCheck)
                                    ToastUtil.getInstance().showLong(SetActivity.this, "已是最新版本" + bean.getVersionName());
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isCheckVersioning = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    private void showDownLoadDialog(Context context, final UpdateVersionBean bean) {
        RedPointUtil.hideUpdateVersionDot();
        String title = "更新版本 " + bean.getVersionName();
        String content = "部分功能优化，解决了若干bug";
        if (!TextUtils.isEmpty(bean.getUpdateLog()))
            content = bean.getUpdateLog();
        final MaterialDialog downLoadDialog = DialogHelper.getDownLoadDialog(context, title, content);
        downLoadDialog.setCanceledOnTouchOutside(false);
        downLoadDialog.setCancelable(false);
        downLoadDialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        downLoadDialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        downLoadDialog.dismiss();
                        if (!DownLoadManager.getInstance().isDownLoading() && !TextUtils.isEmpty(bean.getDownLoadUrl())) {
//                            RedPointUtil.hideUpdateVersionDot();
                            DownLoadManager.getInstance().downLoadApk(SetActivity.this, bean.getDownLoadUrl());
                        }
                    }
                });
        downLoadDialog.show();
    }

    private void doCleanCache() {
        if (isCleancaching) {
            showLoadingDialog("正在清理缓存...");
            return;
        }
        showLoadingDialog("正在清理缓存...");
        isCleancaching = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String cacheNumb = DataCleanManager.getTotalCacheSize(SetActivity.this);
                DataCleanManager.clearAllCache(SetActivity.this);
                ImageUtil.getInstance().clearMemoryAndDiskCache(SetActivity.this);
                isCleancaching = false;
                hideLoadingDialog();
                if (TextUtils.isEmpty(cacheNumb)) {
                    ToastUtil.getInstance().showShort(SetActivity.this, "已清理完毕！");
                } else {
                    ToastUtil.getInstance().showShort(SetActivity.this, "已清理缓存" + cacheNumb);
                }
            }
        }, 1000);
    }

    private void goToFillInfoActivity() {
        FillInfoActivity.activityStart(SetActivity.this, true);
    }

    @Override
    protected void initData() {
        doCheckVersion(true);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_set;
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_REDPOINT_VERSION_UPDATE_SHOW, mode = ThreadMode.MAIN)
    private void showUpdateVersionRedPoint(String defaultContent) {
        LogUtil.error("EventBus showUpdateVersionRedPoint ");
        ivRedPoint.setVisibility(View.VISIBLE);
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_REDPOINT_VERSION_UPDATE_HIDE, mode = ThreadMode.MAIN)
    private void hideUpdateVersionRedPoint(String defaultContent) {
        LogUtil.error("EventBus hideUpdateVersionRedPoint ");
        ivRedPoint.setVisibility(View.INVISIBLE);
    }

    @Subscriber(tag = Constants.EVENTBUS_TAG_DOWNLOAD_PROGRESS, mode = ThreadMode.MAIN)
    private void downloadProgress(int progress) {
        if (progress>0&&progress<100){
            tvCheckVersion.setText(downloadingStr+progress+"%");
        }else{
            tvCheckVersion.setText(checkVersionStr);
        }
    }
}
