package com.yinfeng.wypzh.ui.login;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yinfeng.wypzh.R;
import com.yinfeng.wypzh.base.BaseActivity;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.http.LoginApi;
import com.yinfeng.wypzh.http.common.ApiContents;
import com.yinfeng.wypzh.http.common.BaseObserver;
import com.yinfeng.wypzh.http.common.RxSchedulers;
import com.yinfeng.wypzh.utils.BarUtils;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.StatusBarUtil;
import com.yinfeng.wypzh.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Response;

public class ForgetPassWordActivity extends BaseActivity {
    EditText etPhone, etPsw, etSyncCode;
    TextView tvGetSyncCode;
    ImageView ivShowOrHide;
    Button btComplete;
    boolean isPswHide = false;
    boolean isGetIdentifyCoding = false;
    private final int COUNT_TIME = 60;
    int countTime = COUNT_TIME;
    Disposable mDispose;
    boolean isCompleting = false;
    private String phone;
    private String password;
    private String synccode;

    @Override
    protected void bindView(View mRootView, Bundle savedInstanceState) {
//        StatusBarUtil.setColor(this, Color.parseColor("#f8f8f8"), 0);

        BarUtils.setStatusBarVisibility(ForgetPassWordActivity.this,false);


        etPhone = mRootView.findViewById(R.id.etPhone);
        etPsw = mRootView.findViewById(R.id.etPassword);
        etSyncCode = mRootView.findViewById(R.id.etSyccode);

        tvGetSyncCode = mRootView.findViewById(R.id.tvGetSynccode);

        ivShowOrHide = mRootView.findViewById(R.id.ivHideOrOpen);
        btComplete = mRootView.findViewById(R.id.btComplete);

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


        RxView.clicks(btComplete).
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        if (isCompleting) {
                            showLoadingDialog();
                        } else {
                            if (checkRegistParamValid()) {
                                doComplete();
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

    private void doGetIndentifyCode() {
        LoginApi.getInstance().getSyncCodeLogin(phone)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        if (result != null && result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {

                        } else {
                            ToastUtil.getInstance().showShort(ForgetPassWordActivity.this, result.getMessage());
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

    private void doComplete() {
        isCompleting = true;
        showLoadingDialog();
        String mobile = etPhone.getText().toString().trim();
        String psw = etPsw.getText().toString().trim();
        String code = etSyncCode.getText().toString().trim();
        LoginApi.getInstance().changePassWord(mobile, psw, code)
                .compose(RxSchedulers.<Response<BaseBean<String>>>applySchedulers())
                .compose(this.<Response<BaseBean<String>>>bindToLife())
                .subscribe(new BaseObserver<BaseBean<String>>() {
                    @Override
                    public void success(BaseBean<String> result) {
                        hideLoadingDialog();
                        isCompleting = false;
                        if (result.getCode() == ApiContents.CODE_REQUEST_SUCCESS) {
                            ToastUtil.getInstance().showShort(ForgetPassWordActivity.this, "修改成功");
                            finish();
                        } else {
                            ToastUtil.getInstance().showShort(ForgetPassWordActivity.this, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(int httpCode, int errCode, String errorMsg) {
                        hideLoadingDialog();
                        isCompleting = false;
                        checkNetValidAndToast(httpCode, errCode, errorMsg);
                    }
                });
    }

    private boolean checkRegistParamValid() {
        phone = etPhone.getText().toString().trim();
        password = etPsw.getText().toString().trim();
        synccode = etSyncCode.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            ToastUtil.getInstance().showShort(ForgetPassWordActivity.this, "请输入手机号！");
            return false;
        }
        if (TextUtils.isEmpty(password) || !ContextUtils.isPassWordValid(password)) {
//            ToastUtil.getInstance().showShort(ForgetPassWordActivity.this, "请按要求设置密码！");
            ToastUtil.getInstance().showShort(ForgetPassWordActivity.this, getString(R.string.register_psw_hint));
            return false;
        }
        if (TextUtils.isEmpty(synccode)) {
            ToastUtil.getInstance().showShort(ForgetPassWordActivity.this, "请获取验证码！");
            return false;
        }
        return true;
    }

    private void getIdentifyCodeClick() {
        phone = etPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            if (!ContextUtils.isValidPhoneNum(phone)) {
                ToastUtil.getInstance().showShort(ForgetPassWordActivity.this, "请检查手机号码是否正确！");
                return;
            } else {
                getIdentifyCode();
            }
        } else {
            ToastUtil.getInstance().showShort(ForgetPassWordActivity.this, "请输入手机号！");
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
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    etSyncCode.requestFocus();
                    return true;
                }
                return false;
            }
        });
        etSyncCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ContextUtils.hideSoftInput(ForgetPassWordActivity.this);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_forget_pass_word;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDispose != null)
            mDispose.dispose();
    }
}
