package com.yinfeng.wypzh.http.common;


import android.text.TextUtils;

import com.yinfeng.wypzh.base.MyApplication;
import com.yinfeng.wypzh.bean.UserAgentBean;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.LogUtil;
import com.yinfeng.wypzh.utils.NetUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.yinfeng.wypzh.http.common.ApiContents.BASE_URL;

public class HeaderInterceptor implements Interceptor {
    static final String common_key = "拦截器 头部 ";

    static final String KEY_MD_TOKEN = "MD-TOKEN";
    //设缓存有效期为1天
    static final long CACHE_STALE_SEC = 60 * 60 * 24 * 1;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;

    UserAgentBean userAgentBean;
    private String userAgent;

    public HeaderInterceptor() {
        initConfig();
    }

    private void initConfig() {
        initUserAgent();
    }

    private void initUserAgent() {
        userAgentBean = ContextUtils.getUserAgent();
        userAgent = ContextUtils.getUserAgentJsonStr();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String url = original.url().toString();
        Request.Builder requestBuilder = original.newBuilder()
                .header("Content-Type", "application/json;charset=utf-8")
                .header("User-Agent", userAgent);
        String token = ContextUtils.getToken(MyApplication.getInstance());
        if (!TextUtils.isEmpty(token)) {
            requestBuilder.header(KEY_MD_TOKEN, token);
            if (checkUrlIsNeedFilterMDToken(url)) {
                requestBuilder.removeHeader(KEY_MD_TOKEN);
            }
        }
        if (NetUtil.isNetworkAvailable(MyApplication.getInstance())) {
            requestBuilder.cacheControl(getNoCacheCachControl());
        } else {
            requestBuilder.cacheControl(CacheControl.FORCE_CACHE);
            LogUtil.error(common_key + "没有网络，从缓存获取");
        }
        Request request = requestBuilder.build();
        Response originalResponse = chain.proceed(request);
        if (NetUtil.isNetworkAvailable(MyApplication.getInstance())) {
            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, " + CACHE_CONTROL_CACHE)
                    .build();
        }
    }

    private CacheControl getNoCacheCachControl() {
        CacheControl.Builder builder = new CacheControl.Builder();
        builder.noCache();
        return builder.build();
    }

    private boolean checkUrlIsNeedFilterMDToken(String url) {
        String loginApi = BASE_URL + "login";
        String registerApi = BASE_URL + "member/register";
        String syncodeApi = BASE_URL + "common/sms";
        String syncodeLogin = BASE_URL+"common/login/sms/";
        if (TextUtils.equals(loginApi, url))
            return true;
        if (TextUtils.equals(registerApi, url))
            return true;
        if (url.contains(syncodeApi))
            return true;
        if (url.contains(syncodeLogin))
            return true;
//        if(url.startsWith(BASE_URL+"common/")){
//            return true;
//        }
        return false;
    }

    /**
     final CacheControl.Builder builder = new CacheControl.Builder();
     builder.noCache();//不使用缓存，全部走网络
     builder.noStore();//不使用缓存，也不存储缓存
     builder.onlyIfCached();//只使用缓存
     builder.noTransform();//禁止转码
     builder.maxAge(10, TimeUnit.MILLISECONDS);//指示客户机可以接收生存期不大于指定时间的响应。
     builder.maxStale(10, TimeUnit.SECONDS);//指示客户机可以接收超出超时期间的响应消息
     builder.minFresh(10, TimeUnit.SECONDS);//指示客户机可以接收响应时间小于当前时间加上指定时间的响应。
     CacheControl cache = builder.build();//cacheControl

     CacheControl.FORCE_CACHE; //仅仅使用缓存
     CacheControl.FORCE_NETWORK;// 仅仅使用网络
     */
}
