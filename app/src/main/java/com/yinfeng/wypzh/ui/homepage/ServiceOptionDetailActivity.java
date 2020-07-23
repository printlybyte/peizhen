package com.yinfeng.wypzh.ui.homepage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailBean;
import com.yinfeng.wypzh.bean.order.ServiceOptionDetailResult;
import com.yinfeng.wypzh.bean.order.ServiceTimeBean;
import com.yinfeng.wypzh.bean.order.ShareServiceDetail;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ShareUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.TopBar;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class ServiceOptionDetailActivity extends BaseActivity {

    private SmartRefreshLayout mSmartRefreshLayout;
    //    private ScrollView mScrollView;
    private TopBar mTopBar;
    private RelativeLayout rlNoNetData;
    private TextView tvRetry;
    private LinearLayout llLoading;
    private LinearLayout llGetNetData;
    //    private FrameLayout flWebViewContainer;
    private ImageView ivBanner;
    private TextView tvTitle, tvPrice, tvSubTitle, tvIntroduction;
    private TextView tvServiceDetail, tvQuestions;
    private LinearLayout llRetry;
    private Button btSubscribe;

    private ServiceOptionDetailBean mDetailBean;
    private String htmlDataServiceDetails;
    private String htmlDataQuestions;
    private boolean isDetailOption = true;
    private FragmentManager fragmentManager;
    private ServiceOptionDetailFragment detailFragment;
    private ServiceOptionQuestionFragment questionFragment;
    public static final String KEY_HTML_DATA = "key_htmldata";
    private String shareUrl;
    private String title = "无忧陪诊服务详情";
    private String desc = "无忧陪诊服务详情";

    private boolean isGetShareUrling = false;
    private boolean isShareClick = false;
    private boolean isGetServiceTimeSuccess = false;
    private ServiceTimeBean serviceTimeBean;
    private boolean isGetServiceTiming = false;


    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.setTopCenterTxt("服务详情");
        mTopBar.setTopRightTxt("分享");
        mTopBar.setTopBarBackListener(new TopBar.TopBarBackListener() {
            @Override
            public void topBack() {
                finish();
            }
        });

        mTopBar.setTopBarRightTxtListener(new TopBar.TopBarRightTextCickListener() {
            @Override
            public void topRightTxtClick() {
                if (TextUtils.isEmpty(shareUrl)) {
                    UserInfo mUserInfo = SFUtil.getInstance().getUserInfo(ServiceOptionDetailActivity.this);
                    String accountId = mUserInfo.getAccountId();
                    if (!TextUtils.isEmpty(accountId)) {
                        doGetShareUrl(accountId);
                    }
                } else {
                    doShare();
                }
            }
        });
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
//        mScrollView = mRootView.findViewById(R.id.mScrollView);
        ivBanner = mRootView.findViewById(R.id.ivBanner);
        rlNoNetData = mRootView.findViewById(R.id.rlNoNetData);
        tvRetry = mRootView.findViewById(R.id.tvRetry);
        llLoading = mRootView.findViewById(R.id.llLoading);
        llGetNetData = mRootView.findViewById(R.id.llGetNetData);
//        flWebViewContainer = mRootView.findViewById(R.id.flWebViewContainer);
        llRetry = mRootView.findViewById(R.id.llRetry);
        tvTitle = mRootView.findViewById(R.id.tvTitle);
        tvPrice = mRootView.findViewById(R.id.tvPrice);
        tvSubTitle = mRootView.findViewById(R.id.tvSubTitle);
        tvIntroduction = mRootView.findViewById(R.id.tvIntroduction);
        tvServiceDetail = mRootView.findViewById(R.id.tvServiceDetail);
        tvQuestions = mRootView.findViewById(R.id.tvQuestions);
        btSubscribe = mRootView.findViewById(R.id.btSubscribe);
        resetIvBannerParams();
    }

    private void doShare() {
        ShareUtil.shareUrlToWx(this, shareUrl, title, desc, new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }


            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                if (throwable != null)
                    ToastUtil.getInstance().showShort(ServiceOptionDetailActivity.this, throwable.getMessage());
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });
    }

    private void doGetShareUrl(String accountId) {
        if (isGetShareUrling) {
            showLoadingDialog();
            isShareClick = true;
            return;
        }
        isGetShareUrling = true;
        OrderApi.getInstance().getShareUrlServiceOption(accountId)
                .compose(RxSchedulers.<Response<BaseBean<ShareServiceDetail>>>applySchedulers())
                .compose(this.<Response<BaseBean<ShareServiceDetail>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<ShareServiceDetail>>() {
                    @Override
                    public void success(BaseBean<ShareServiceDetail> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ShareServiceDetail shareServiceDetail = result.getResult();
                            if (shareServiceDetail == null || TextUtils.isEmpty(shareServiceDetail.getUrl())) {
                                ToastUtil.getInstance().showShort(ServiceOptionDetailActivity.this, "分享链接为空");
                            } else {
                                shareUrl = shareServiceDetail.getUrl();
                                title = shareServiceDetail.getTitle();
                                desc = shareServiceDetail.getDesc();
                                if (mLoadingDialog != null && mLoadingDialog.isShowing() && isShareClick) {
                                    doShare();
                                }
                            }
                        } else {
                            ToastUtil.getInstance().showShort(ServiceOptionDetailActivity.this, result.getMessage());
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

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(btSubscribe).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        goToNextPage();
                    }
                });
        RxView.clicks(llRetry).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        llLoading.setVisibility(View.VISIBLE);
                        rlNoNetData.setVisibility(View.GONE);
                        doGetServiceDetail();
                        doGetServiceTimeStartAndEnd();
                    }
                });

        tvServiceDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDetailOption) {
                    isDetailOption = true;
                    tvServiceDetail.setBackgroundResource(R.drawable.shape_green_solid_lefttop);
                    tvServiceDetail.setTextColor(ContextCompat.getColor(ServiceOptionDetailActivity.this, android.R.color.white));
                    tvQuestions.setBackgroundResource(R.drawable.shape_grey_solid_righttop);
                    tvQuestions.setTextColor(ContextCompat.getColor(ServiceOptionDetailActivity.this, R.color.cb5b5b6));
                    clickShowDetailFrament();
                }
            }
        });
        tvQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDetailOption) {
                    isDetailOption = false;
                    tvServiceDetail.setBackgroundResource(R.drawable.shape_grey_solid_lefttop);
                    tvServiceDetail.setTextColor(ContextCompat.getColor(ServiceOptionDetailActivity.this, R.color.cb5b5b6));
                    tvQuestions.setBackgroundResource(R.drawable.shape_green_solid_righttop);
                    tvQuestions.setTextColor(ContextCompat.getColor(ServiceOptionDetailActivity.this, android.R.color.white));
                    clickShowQuestionFrament();
                }

            }
        });

    }

    private void goToNextPage() {
        if (serviceTimeBean == null) {
            doGetServiceTimeStartAndEnd();
            return;
        }

        if (mDetailBean == null)
            return;
        SubscribeOnlineActivity.activityStart(this, mDetailBean,serviceTimeBean);
        finish();
    }

    @Override
    protected void initData() {
        doGetServiceDetail();
        doGetServiceTimeStartAndEnd();
        UserInfo mUserInfo = SFUtil.getInstance().getUserInfo(ServiceOptionDetailActivity.this);
        String accountId = mUserInfo.getAccountId();
        if (!TextUtils.isEmpty(accountId)) {
            doGetShareUrl(accountId);
        }
    }

    private void doGetServiceDetail() {
        OrderApi.getInstance().getServiceOptionDetail("")
                .compose(RxSchedulers.<Response<ServiceOptionDetailResult>>applySchedulers())
                .compose(this.<Response<ServiceOptionDetailResult>>bindToLife())
                .subscribe(new BaseObserver<ServiceOptionDetailResult>(this) {
                    @Override
                    public void success(ServiceOptionDetailResult result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            mDetailBean = result.getResult();
                            resetView();
                        } else {
                            showRetry(result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        showRetry(errorMsg);
                    }
                });
    }

    private void doGetServiceTimeStartAndEnd() {
        if (isGetServiceTiming) {
            return;
        }
        isGetServiceTiming = true;
        OrderApi.getInstance().getServiceTime()
                .compose(RxSchedulers.<Response<BaseBean<ServiceTimeBean>>>applySchedulers())
                .compose(this.<Response<BaseBean<ServiceTimeBean>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<ServiceTimeBean>>(this) {
                    @Override
                    public void success(BaseBean<ServiceTimeBean> result) {
                        isGetServiceTiming = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ServiceTimeBean bean = result.getResult();
                            if (bean != null && !TextUtils.isEmpty(bean.getStart())) {
                                isGetServiceTimeSuccess = true;
                                serviceTimeBean = bean;
                            }
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetServiceTiming = false;
                    }
                });
    }

    private void resetView() {
        llLoading.setVisibility(View.GONE);
        llGetNetData.setVisibility(View.VISIBLE);
        mSmartRefreshLayout.setVisibility(View.VISIBLE);
        rlNoNetData.setVisibility(View.GONE);
        String imgUrl = mDetailBean.getImgUrl();
        String title = mDetailBean.getTitle();
        int price = mDetailBean.getPrice();
        String subTitle = mDetailBean.getSubTitle();
        String instroduction = mDetailBean.getIntroduction();
        htmlDataServiceDetails = mDetailBean.getServiceDetails();
        htmlDataQuestions = mDetailBean.getQuestion();

        ImageUtil.getInstance().loadImg(this, imgUrl, ivBanner);
        if (!TextUtils.isEmpty(title))
            tvTitle.setText(title);

        tvPrice.setText(ContextUtils.getPriceStrConvertFenToYuan(price));
        if (!TextUtils.isEmpty(subTitle))
            tvSubTitle.setText(subTitle);
        if (!TextUtils.isEmpty(instroduction))
            tvIntroduction.setText(instroduction);

        initDetailAndQuestionFragment();
    }

    private void initDetailAndQuestionFragment() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!(detailFragment != null && detailFragment.isAdded())) {
            Bundle detailBundle = new Bundle();
            detailBundle.putSerializable(KEY_HTML_DATA, htmlDataServiceDetails);
            detailFragment = new ServiceOptionDetailFragment();
            detailFragment.setArguments(detailBundle);
            transaction.add(R.id.flWebViewContainer, detailFragment);
        }
        if (!(questionFragment != null && questionFragment.isAdded())) {
            Bundle detailBundle = new Bundle();
            detailBundle.putSerializable(KEY_HTML_DATA, htmlDataQuestions);
            questionFragment = new ServiceOptionQuestionFragment();
            questionFragment.setArguments(detailBundle);
            transaction.add(R.id.flWebViewContainer, questionFragment);
        }
        if (isDetailOption) {
            transaction.show(detailFragment)
                    .hide(questionFragment)
                    .commitAllowingStateLoss();
        } else {
            transaction.show(questionFragment)
                    .hide(detailFragment)
                    .commitAllowingStateLoss();
        }
    }

    private void clickShowDetailFrament() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.show(detailFragment).hide(questionFragment).commitAllowingStateLoss();
    }

    private void clickShowQuestionFrament() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.show(questionFragment).hide(detailFragment).commitAllowingStateLoss();
    }

    private void resetIvBannerParams() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ivBanner.getLayoutParams();
        int screenWidth = ContextUtils.getSreenWidth(this);
        int targetHeight = screenWidth * 100 / 360;
        params.height = targetHeight;
        ivBanner.setLayoutParams(params);
    }

    private void showRetry(String content) {
        rlNoNetData.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(content))
            tvRetry.setText(content);
        llLoading.setVisibility(View.GONE);
        llGetNetData.setVisibility(View.INVISIBLE);
        mSmartRefreshLayout.setVisibility(View.INVISIBLE);

    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_service_option_detail;
    }

}
