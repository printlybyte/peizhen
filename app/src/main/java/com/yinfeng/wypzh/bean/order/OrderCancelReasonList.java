package com.yinfeng.wypzh.bean.order;

import java.io.Serializable;
import java.util.List;

/**
 * @author Asen
 */
public class OrderCancelReasonList implements Serializable {
    private List<OrderCancelReason> list;

    public List<OrderCancelReason> getList() {
        return list;
    }

    public void setList(List<OrderCancelReason> list) {
        this.list = list;
    }
}
