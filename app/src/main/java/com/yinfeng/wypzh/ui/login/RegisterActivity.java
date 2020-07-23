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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.base.MyApplication;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.UserInfo;
import com.yinfeng.wypzh.http.LoginApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
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

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    EditText etPhone, etPsw, etSyncCode;
    TextView tvGetSyncCode, tvAgreement, policy, protocol;
    ImageView ivShowOrHide, ivAgreenSelect;
    Button btRegister;
    boolean isPswHide = false;
    boolean isGetIdentifyCoding = false;
    private final int COUNT_TIME = 60;
    int countTime = COUNT_TIME;
    Disposable mDispose;
    boolean isAgree = false;
    boolean isRegistering = false;
    String showProgressTxt = "注册中";
    private String phone;
    private String password;
    private String synccode;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
//        StatusBarUtil.setColor(this, Color.parseColor("#f8f8f8"), 0);
        BarUtils.setStatusBarVisibility(RegisterActivity.this,false);



        etPhone = mRootView.findViewById(R.id.etPhone);
        etPsw = mRootView.findViewById(R.id.etPassword);
        etSyncCode = mRootView.findViewById(R.id.etSyccode);

        tvGetSyncCode = mRootView.findViewById(R.id.tvGetSynccode);
        tvAgreement = mRootView.findViewById(R.id.tvAgreenAndNotice);

        ivShowOrHide = mRootView.findViewById(R.id.ivHideOrOpen);
        ivAgreenSelect = mRootView.findViewById(R.id.ivAgreenSelect);
        btRegister = mRootView.findViewById(R.id.btRegister);

        protocol = mRootView.findViewById(R.id.activity_login2_user_protocol);
        policy = mRootView.findViewById(R.id.activity_login2_privacy_policy);
        protocol.setOnClickListener(this);
        policy.setOnClickListener(this);
        initAgreenMent();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void setListener() {
        ivShowOrHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPswHide) {
                    ivShowOrHide.setImageResource(R.drawable.show);
                    etPsw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isPswHide = false;
                } else {
                    ivShowOrHide.setImageResource(R.drawable.hide);
                    etPsw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPswHide = true;
                }
                if (etPsw.hasFocus())
                    etPsw.setSelection(etPsw.getText().toString().length());
            }
        });

        ivAgreenSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAgree) {
                    ivAgreenSelect.setImageResource(R.drawable.unselect_oval);
                    isAgree = false;
                } else {
                    ivAgreenSelect.setImageResource(R.drawable.selected_oval);
                    isAgree = true;
                }
            }
        });

        RxView.clicks(btRegister).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (isRegistering) {
                            showLoadingDialog(showProgressTxt);
                        } else {
                            if (checkRegistParamValid()) {
                                doRegist();
                            }
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


    @Override
    protected void initData() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDispose != null)
            mDispose.dispose();
    }

    private void doGetIndentifyCode() {
        LoginApi.getInstance().getSyncCode(phone)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        if (result != null && result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {

                        } else {
                            ToastUtil.getInstance().showShort(RegisterActivity.this, result.getMessage());
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

    private void doRegist() {
        isRegistering = true;
        showLoadingDialog(showProgressTxt);
        LoginApi.getInstance().reist(phone, password, synccode)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        isRegistering = false;
                        hideLoadingDialog();
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            String token = result.getResult();
                            if (TextUtils.isEmpty(token)) {
                                ToastUtil.getInstance().showShort(RegisterActivity.this, "数据异常，请重新获取");
                                return;
                            }
                            SFUtil.getInstance().clearAllSf(RegisterActivity.this);

                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(RegisterActivity.this);
                            userInfo.setUserID(phone);
                            userInfo.setToken(token);
                            userInfo.setComplete(false);
                            SFUtil.getInstance().setUserInfo(RegisterActivity.this, userInfo);

                            MyApplication.getInstance().setToken(token);
                            MyApplication.getInstance().setComplete(false);
                            FillInfoActivity.activityStart(RegisterActivity.this, false);
                            finish();
                        } else {
                            ToastUtil.getInstance().showShort(RegisterActivity.this, result.getMessage());
                        }

                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isRegistering = false;
                        hideLoadingDialog();
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    //    Intent intent = new Intent(RegisterActivity.this, FillInfoActivity.class);
//    startActivity(intent);
    private boolean checkRegistParamValid() {
        phone = etPhone.getText().toString().trim();
        password = etPsw.getText().toString().trim();
        synccode = etSyncCode.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            ToastUtil.getInstance().showShort(RegisterActivity.this, "请输入手机号！");
            return false;
        }
        if (TextUtils.isEmpty(password) || !ContextUtils.isPassWordValid(password)) {
            ToastUtil.getInstance().showShort(RegisterActivity.this, "请按要求设置密码！");
            return false;
        }
        if (TextUtils.isEmpty(synccode)) {
            ToastUtil.getInstance().showShort(RegisterActivity.this, "请获取验证码！");
            return false;
        }
        if (!isAgree) {
            ToastUtil.getInstance().showShort(RegisterActivity.this, "请阅读并同意服务隐私条款！");
            return false;
        }

        return true;
    }

    private void getIdentifyCodeClick() {
        phone = etPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            if (!ContextUtils.isValidPhoneNum(phone)) {
                ToastUtil.getInstance().showShort(RegisterActivity.this, "请检查手机号码是否正确！");
                return;
            } else {
                getIdentifyCode();
            }
        } else {
            ToastUtil.getInstance().showShort(RegisterActivity.this, "请输入手机号！");
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
                                resetSyncodeState(getResources().getString(R.string.register_get_synccode));
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

    private void resetSyncodeState(String content) {
        tvGetSyncCode.setText(content);
        if (mDispose != null)
            mDispose.dispose();
        isGetIdentifyCoding = false;
        countTime = COUNT_TIME;
    }

    private void initAgreenMent() {
        String agreenMentScript = getResources().getString(R.string.register_serviceandnotice);
        String service = "服务协议";
        String privacy = "隐私条款";
        int indexService = agreenMentScript.indexOf(service);
        int indexPrivacy = agreenMentScript.indexOf(privacy);

        SpannableString ss = new SpannableString(agreenMentScript);
        ForegroundColorSpan greySpan1 = new ForegroundColorSpan(Color.parseColor("#898989"));
        ForegroundColorSpan greySpan2 = new ForegroundColorSpan(Color.parseColor("#898989"));
        ss.setSpan(greySpan1, 0, indexService, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(greySpan2, indexService + service.length(), indexPrivacy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        MyClickableSpan serviceClickSpan = new MyClickableSpan(service);
        MyClickableSpan privacyClickSpan = new MyClickableSpan(privacy);
        ss.setSpan(serviceClickSpan, indexService, service.length() + indexService, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(privacyClickSpan, indexPrivacy, privacy.length() + indexPrivacy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvAgreement.setText(ss);
        ContextUtils.setTextViewLinkClickable(tvAgreement);
    }


    class MyClickableSpan extends ClickableSpan {

        private String content;

        public MyClickableSpan(String content) {
            this.content = content;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(Color.parseColor("#06b49b"));
        }

        @Override
        public void onClick(View widget) {
            ToastUtil.getInstance().showShort(RegisterActivity.this, content);

        }
    }
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
}
