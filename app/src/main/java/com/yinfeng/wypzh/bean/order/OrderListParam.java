package com.yinfeng.wypzh.bean.order;

import java.io.Serializable;

/**
 * @author Asen
 */
public class OrderListParam implements Serializable {
    private String state;
    private String eveState;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEveState() {
        return eveState;
    }

    public void setEveState(String eveState) {
        this.eveState = eveState;
    }
}
