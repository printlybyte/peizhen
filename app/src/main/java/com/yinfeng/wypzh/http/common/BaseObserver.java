package com.yinfeng.wypzh.http.common;


import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yinfeng.wypzh.bean.BaseBean;
import com.yinfeng.wypzh.utils.ContextUtils;
import com.yinfeng.wypzh.utils.LogUtil;

import java.nio.charset.Charset;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public abstract class BaseObserver<T> implements Observer<Response<T>> {

    private Context mContext;
    private boolean isProcessNext = false;

    public BaseObserver() {
    }

    public BaseObserver(Context context) {
        mContext = context;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Response<T> tResponse) {
        isProcessNext = true;
        int httpCode = tResponse.code();
        if (httpCode == 200) {
            success(tResponse.body());
        } else {
            try {
                Charset charset = Charset.forName("UTF-8");
                String errorContent = tResponse.errorBody().source().buffer().readString(charset);
                JsonObject obj = (JsonObject) new JsonParser().parse(errorContent);
                int errCode = obj.get("code").getAsInt();
                if (errCode == ApiContents.CODE_TOKEN_INVALID && mContext != null) {
                    ContextUtils.kickOut(mContext);
                    ContextUtils.showKickDialog(mContext);
                    return;
                }

                String errMessage = obj.get("message").getAsString();
                fail(httpCode, errCode, errMessage);
            } catch (Exception e) {
                fail(httpCode, -1, "");
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.error(e.getMessage());
        Log.i("testre",e.getMessage());
        fail(-1, -1, "");
    }

    @Override
    public void onComplete() {
        if (!isProcessNext) {
            fail(-1, -1, "");
        }
    }


    public abstract void success(T result);

    public abstract void fail(int httpCode, int errCode, String errorMsg);
}

