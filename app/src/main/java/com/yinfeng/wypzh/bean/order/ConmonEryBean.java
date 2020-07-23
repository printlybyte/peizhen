package com.yinfeng.wypzh.bean.order;

/**
 * ============================================
 * 描  述：
 * 包  名：com.yinfeng.wypzh.bean.order
 * 类  名：ConmonSsBean
 * 创建人：liuguodong
 * 创建时间：2019/8/26 14:20
 * ============================================
 **/
public class ConmonEryBean {


    /**
     * code : 500
     * message : 请选择正确的服务时间
     * data :
     * now : 2019-11-05 10:07:10
     */

    private int code;
    private String message;
    private String data;
    private String now;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }
}
