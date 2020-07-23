package com.yinfeng.wypzh.bean.waiter;

import java.io.Serializable;

/**
 * @author Asen
 */
public class WaiterInfo implements Serializable {

    /**
     * id : fc972ba932eb423696188b176711
     * delFlag : 0
     * createTime : 2018-08-24 10:52:36
     * updateTime : 2018-08-24 10:52:42
     * createBy :
     * updateBy :
     * state : NORMAL
     * sort : 999
     * accountId : fc972ba932eb423696188b17e82cf58e
     * realname : 陪诊员4
     * age : 17
     * phone : 18767837628
     * gender : 1
     * idCard : 37817261738291837123
     * frontCardImg : https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=689994403,3171891888&fm=26&gp=0.jpg
     * backCardImg : https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1535538651560&di=b3c70daa8c264cc1b61320f1804fcba8&imgtype=0&src=http://news.cnr.cn/native/gd/20180715/W020180715495508331594.jpg
     * serviceSum : 2
     * appraiseStart : 5
     * level :
     * online : 1
     * servicedDescription : 测试服务说明
     * os : Android
     * profile :
     */

    private String id;
    private String delFlag;
    private String createTime;
    private String updateTime;
    private String createBy;
    private String updateBy;
    private String state;
    private int sort;
    private String accountId;
    private String realname;
//    private int age;
    private String age;
    private String phone;
    private String gender;
    private String idCard;
    private String frontCardImg;
    private String backCardImg;
    private int serviceSum;
    private int appraiseStart;
    private String level;
    private String online;
    private String serviceDescription;
    private String os;
    private String profile;

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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getFrontCardImg() {
        return frontCardImg;
    }

    public void setFrontCardImg(String frontCardImg) {
        this.frontCardImg = frontCardImg;
    }

    public String getBackCardImg() {
        return backCardImg;
    }

    public void setBackCardImg(String backCardImg) {
        this.backCardImg = backCardImg;
    }

    public int getServiceSum() {
        return serviceSum;
    }

    public void setServiceSum(int serviceSum) {
        this.serviceSum = serviceSum;
    }

    public int getAppraiseStart() {
        return appraiseStart;
    }

    public void setAppraiseStart(int appraiseStart) {
        this.appraiseStart = appraiseStart;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
