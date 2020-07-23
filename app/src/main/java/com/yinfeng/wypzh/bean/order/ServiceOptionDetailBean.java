package com.yinfeng.wypzh.bean.order;

import java.io.Serializable;

/**
 * @author Asen
 */
public class ServiceOptionDetailBean implements Serializable {

    /**
     * id : 39c2b1ec589d4038b4281d0d4dbb9c2c
     * delFlag : 0
     * createTime : 2018-08-29 10:55:35
     * updateTime : 2018-08-29 10:55:35
     * createBy :
     * updateBy :
     * state : NORMAL
     * sort : 999
     * price : 200
     * title : 4
     * subTitle : 4
     * renewalPrice : 100
     * renewalTime : 1
     * serviceTime : 2
     * preStartTime :30
     * marketPrice
     * remarks
     * introduction : 30
     * serviceDetails :
     * question :
     * type : DEFAULT
     * imgUrl :
     * positionable :true //gps open or close
     */

    private String id;
    private String delFlag;
    private String createTime;
    private String updateTime;
    private String createBy;
    private String updateBy;
    private String state;
    private int sort;
    private int price;
    private String title;
    private String subTitle;
    private int renewalPrice;
    private int renewalTime;
    private int serviceTime;
    private int preStartTime;
    private int marketPrice;
    private String introduction;
    private String serviceDetails;
    private String question;
    private String type;
    private String imgUrl;
    private boolean positionable;
    private String[] remarks;

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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getRenewalPrice() {
        return renewalPrice;
    }

    public void setRenewalPrice(int renewalPrice) {
        this.renewalPrice = renewalPrice;
    }

    public int getRenewalTime() {
        return renewalTime;
    }

    public void setRenewalTime(int renewalTime) {
        this.renewalTime = renewalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public int getPreStartTime() {
        return preStartTime;
    }

    public void setPreStartTime(int preStartTime) {
        this.preStartTime = preStartTime;
    }

    public int getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(int marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(String serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isPositionable() {
        return positionable;
    }

    public void setPositionable(boolean positionable) {
        this.positionable = positionable;
    }

    public String[] getRemarks() {
        return remarks;
    }

    public void setRemarks(String[] remarks) {
        this.remarks = remarks;
    }
}
