package com.yinfeng.wypzh.http;


import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.bean.OssResult;
import com.yinfeng.wypzh.bean.UpdateVersionBean;
import com.yinfeng.wypzh.bean.login.ChangePswParam;
import com.yinfeng.wypzh.bean.login.FillInfoParam;
import com.yinfeng.wypzh.bean.login.IMLogInfo;
import com.yinfeng.wypzh.bean.login.LoginParamBean;
import com.yinfeng.wypzh.bean.login.LoginResult;
import com.yinfeng.wypzh.bean.login.RegistParamBean;
import com.yinfeng.wypzh.bean.login.UserInfoNetResult;
import com.yinfeng.wypzh.bean.message.PushMessageListResult;
import com.yinfeng.wypzh.http.common.RetrofitManager;

import io.reactivex.Observable;
import retrofit2.Response;

public class LoginApi {
    private volatile static LoginApi instance;

    private LoginApi() {
    }

    public static LoginApi getInstance() {
        if (instance == null) {
            synchronized (LoginApi.class) {
                if (instance == null)
                    instance = new LoginApi();
            }
        }
        return instance;
    }

    /**
     * 登录
     *
     * @param userName
     * @param passWord
     * @return
     */
    public Observable<Response<LoginResult>> login(String userName, String passWord) {
        LoginParamBean bean = new LoginParamBean(userName, passWord);
        return RetrofitManager.getInstance().create(LoginService.class).login(bean);
    }

    /**
     * 登录(验证码)
     *
     * @param userName
     * @param code
     * @return
     */
    public Observable<Response<LoginResult>> loginWithCode(String userName, String code) {
        LoginParamBean bean = new LoginParamBean();
        bean.setUsername(userName);
        bean.setCode(code);
        return RetrofitManager.getInstance().create(LoginService.class).login(bean);
    }

    /**
     * 注册
     *
     * @param userName
     * @param passWord
     * @param code
     * @return
     */
    public Observable<Response<BaseBean<String>>> reist(String userName, String passWord, String code) {
        RegistParamBean bean = new RegistParamBean(userName, passWord, code);
        return RetrofitManager.getInstance().create(LoginService.class).regist(bean);
    }

    /**
     * 获取验证码注册)
     *
     * @param mobile
     * @return
     */
    public Observable<Response<BaseBean<String>>> getSyncCode(String mobile) {
        return RetrofitManager.getInstance().create(LoginService.class).getSyncCode(mobile);
    }

    /**
     * 获取登录验证吗
     *
     * @param mobile
     * @return
     */
    public Observable<Response<BaseBean<String>>> getSyncCodeLogin(String mobile) {
        return RetrofitManager.getInstance().create(LoginService.class).getSyncCodeLogin(mobile);
    }

    /**
     * 修改密码
     *
     * @param newPsw
     * @param code
     * @return
     */
    public Observable<Response<BaseBean<String>>> changePassWord(String mobile, String newPsw, String code) {
        ChangePswParam param = new ChangePswParam(mobile, newPsw, code);
        return RetrofitManager.getInstance().create(LoginService.class).changePassWord(param);
    }

    /**
     * 完善个人信息
     *
     * @param param
     * @return
     */
    public Observable<Response<BaseBean<String>>> fillUserInfo(FillInfoParam param) {
        return RetrofitManager.getInstance().create(LoginService.class).fillUserInfo(param);
    }

    /**
     * 获取oss tokens
     *
     * @return
     */
    public Observable<Response<BaseBean<OssResult>>> getOssTokens() {
        return RetrofitManager.getInstance().create(LoginService.class).getOssTokens();
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public Observable<Response<BaseBean<UserInfoNetResult>>> getUserInfo() {
        return RetrofitManager.getInstance().create(LoginService.class).getUserInfo();
    }

    /**
     * 获取云信token
     *
     * @return
     */
    public Observable<Response<BaseBean<IMLogInfo>>> getIMcloudLoginInfo() {
        return RetrofitManager.getInstance().create(LoginService.class).getIMcloudLoginInfo();
    }

    /**
     * 获取投诉电话
     *
     * @return
     */
    public Observable<Response<BaseBean<String>>> getComplainPhone() {
        return RetrofitManager.getInstance().create(LoginService.class).getComplainPhone();
    }

    /**
     * 获取订单提醒列表
     *
     * @return
     */
    public Observable<Response<BaseBean<PushMessageListResult>>> getPushMessageList() {
        return RetrofitManager.getInstance().create(LoginService.class).getPushMessageList();
    }

    /**
     * 获取安卓最新版本信息
     *
     * @return
     */
    public Observable<Response<BaseBean<UpdateVersionBean>>> checkVersion() {
        return RetrofitManager.getInstance().create(LoginService.class).checkVersion("0");
    }

    /**
     * 会员登录成功回调 激活优惠券
     *
     * @return
     */
    public Observable<Response<BaseBean<String>>> activeCoupon() {
        return RetrofitManager.getInstance().create(LoginService.class).activeCoupon();
    }


}
