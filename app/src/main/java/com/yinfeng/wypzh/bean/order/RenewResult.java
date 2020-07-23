package com.yinfeng.wypzh.bean.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Asen
 */
public class RenewResult implements Serializable {
    @SerializedName("id")
    private String renewId;
    @SerializedName("price")
    private int renewPrice;

    private int renewTime;

    public String getRenewId() {
        return renewId;
    }

    public void setRenewId(String renewId) {
        this.renewId = renewId;
    }

    public int getRenewPrice() {
        return renewPrice;
    }

    public void setRenewPrice(int renewPrice) {
        this.renewPrice = renewPrice;
    }

    public int getRenewTime() {
        return renewTime;
    }

    public void setRenewTime(int renewTime) {
        this.renewTime = renewTime;
    }
}
