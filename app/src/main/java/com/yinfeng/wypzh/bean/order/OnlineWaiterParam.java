package com.yinfeng.wypzh.bean.order;

import java.io.Serializable;

/**
 * @author Asen
 */
public class OnlineWaiterParam implements Serializable {
    private String hospitalId;

    public OnlineWaiterParam(String hospitalId) {
        this.hospitalId = hospitalId;
    }
}
