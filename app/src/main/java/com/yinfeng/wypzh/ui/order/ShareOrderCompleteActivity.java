package com.yinfeng.wypzh.ui.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.order.ShareServiceDetail;
import com.yinfeng.wypzh.bean.order.ShareVoucher;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.homepage.ServiceOptionDetailActivity;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ShareUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class ShareOrderCompleteActivity extends BaseActivity {
    private static final String KEY_ISAFTER_ORDERCOMPLETE = "key_isafterorder";
    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    //    private ImageView ivWXhy, ivWXpyq, ivQQhy;
    private ImageView ivWXhy, ivWXpyq;
    private boolean isGetShareUrling = false;
    private boolean isShareClick = false;
    private boolean isWxHy = true;
    private String shareUrl, title, desc;
    private boolean isOrderComplete = false;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        getIntentData();
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("分享赢免费陪诊");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });

        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
        ivWXhy = mRootView.findViewById(R.id.ivWXhy);
        ivWXpyq = mRootView.findViewById(R.id.ivWXpyq);
//        ivQQhy = mRootView.findViewById(R.id.ivQQhy);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(ivWXhy).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        isWxHy = true;
                        if (TextUtils.isEmpty(shareUrl)) {
                            UserInfo mUserInfo = SFUtil.getInstance().getUserInfo(ShareOrderCompleteActivity.this);
                            String accountId = mUserInfo.getAccountId();
                            if (!TextUtils.isEmpty(accountId)) {
                                if (isOrderComplete) {
                                    doGetShareUrlAfterOrder(accountId);
                                } else {
                                    doGetShareUrlComment(accountId);
                                }
                            }
                        } else {
                            doShare();
                        }
                    }
                });
        RxView.clicks(ivWXpyq).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        isWxHy = false;
                        if (TextUtils.isEmpty(shareUrl)) {
                            UserInfo mUserInfo = SFUtil.getInstance().getUserInfo(ShareOrderCompleteActivity.this);
                            String accountId = mUserInfo.getAccountId();
                            if (!TextUtils.isEmpty(accountId)) {
                                if (isOrderComplete) {
                                    doGetShareUrlAfterOrder(accountId);
                                } else {
                                    doGetShareUrlComment(accountId);
                                }
                            }
                        } else {
                            doShare();
                        }
                    }
                });
//        RxView.clicks(ivQQhy).
//                throttleFirst(500, TimeUnit.MILLISECONDS)
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) {
//
//                    }
//                });
    }

    private void doGetShareUrlComment(String accountId) {
        if (isGetShareUrling) {
            showLoadingDialog();
            isShareClick = true;
            return;
        }
        isGetShareUrling = true;
        OrderApi.getInstance().getShareUrlServiceOption(accountId)
                .compose(RxSchedulers.<Response<BaseBean<ShareServiceDetail>>>applySchedulers())
                .compose(this.<Response<BaseBean<ShareServiceDetail>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<ShareServiceDetail>>(this) {
                    @Override
                    public void success(BaseBean<ShareServiceDetail> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ShareServiceDetail shareServiceDetail = result.getResult();
                            if (shareServiceDetail == null || TextUtils.isEmpty(shareServiceDetail.getUrl())) {
                                ToastUtil.getInstance().showShort(ShareOrderCompleteActivity.this, "分享链接为空");
                            } else {
                                shareUrl = shareServiceDetail.getUrl();
                                title = shareServiceDetail.getTitle();
                                desc = shareServiceDetail.getDesc();
                                if (mLoadingDialog != null && mLoadingDialog.isShowing() && isShareClick) {
                                    doShare();
                                }
                            }
                        } else {
                            ToastUtil.getInstance().showShort(ShareOrderCompleteActivity.this, result.getMessage());
                        }
                        isGetShareUrling = false;
                        hideLoadingDialog();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetShareUrling = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });

    }


    private void doGetShareUrlAfterOrder(String accountId) {
        if (isGetShareUrling) {
            showLoadingDialog();
            isShareClick = true;
            return;
        }
        isGetShareUrling = true;
        OrderApi.getInstance().getShareUrlAfterComplete(accountId)
                .compose(RxSchedulers.<Response<BaseBean<ShareVoucher>>>applySchedulers())
                .compose(this.<Response<BaseBean<ShareVoucher>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<ShareVoucher>>(this) {
                    @Override
                    public void success(BaseBean<ShareVoucher> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ShareVoucher shareVoucher = result.getResult();
                            if (shareVoucher == null || TextUtils.isEmpty(shareVoucher.getUrl())) {
                                ToastUtil.getInstance().showShort(ShareOrderCompleteActivity.this, "分享链接为空");
                            } else {
                                shareUrl = shareVoucher.getUrl();
                                title = shareVoucher.getTitle();
                                desc = shareVoucher.getDesc();
                                if (mLoadingDialog != null && mLoadingDialog.isShowing() && isShareClick) {
                                    doShare();
                                }
                            }
                        } else {
                            ToastUtil.getInstance().showShort(ShareOrderCompleteActivity.this, result.getMessage());
                        }
                        isGetShareUrling = false;
                        hideLoadingDialog();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetShareUrling = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });

    }

    private void doShare() {
        if (isWxHy) {
            ShareUtil.shareUrlToWxHy(ShareOrderCompleteActivity.this, shareUrl, title, desc, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {

                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    if (throwable != null)
                        ToastUtil.getInstance().showShort(ShareOrderCompleteActivity.this, throwable.getMessage());
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {

                }
            });
        } else {
            ShareUtil.shareUrlToWxCircle(ShareOrderCompleteActivity.this, shareUrl, title, desc, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {

                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    if (throwable != null)
                        ToastUtil.getInstance().showShort(ShareOrderCompleteActivity.this, throwable.getMessage());
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {

                }
            });
        }
    }


    @Override
    protected void initData() {
        UserInfo mUserInfo = SFUtil.getInstance().getUserInfo(ShareOrderCompleteActivity.this);
        String accountId = mUserInfo.getAccountId();
        if (!TextUtils.isEmpty(accountId)) {
            if (isOrderComplete) {
                doGetShareUrlAfterOrder(accountId);
            } else {
                doGetShareUrlComment(accountId);
            }

        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_share_order_complete;
    }

    public static void activityStart(Context context, boolean isOrderComplete) {
        Intent intent = new Intent(context, ShareOrderCompleteActivity.class);
        intent.putExtra(KEY_ISAFTER_ORDERCOMPLETE, isOrderComplete);
        context.startActivity(intent);
    }

    private void getIntentData() {
        isOrderComplete = getIntent().getBooleanExtra(KEY_ISAFTER_ORDERCOMPLETE, true);
    }
}
