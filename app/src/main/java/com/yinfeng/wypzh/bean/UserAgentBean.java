package com.yinfeng.wypzh.bean;

public class UserAgentBean {
    private String versionCode;//app 版本号 动态获取
//    private String netState; //网络状态 移动/wifi 动态获取
    private String os = "Android";
    private String mobile_brand;//品牌
    private String mobile_model;//型号
    private String mobile_display;//版本号


    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

//    public String getNetState() {
//        return netState;
//    }
//
//    public void setNetState(String netState) {
//        this.netState = netState;
//    }

    public String getMobile_brand() {
        return mobile_brand;
    }

    public void setMobile_brand(String mobile_brand) {
        this.mobile_brand = mobile_brand;
    }

    public String getMobile_model() {
        return mobile_model;
    }

    public void setMobile_model(String mobile_model) {
        this.mobile_model = mobile_model;
    }

    public String getMobile_display() {
        return mobile_display;
    }

    public void setMobile_display(String mobile_display) {
        this.mobile_display = mobile_display;
    }
}
