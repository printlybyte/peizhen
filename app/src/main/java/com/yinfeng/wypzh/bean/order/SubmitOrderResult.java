package com.yinfeng.wypzh.bean.order;

import com.yinfeng.wypzh.bean.BaseBean;

import java.io.Serializable;

/**
 * @author Asen
 */
public class SubmitOrderResult extends BaseBean<SubmitOrderData> implements Serializable {
    private String now;

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

}