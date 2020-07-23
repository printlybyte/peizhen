package com.yinfeng.wypzh.bean.order;


import java.io.Serializable;

/**
 * @author Asen
 */
public class CancelOrderParam implements Serializable {

    private String id;
    private String cancelType;//陪诊员取消值为：WAITER 会员取消值为：MEMBER
    private String cancelReason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCancelType() {
        return cancelType;
    }

    public void setCancelType(String cancelType) {
        this.cancelType = cancelType;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
