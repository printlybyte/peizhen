package com.yinfeng.wypzh.http.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yinfeng.wypzh.bean.BaseBean;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private Retrofit mRetrofit;

    private volatile static RetrofitManager instance;

    public static RetrofitManager getInstance() {
        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null)
                    instance = new RetrofitManager();
            }
        }
        return instance;
    }

    private RetrofitManager() {
        init();
    }

    private void init() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiContents.BASE_URL)
                .client(OkHttpManager.getInstance().client())
//                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addConverterFactory(GsonConverterFactory.create(initGsonForBaseBean()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

    private Gson initGsonForBaseBean() {
        return new GsonBuilder().registerTypeAdapter(BaseBean.class, new BaseBeanTypedAdapter()).create();
    }

    public <T> T create(Class<T> reqServer) {
        return mRetrofit.create(reqServer);
    }

}

