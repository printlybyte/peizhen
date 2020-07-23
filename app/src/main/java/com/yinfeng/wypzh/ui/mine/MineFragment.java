package com.yinfeng.wypzh.ui.mine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseFragment;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.login.UserInfoNetResult;
import com.yinfeng.wypzh.bean.order.ShareServiceDetail;
import com.yinfeng.wypzh.bean.order.ShareVoucher;
import com.yinfeng.wypzh.bean.patient.PatientInfo;
import com.yinfeng.wypzh.http.LoginApi;
import com.yinfeng.wypzh.http.OrderApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.dialog.CaptureAndGalleryDialog;
import com.yinfeng.wypzh.ui.dialog.PermissionTipDialog;
import com.yinfeng.wypzh.ui.homepage.ServiceOptionDetailActivity;
import com.yinfeng.wypzh.ui.login.FillInfoActivity;
import com.yinfeng.wypzh.ui.login.UserAgrentActivity;
import com.yinfeng.wypzh.ui.order.ShareOrderCompleteActivity;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.DialogHelper;
import com.yinfeng.wypzh.utils.ImageUtil;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.ShareUtil;
import com.yinfeng.wypzh.utils.ToastUtil;
import com.yinfeng.wypzh.widget.TopBar;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import retrofit2.Response;


public class MineFragment extends BaseFragment implements View.OnClickListener{
    private TopBar mTopBar;
    private SmartRefreshLayout mSmartRefreshLayout;
    private ImageView ivHeadIcon;
    private TextView tvName, policy, protocol;
    private ImageView ivLevel;
    //    private LinearLayout llWallet, llInvite, llCoupon, llSet;
    private LinearLayout llInvite, llCoupon;
    private RelativeLayout rlSet;
    private ImageView ivRedPoint;

    PermissionTipDialog permissionTipDialog;
    CaptureAndGalleryDialog captureAndGalleryDialog;
    CaptureAndGalleryDialog.CaptureAndGallerySelectListener photoListener;
    boolean isGalleryOk = false;
    boolean isCaptureOk = false;
    private String headIconImgName;
    private String headIconImgPath;
    private static final int REQUESTCODE_CAPTURE = 0x01;
    private static final int REQUESTCODE_GALLERY = 0x02;
    RequestOptions imageOptions;
    private boolean isGetUserInfoing = false;

    private String shareUrl;
    private String title = "无忧陪诊邀请券";
    private String desc = "无忧陪诊邀请券";
    private boolean isGetShareUrling = false;
    private boolean isShareClick = false;

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindView(View view, Bundle savedInstanceState) {
        mTopBar = mRootView.findViewById(R.id.topbar);
        mTopBar.hideTopBack();
        mTopBar.setTopCenterTxt("个人中心");
        mSmartRefreshLayout = mRootView.findViewById(R.id.mSmartRefreshLayout);
        ivHeadIcon = mRootView.findViewById(R.id.ivHeadIcon);
        tvName = mRootView.findViewById(R.id.tvName);
        ivLevel = mRootView.findViewById(R.id.ivLevel);
//        llWallet = mRootView.findViewById(R.id.llWallet);
        llInvite = mRootView.findViewById(R.id.llInvite);
        llCoupon = mRootView.findViewById(R.id.llCoupon);
        rlSet = mRootView.findViewById(R.id.rlSet);
        ivRedPoint = mRootView.findViewById(R.id.ivRedPoint);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setEnableRefresh(false);

        protocol = mRootView.findViewById(R.id.activity_login2_user_protocol);
        policy = mRootView.findViewById(R.id.activity_login2_privacy_policy);
        protocol.setOnClickListener(this);
        policy.setOnClickListener(this);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
//
//        RxView.clicks(llWallet).
//                throttleFirst(500, TimeUnit.MILLISECONDS)
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) {
//
//                    }
//                });
        RxView.clicks(llInvite).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
//                        if (TextUtils.isEmpty(shareUrl)) {
//                            UserInfo mUserInfo = SFUtil.getInstance().getUserInfo(getActivity());
//                            String accountId = mUserInfo.getAccountId();
//                            if (!TextUtils.isEmpty(accountId)) {
//                                doGetShareUrl(accountId);
//                            }
//                        } else {
//                            doShare();
//                        }
                        ShareOrderCompleteActivity.activityStart(getActivity(), false);
                    }
                });
        RxView.clicks(llCoupon).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        FreeVoucherActivity.activityStart(getActivity());
                    }
                });
        RxView.clicks(rlSet).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        Intent intent = new Intent(getActivity(), SetActivity.class);
                        startActivity(intent);
                    }
                });
        RxView.clicks(ivHeadIcon).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
//                        requestPermissions();
                    }
                });


    }

    @Override
    protected void initData() {
        headIconImgName = "wypzheader.jpeg";
        headIconImgPath = ContextUtils.getImgPath(headIconImgName);

        imageOptions = ImageUtil.getInstance().getDefineOptions(80, R.drawable.head_default);
        imageOptions.circleCrop();
//        imageOptions.skipMemoryCache(true);
        imageOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
//        String path = SFUtil.getInstance().getUserHeadLastImgPath(getActivity());
//        if (!TextUtils.isEmpty(path)) {
//            ImageUtil.getInstance().loadImg(getActivity(), path, imageOptions, ivHeadIcon);
//        }
        initListener();

        UserInfo userInfo = SFUtil.getInstance().getUserInfo(getActivity());
        String path = userInfo.getProfile();
        String name = userInfo.getName();
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(name)) {
//            doGetUserInfo();
        } else {
//            ImageUtil.getInstance().loadImg(getActivity(), path, imageOptions, ivHeadIcon);
            ImageUtil.getInstance().loadImgCircle(getActivity(), path, ivHeadIcon);
            tvName.setText(name);
        }
        initLevelIcon(userInfo.getLevel());
        UserInfo mUserInfo = SFUtil.getInstance().getUserInfo(getActivity());
        String accountId = mUserInfo.getAccountId();
        if (!TextUtils.isEmpty(accountId)) {
            doGetShareUrl(accountId);
        }
        doGetUserInfo();
    }

    private void initLevelIcon(String level) {
        ivLevel.setImageResource(R.drawable.level_putong);
        if (!TextUtils.isEmpty(level)) {
            if (TextUtils.equals(level, "普通")) {
                ivLevel.setImageResource(R.drawable.level_putong);
            }
            if (TextUtils.equals(level, "白银")) {
                ivLevel.setImageResource(R.drawable.level_baiyin);
            }
            if (TextUtils.equals(level, "黄金")) {
                ivLevel.setImageResource(R.drawable.level_huangjin);
            }
            if (TextUtils.equals(level, "钻石")) {
                ivLevel.setImageResource(R.drawable.level_zuanshi);
            }
        }

    }


    private void doGetUserInfo() {
        if (isGetUserInfoing) {
            return;
        }
        isGetUserInfoing = true;
        LogUtil.error("MineFragment getUserInfo ... ");
        LoginApi.getInstance().getUserInfo()
                .compose(RxSchedulers.<Response<BaseBean<UserInfoNetResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<UserInfoNetResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<UserInfoNetResult>>() {
                    @Override
                    public void success(BaseBean<UserInfoNetResult> result) {
                        isGetUserInfoing = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            UserInfoNetResult uInfo = result.getResult();
                            PatientInfo pInfo = uInfo.getMemberPatient();

                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(getActivity());
                            userInfo.setName(uInfo.getName());
                            userInfo.setSex(uInfo.getSex());
                            userInfo.setLevel(uInfo.getLevel());
                            userInfo.setProfile(uInfo.getProfile());
                            userInfo.setIdcard(pInfo.getIdcard());
                            userInfo.setIsHistory(pInfo.getIsHistory());
                            userInfo.setMedicalHistory(pInfo.getMedicalHistory());
                            userInfo.setOtherMedical(pInfo.getOtherMedical());
                            SFUtil.getInstance().setUserInfo(getActivity(), userInfo);
//                            ImageUtil.getInstance().loadImg(getActivity(), uInfo.getProfile(), imageOptions, ivHeadIcon);
                            ImageUtil.getInstance().loadImgCircle(getActivity(), uInfo.getProfile(), ivHeadIcon);
                            tvName.setText(uInfo.getName());
                            initLevelIcon(uInfo.getLevel());
                        }

                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isGetUserInfoing = false;
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
        OrderApi.getInstance().getShareUrlAfterComplete(accountId)
                .compose(RxSchedulers.<Response<BaseBean<ShareVoucher>>>applySchedulers())
                .compose(this.<Response<BaseBean<ShareVoucher>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<ShareVoucher>>() {
                    @Override
                    public void success(BaseBean<ShareVoucher> result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ShareVoucher shareVoucher = result.getResult();
                            if (shareVoucher == null || TextUtils.isEmpty(shareVoucher.getUrl())) {
                                ToastUtil.getInstance().showShort(getActivity(), "分享链接为空");
                            } else {
                                shareUrl = shareVoucher.getUrl();
                                title = shareVoucher.getTitle();
                                desc = shareVoucher.getDesc();
                                if (mLoadingDialog != null && mLoadingDialog.isShowing() && isShareClick) {
                                    doShare();
                                }
                            }
                        } else {
                            ToastUtil.getInstance().showShort(getActivity(), result.getMessage());
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
        ShareUtil.shareUrlToWx(getActivity(), shareUrl, title, desc, new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                if (throwable != null)
                    ToastUtil.getInstance().showShort(getActivity(), throwable.getMessage());
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });
    }

    @SuppressLint("CheckResult")
//    private void requestPermissions() {
//        RxPermissions rxPermission = new RxPermissions(getActivity());
//        rxPermission
//                .requestEach(
////                        Manifest.permission.ACCESS_FINE_LOCATION,
////                        Manifest.permission.READ_CALENDAR,
////                        Manifest.permission.READ_CALL_LOG,
////                        Manifest.permission.READ_CONTACTS,
////                        Manifest.permission.READ_PHONE_STATE,
////                        Manifest.permission.READ_SMS,
////                        Manifest.permission.RECORD_AUDIO,
////                        Manifest.permission.CALL_PHONE,
////                        Manifest.permission.SEND_SMS,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.CAMERA
//                )
//                .subscribe(new Consumer<Permission>() {
//                    @Override
//                    public void accept(Permission permission) throws Exception {
//                        if (permission.granted) {
//                            // 用户已经同意该权限
//                            if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
//                                isGalleryOk = true;
//                            }
//                            if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
//                                isCaptureOk = true;
//                            }
//                            if (isCaptureOk && isGalleryOk) {
//                                captureAndGalleryDialog = DialogHelper.getGalleryAndCaptureDialog(getActivity(), photoListener);
//                                captureAndGalleryDialog.show();
//                                isGalleryOk = false;
//                                isCaptureOk = false;
//                            }
//                        } else if (permission.shouldShowRequestPermissionRationale) {
//                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                        } else {
//                            // 用户拒绝了该权限，并且选中『不再询问』
//                            String permissionName = "";
//                            if (TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.name)) {
//                                permissionName = "相册";
//                            }
//                            if (TextUtils.equals(Manifest.permission.CAMERA, permission.name)) {
//                                permissionName = "摄像头";
//                            }
//                            permissionTipDialog = DialogHelper.getPermissionTipDialog(getActivity(), permissionName);
//                            permissionTipDialog.show();
//                        }
//                    }
//                });
//
//
//    }

    private void initListener() {
//        photoListener = new CaptureAndGalleryDialog.CaptureAndGallerySelectListener() {
//
//            @Override
//            public void selectCapture() {
//                ContextUtils.takePhoto(MineFragment.this, headIconImgName, REQUESTCODE_CAPTURE);
//            }
//
//            @Override
//            public void selectGallery() {
//                ContextUtils.choosePhoto(MineFragment.this, REQUESTCODE_GALLERY);
//            }
//        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri imageUri = null;
            if (requestCode == REQUESTCODE_CAPTURE) {
                imageUri = ContextUtils.getImgUri(getActivity(), headIconImgPath);
            }
            if (requestCode == REQUESTCODE_GALLERY) {
                imageUri = data.getData();
            }
            ImageUtil.getInstance().loadImg(getActivity(), imageUri, imageOptions, ivHeadIcon);
            ImageUtil.getInstance().getCacheFilePath(getActivity(), imageUri, ivHeadIcon.getWidth(), ivHeadIcon.getHeight(), null);
        }
    }

    // 含有my_tag,当用户post事件时,只有指定了"my_tag"的事件才会触发该函数,
    // post函数在哪个线程执行,该函数就执行在哪个线程
    @Subscriber(tag = Constants.EVENTBUS_TAG_UPDATE_USERINFO, mode = ThreadMode.MAIN)
    private void updateHeaderImgOrName(UserInfo userInfo) {
        LogUtil.error("MineFragment EventBus updateHeaderImg path :" + userInfo.getProfile());
//        ImageUtil.getInstance().loadImg(getActivity(), userInfo.getProfile(), imageOptions, ivHeadIcon);
        ImageUtil.getInstance().loadImgCircle(getActivity(), userInfo.getProfile(), ivHeadIcon);
        tvName.setText(userInfo.getName());
        doGetUserInfo();
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

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_mine;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), UserAgrentActivity.class);
        switch (v.getId()) {
            default:
                break;
            case R.id.activity_login2_user_protocol:
                intent.putExtra("us_ag","yhxy");
                break;
            case R.id.activity_login2_privacy_policy:
                intent.putExtra("us_ag","yszc");

                break;
        }
        startActivity(intent);
    }
}
