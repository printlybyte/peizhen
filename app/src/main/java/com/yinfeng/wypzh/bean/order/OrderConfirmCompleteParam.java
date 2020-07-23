package com.yinfeng.wypzh.bean.order;

import java.io.Serializable;

/**
 * @author Asen
 */
public class OrderConfirmCompleteParam implements Serializable {
    public OrderConfirmCompleteParam() {
    }

    public OrderConfirmCompleteParam(String id) {
        this.id = id;
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
