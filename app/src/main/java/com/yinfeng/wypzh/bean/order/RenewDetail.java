package com.yinfeng.wypzh.bean.order;

import java.io.Serializable;

/**
 * @author Asen
 */
public class RenewDetail implements Serializable {

    /**
     * id : 0cce1f484f6240e692c4404803e36626
     * delFlag : 0
     * createTime : 2018-09-20 14:16:55
     * updateTime : 2018-09-20 14:36:56
     * createBy : 9ffb44355dd2426cb366195015ded0b7
     * updateBy :
     * state : UNPID
     *
     * SUBMIT:申请 会员申请加时
     * PAID:支付完成
     * AGREE:同意 陪诊员同意
     * REJECT:拒绝 陪诊员拒绝
     * OVERTIME:超时 陪诊员超时未处理
     * CANCEL:取消 会员取消申请
     * ERROR:异常，原因在cancel_reason
     *
     * sort :
     * orderId : d27dc6b0f9784b54ac3791ea6b2081ad
     * orderCode: 订单号
     * hospitalId :
     * departmentId :
     * memberId : 9ffb44355dd2426cb366195015ded0b7
     * waiterId : 022580a0bd2f499abbdd2e91685dae93
     * expire : 30
     * payId :
     * payWay :
     * payPrice : 0
     * cancelReason : 超时未支付
     * refundNo :
     * refundState :
     * productId :
     * waiter :
     */

    private String id;
    private String delFlag;
    private String createTime;
    private String updateTime;
    private String createBy;
    private String updateBy;
    private String state;
    private String sort;
    private String orderId;
    private String orderCode;
    private String hospitalId;
    private String departmentId;
    private String memberId;
    private String waiterId;
    private int expire;
    private String payId;
    private String payWay;
    private int payPrice;
    private String cancelReason;
    private String refundNo;
    private String refundState;
    private String productId;
    private String waiter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getWaiterId() {
        return waiterId;
    }

    public void setWaiterId(String waiterId) {
        this.waiterId = waiterId;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public int getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(int payPrice) {
        this.payPrice = payPrice;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public String getRefundState() {
        return refundState;
    }

    public void setRefundState(String refundState) {
        this.refundState = refundState;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getWaiter() {
        return waiter;
    }

    public void setWaiter(String waiter) {
        this.waiter = waiter;
    }
}
