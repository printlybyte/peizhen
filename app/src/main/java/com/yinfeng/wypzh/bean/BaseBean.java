package com.yinfeng.wypzh.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseBean<T> implements Serializable {
    private int code;
    private String message;
    @SerializedName("data")
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}

