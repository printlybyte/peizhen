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

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import static com.yinfeng.wypzh.http.common.ApiContents.BASE_URL;

public interface LoginService {
//    @POST("front/login")
//    @FormUrlEncoded
//    Observable<Response<LoginResult>> login(@Field("username") String username, @Field("password") String password);
//    Observable<LoginResult> login(@Field("username") String username, @Field("password") String password);

//    @POST("http://192.168.1.143:8014/front/login")
//    Observable<Response<LoginResult>> login(@Body RequestBody body);

    @POST(BASE_URL + "login")
    Observable<Response<LoginResult>> login(@Body LoginParamBean bean);

    @POST(BASE_URL + "member/register")
    Observable<Response<BaseBean<String>>> regist(@Body RegistParamBean bean);

    @POST(BASE_URL + "common/sms/{mobile}")
    Observable<Response<BaseBean<String>>> getSyncCode(@Path("mobile") String mobile);

    @POST(BASE_URL + "common/login/sms/{mobile}")
    Observable<Response<BaseBean<String>>> getSyncCodeLogin(@Path("mobile") String mobile);

    @POST(BASE_URL + "member/complete")
    //完善信息
    Observable<Response<BaseBean<String>>> fillUserInfo(@Body FillInfoParam fillInfoParam);

    @POST(BASE_URL + "account/resetPsw")
    Observable<Response<BaseBean<String>>> changePassWord(@Body ChangePswParam param);

    @POST(BASE_URL + "common/ossToken")
    Observable<Response<BaseBean<OssResult>>> getOssTokens();

    @POST(BASE_URL + "member/")
    Observable<Response<BaseBean<UserInfoNetResult>>> getUserInfo();

    @POST(BASE_URL + "common/ref/im/token")
    Observable<Response<BaseBean<IMLogInfo>>> getIMcloudLoginInfo();

    @POST(BASE_URL + "common/complaintsPhone")
    Observable<Response<BaseBean<String>>> getComplainPhone();

    /**
     * 获取订单提醒列表
     *
     * @return
     */
    @POST(BASE_URL + "push/query")
    Observable<Response<BaseBean<PushMessageListResult>>> getPushMessageList();

    /**
     * 获取安卓最新版本信息
     *
     * @return
     */
    @POST(BASE_URL + "common/android")
    Observable<Response<BaseBean<UpdateVersionBean>>> checkVersion(@Query("type") String type);

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

    /**
     * 会员登录成功回调 激活免单全
     *
     * @return
     */
    @POST(BASE_URL + "couponAccount/activate")
    Observable<Response<BaseBean<String>>> activeCoupon();
}
