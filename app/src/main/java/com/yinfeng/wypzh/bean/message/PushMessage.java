package com.yinfeng.wypzh.bean.message;

import java.io.Serializable;

/**
 * @author Asen
 */
public class PushMessage implements Serializable {
    /**
     * "display_type": "notification",
     * "extra": {
     * "orderID": "90fcf105deb34cafb4e7821d8d5e0b41",
     * * "id":"renewid",
     * "pushType": "APPLY_AGREE"
     * },
     * "msg_id": "uapffbp153793399471501",
     * "body": {
     * "after_open": "go_app",
     * "ticker": "延时确认",
     * "text": "您好，陪诊员已同意您的延时服务申请。",
     * "title": "延时确认"
     * },
     * "random_min": 0
     * }
     */

    private String orderID;
    private String id;
    private String pushType;
    private String msg_id;
    private String ticker;
    private String text;
    private String title;
    private int notifId;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNotifId() {
        return notifId;
    }

    public void setNotifId(int notifId) {
        this.notifId = notifId;
    }
}
