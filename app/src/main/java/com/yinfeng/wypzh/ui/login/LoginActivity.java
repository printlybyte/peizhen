package com.yinfeng.wypzh.ui.login;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.hawk.Hawk;
import com.yinfeng.wypzh.ConstantApi;
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
import com.yinfeng.wypzh.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    Button btLogin;
    EditText etPhone, etPsw;
    TextView tvSyncLogin, tvForget, tvRegister, policy, protocol;
    private String showProgressTxt = "登录中";
    private boolean isLogining = false;


    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
//        StatusBarUtil.setColor(this, Color.parseColor("#F7F8F8"), 0);
        BarUtils.setStatusBarVisibility(LoginActivity.this, false);


        btLogin = mRootView.findViewById(R.id.btLogin);
        etPhone = mRootView.findViewById(R.id.etPhone);
        etPsw = mRootView.findViewById(R.id.etPassword);

        tvSyncLogin = mRootView.findViewById(R.id.tvSycnCodeLogin);
        tvForget = mRootView.findViewById(R.id.tvForgetPsw);
        tvRegister = mRootView.findViewById(R.id.tvRegister);
        protocol = mRootView.findViewById(R.id.activity_login2_user_protocol);
        policy = mRootView.findViewById(R.id.activity_login2_privacy_policy);
        protocol.setOnClickListener(this);
        policy.setOnClickListener(this);

        initSyncCodeLogin();
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


    private void initSyncCodeLogin() {
        ClickableSpan sync = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(Color.parseColor("#434343"));
            }

            @Override
            public void onClick(View widget) {
                doSyncCodeLogin();
            }
        };

        String syncCode = getResources().getString(R.string.login_synccode);
        SpannableString ss = new SpannableString(syncCode);
        ss.setSpan(sync, 0, syncCode.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSyncLogin.setText(ss);
        ContextUtils.setTextViewLinkClickable(tvSyncLogin);
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
                            checkLoginParamValid();
                        }
                    }
                });

    }

    @Override
    protected void initData() {
        etPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    etPsw.requestFocus();
                    return true;
                }
                return false;
            }
        });
        etPsw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ContextUtils.hideSoftInput(LoginActivity.this);
                    checkLoginParamValid();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_login;
    }

    private void checkLoginParamValid() {
        String username = etPhone.getText().toString().trim();
        String password = etPsw.getText().toString().trim();
        ObjectAnimator userNameShakeObjAnim = ContextUtils.getShakeObjAnim(etPhone);
        ObjectAnimator pswShakeObjAnim = ContextUtils.getShakeObjAnim(etPsw);
        if (TextUtils.isEmpty(username)) {
            userNameShakeObjAnim.start();
            etPhone.requestFocus();
        } else {
            if (!ContextUtils.isValidPhoneNum(username)) {
                userNameShakeObjAnim.start();
                etPhone.requestFocus();
                etPhone.setSelection(username != null ? username.length() : 0);
                ToastUtil.getInstance().showShort(LoginActivity.this, "请输入正确的手机号！");
            } else {
                if (TextUtils.isEmpty(password)) {
                    pswShakeObjAnim.start();
                    etPsw.requestFocus();
                } else {
                    ContextUtils.hideSoftInput(LoginActivity.this);
                    doLogin();
                }
            }
        }
    }

    private void doSyncCodeLogin() {
        Intent intent = new Intent(this, SycCodeLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void doForgetPsw() {
        Intent intent = new Intent(this, ForgetPassWordActivity.class);
        startActivity(intent);
    }

    private void doRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void doLogin() {
        isLogining = true;
        showLoadingDialog(showProgressTxt);
        final String username = etPhone.getText().toString().trim();
        String password = etPsw.getText().toString().trim();

        LoginApi.getInstance().login(username, password)
                .compose(RxSchedulers.<Response<LoginResult>>applySchedulers())
//                .compose(this.<Response<LoginResult>>bindToLife())
                .subscribe(new BaseObserver<LoginResult>() {

                    @Override
                    public void success(LoginResult result) {

Log.i("testree",""+new Gson().toJson(result));

                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            SFUtil.getInstance().clearAllSf(LoginActivity.this);

                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(LoginActivity.this);

                          String resultx=  result.getResult().toString()+"";
                            userInfo.setUserID(username);
                            userInfo.setToken(resultx);

                            Hawk.put(ConstantApi.DO_TOKEN, resultx);
                            Log.i(ConstantApi.LOG_I, resultx);

                            userInfo.setComplete(result.isComplete());
                            SFUtil.getInstance().setUserInfo(LoginActivity.this, userInfo);

                            ContextUtils.addTagsUm(LoginActivity.this, Constants.UMENG_TAG_MEMBER);
                            MyApplication.getInstance().setToken(result.getResult());
                            MyApplication.getInstance().setComplete(result.isComplete());
                            if (result.isComplete()) {
                                doGetUserInfo();
                            } else {
                                isLogining = false;
                                hideLoadingDialog();
                                Intent intent = new Intent(LoginActivity.this, FillInfoActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            isLogining = false;
                            hideLoadingDialog();
                            ToastUtil.getInstance().showShort(LoginActivity.this, result.getMessage());
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

                            UserInfo userInfo = SFUtil.getInstance().getUserInfo(LoginActivity.this);
                            userInfo.setAccountId(uInfo.getAccountId()+"");
                            userInfo.setName(uInfo.getName()+"");
                            userInfo.setSex(uInfo.getSex()+"");
                            userInfo.setLevel(uInfo.getLevel()+"");
                            userInfo.setProfile(uInfo.getProfile()+"");
                            userInfo.setIdcard(pInfo.getIdcard()+"");
                            userInfo.setIsHistory(pInfo.getIsHistory()+"");
                            userInfo.setMedicalHistory(pInfo.getMedicalHistory()+"");
                            userInfo.setOtherMedical(pInfo.getOtherMedical()+"");
                            SFUtil.getInstance().setUserInfo(LoginActivity.this, userInfo);
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        isLogining = false;
                        hideLoadingDialog();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

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
