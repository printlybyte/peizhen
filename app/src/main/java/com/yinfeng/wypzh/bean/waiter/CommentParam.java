package com.yinfeng.wypzh.bean.waiter;

import java.io.Serializable;

/**
 * @author Asen
 */
public class CommentParam implements Serializable {
    private String code;//订单号
    private String realname;//陪诊员名字
    private String orderId;//	订单id
    private String waiterId;//		陪诊员id
    private String memberName;//会员名字
    private String memberId;//	会员id

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getWaiterId() {
        return waiterId;
    }

    public void setWaiterId(String waiterId) {
        this.waiterId = waiterId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
