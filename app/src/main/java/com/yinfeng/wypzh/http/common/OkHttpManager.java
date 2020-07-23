package com.yinfeng.wypzh.http.common;


import com.yinfeng.wypzh.utils.ContextUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpManager {
    private OkHttpClient mClient;
    private boolean isNeedRetry = false;
    private static final long mConnectTime = 10;
    private static final long mReadTime = 10;
    private static final long mWriteTime = 10;
    private volatile static OkHttpManager instance;


    public static OkHttpManager getInstance() {
        if (instance == null) {
            synchronized (OkHttpManager.class) {
                if (instance == null)
                    instance = new OkHttpManager();
            }
        }
        return instance;
    }

    private OkHttpManager() {
        mClient = init();
    }

    private OkHttpClient init() {
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.cache(ContextUtils.getOkHttpCache());
        mBuilder.connectTimeout(mConnectTime, TimeUnit.SECONDS);
        mBuilder.readTimeout(mReadTime, TimeUnit.SECONDS);
        mBuilder.writeTimeout(mWriteTime, TimeUnit.SECONDS);
        mBuilder.retryOnConnectionFailure(isNeedRetry);

        addHeaderInterceptor(mBuilder);
        addLogInterceptor(mBuilder);

        return mBuilder.build();
    }

    private void addLogInterceptor(OkHttpClient.Builder mBuilder) {
        mBuilder.addInterceptor(new LogInterceptor());
    }

    private void addHeaderInterceptor(OkHttpClient.Builder mBuilder) {
        mBuilder.addInterceptor(new HeaderInterceptor());
    }

    public OkHttpClient client() {
        if (mClient == null)
            mClient = init();
        return mClient;
    }

}
