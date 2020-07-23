package com.yinfeng.wypzh.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakewharton.rxbinding2.view.RxView;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.Constants;
import com.yinfeng.wypzh.base.MyApplication;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.bean.login.LoginResult;
import com.yinfeng.wypzh.bean.login.UserInfoNetResult;
import com.yinfeng.wypzh.bean.patient.PatientInfo;
import com.yinfeng.wypzh.http.LoginApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.ui.MainActivity;
import com.yinfeng.wypzh.utils.BarUtils;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.SFUtil;
import com.yinfeng.wypzh.utils.StatusBarUtil;
import com.yinfeng.wypzh.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class SycCodeLoginActivity extends BaseActivity implements View.OnClickListener{

    EditText etPhone, etSyncCode;
    TextView tvGetSyncCode;
    Button btLogin;
    boolean isGetIdentifyCoding = false;
    private final int COUNT_TIME = 60;
    int countTime = COUNT_TIME;
    Disposable mDispose;
    boolean isLogining = false;
    String showProgressTxt = "登录中";
    private String phone;
    private String synccode;

    TextView tvPswLogin, tvForget, tvRegister , policy, protocol;

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, UserAgrentActivity.class);
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
    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
//        StatusBarUtil.setColor(this, Color.parseColor("#f8f8f8"), 0);
        BarUtils.setStatusBarVisibility(SycCodeLoginActivity.this,false);


        etPhone = mRootView.findViewById(R.id.etPhone);
        etSyncCode = mRootView.findViewById(R.id.etSyccode);
        tvGetSyncCode = mRootView.findViewById(R.id.tvGetSynccode);
        btLogin = mRootView.findViewById(R.id.btLogin);
        protocol = mRootView.findViewById(R.id.activity_login2_user_protocol);
        policy = mRootView.findViewById(R.id.activity_login2_privacy_policy);
        protocol.setOnClickListener(this);
        policy.setOnClickListener(this);

        tvPswLogin = mRootView.findViewById(R.id.tvPswLogin);
        tvForget = mRootView.findViewById(R.id.tvForgetPsw);
        tvRegister = mRootView.findViewById(R.id.tvRegister);

        initPswLogin();
        initForgetPsw();
        initRegister();
    }
    private void initRegister() {
        ClickableSpan registSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(Color.parseColor("#06b49b"));
            }

            @Override
            public void onClick(View widget) {
                doRegister();
            }
        };

        String registerAll = getResources().getString(R.string.login_toregist);
        String regist = "注册";
        int index = registerAll.indexOf(regist);
        SpannableString ss = new SpannableString(registerAll);
        ss.setSpan(registSpan, index, index + regist.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(ss);
        ContextUtils.setTextViewLinkClickable(tvRegister);
    }


    private void initForgetPsw() {
        ClickableSpan forgetSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(Color.parseColor("#434343"));
            }

            @Override
            public void onClick(View widget) {
                doForgetPsw();
            }
        };

        String forget = getResources().getString(R.string.login_forgetpsw);
        SpannableString ss = new SpannableString(forget);
        ss.setSpan(forgetSpan, 0, forget.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvForget.setText(ss);
        ContextUtils.setTextViewLinkClickable(tvForget);
    }


    private void initPswLogin() {
        ClickableSpan sync = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(Color.parseColor("#434343"));
            }

            @Override
            public void onClick(View widget) {
                doPswLogin();
            }
        };

        String pswLogin = "密码登录";
        SpannableString ss = new SpannableString(pswLogin);
        ss.setSpan(sync, 0, pswLogin.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPswLogin.setText(ss);
        ContextUtils.setTextViewLinkClickable(tvPswLogin);
    }
    private void doPswLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
//        finish();
    }

    private void doForgetPsw() {
        Intent intent = new Intent(this, ForgetPassWordActivity.class);
        startActivity(intent);
    }

    private void doRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        RxView.clicks(btLogin).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (isLogining) {
                            showLoadingDialog(showProgressTxt);
                        } else {
                            if (checkLoginParamValid())
                                doLogin();
                        }
                    }
                });
        RxView.clicks(tvGetSyncCode).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        getIdentifyCodeClick();
                    }
                });
    }

    private boolean checkLoginParamValid() {
        phone = etPhone.getText().toString().trim();
        synccode = etSyncCode.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.getInstance().showShort(SycCodeLoginActivity.this, "请输入您的手机号！");
            return false;
        } else {
            if (!ContextUtils.isValidPhoneNum(phone)) {
                ToastUtil.getInstance().showShort(SycCodeLoginActivity.this, "请检查输入的手机号是否正确！");
                return false;
            }
        }
        if (TextUtils.isEmpty(synccode)) {
            ToastUtil.getInstance().showShort(SycCodeLoginActivity.this, "请获取并输入验证码！");
            return false;
        }
        return true;
    }

    private void doGetIndentifyCode() {
        LoginApi.getInstance().getSyncCodeLogin(phone)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        if (result != null && result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {

                        } else {
                            ToastUtil.getInstance().showShort(SycCodeLoginActivity.this, result.getMessage());
                            resetSyncodeState("重新获取");
                        }

                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                        resetSyncodeState("重新获取");
                    }
                });

    }

    private void resetSyncodeState(String content) {
        tvGetSyncCode.setText(content);
        if (mDispose != null)
            mDispose.dispose();
        isGetIdentifyCoding = false;
        countTime = COUNT_TIME;
    }

    private void doLogin() {
        isLogining = true;
        showLoadingDialog(showProgressTxt);
        final String username = etPhone.getText().toString().trim();
        final String code = etSyncCode.getText().toString().trim();
        LoginApi.getInstance().loginWithCode(username, code)
                .compose(RxSchedulers.<Response<LoginResult>>applySchedulers())
//                .compose(this.<Response<LoginResult>>bindToLife())
                .subscribe(new BaseObserver<LoginResult>() {

                    @Override
                    public void success(LoginResult result) {
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            SFUtil.getInstance().clearAllSf(SycCodeLoginActivity.this);

                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(SycCodeLoginActivity.this);
                            userInfo.setUserID(username);
                            userInfo.setToken(result.getResult());
                            userInfo.setComplete(result.isComplete());
                            SFUtil.getInstance().setUserInfo(SycCodeLoginActivity.this, userInfo);

                            ContextUtils.addTagsUm(SycCodeLoginActivity.this, Constants.UMENG_TAG_MEMBER);
                            MyApplication.getInstance().setToken(result.getResult());
                            MyApplication.getInstance().setComplete(result.isComplete());
                            if (result.isComplete()) {
                                doGetUserInfo();
                            } else {
                                isLogining = false;
                                hideLoadingDialog();
                                Intent intent = new Intent(SycCodeLoginActivity.this, FillInfoActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            isLogining = false;
                            hideLoadingDialog();
                            ToastUtil.getInstance().showShort(SycCodeLoginActivity.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isLogining = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    private void doGetUserInfo() {
        LoginApi.getInstance().getUserInfo()
                .compose(RxSchedulers.<Response<BaseBean<UserInfoNetResult>>>applySchedulers())
                .compose(this.<Response<BaseBean<UserInfoNetResult>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<UserInfoNetResult>>() {
                    @Override
                    public void success(BaseBean<UserInfoNetResult> result) {
                        isLogining = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            UserInfoNetResult uInfo = result.getResult();
                            PatientInfo pInfo = uInfo.getMemberPatient();

                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(SycCodeLoginActivity.this);
                            userInfo.setAccountId(uInfo.getAccountId());
                            userInfo.setName(uInfo.getName());
                            userInfo.setSex(uInfo.getSex());
                            userInfo.setLevel(uInfo.getLevel());
                            userInfo.setProfile(uInfo.getProfile());
                            userInfo.setIdcard(pInfo.getIdcard());
                            userInfo.setIsHistory(pInfo.getIsHistory());
                            userInfo.setMedicalHistory(pInfo.getMedicalHistory());
                            userInfo.setOtherMedical(pInfo.getOtherMedical());
                            SFUtil.getInstance().setUserInfo(SycCodeLoginActivity.this, userInfo);
                        }
                        Intent intent = new Intent(SycCodeLoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isLogining = false;
                        hideLoadingDialog();
                        Intent intent = new Intent(SycCodeLoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

    }

    private void getIdentifyCodeClick() {
        phone = etPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            if (!ContextUtils.isValidPhoneNum(phone)) {
                ToastUtil.getInstance().showShort(SycCodeLoginActivity.this, "请检查手机号码是否正确！");
                return;
            } else {
                getIdentifyCode();
            }
        } else {
            ToastUtil.getInstance().showShort(SycCodeLoginActivity.this, "请输入手机号！");
        }

    }


    private void getIdentifyCode() {
        if (!isGetIdentifyCoding) {
            isGetIdentifyCoding = true;
            doGetIndentifyCode();
            tvGetSyncCode.setText("60秒");
            Observable.interval(0, 1, TimeUnit.SECONDS)
                    .compose(RxSchedulers.<Long>applySchedulers())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDispose = d;
                        }

                        @Override
                        public void onNext(Long aLong) {
                            if (countTime == 0) {
                                tvGetSyncCode.setText(getResources().getString(R.string.register_get_synccode));
                                mDispose.dispose();
                                isGetIdentifyCoding = false;
                                countTime = COUNT_TIME;
                                return;
                            }
                            String showTime = countTime + "秒";
                            tvGetSyncCode.setText(showTime);
                            countTime--;
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_syc_code_login_new;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDispose != null)
            mDispose.dispose();
    }

//    @Override
//    public void onBackPressedSupport() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
}
