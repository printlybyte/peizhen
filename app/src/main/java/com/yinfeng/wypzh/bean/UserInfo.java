package com.yinfeng.wypzh.bean;

import java.io.Serializable;

/**
 * @author Asen
 */
public class UserInfo implements Serializable {
    private String userID;//用户登录手机号，登录时存入
    private String token;
    private boolean isComplete;//是否已完善个人信息
    private String profile;//用户头像地址
    private String name;
    private String phone;
    private String idcard;
    private String isHistory;//是否有既往病史 0无 1有
    private String medicalHistory;
    private String memberId;
    //    private String type; //0自己 1他人
    private String otherMedical;
    private String sex;//0男 1女
    private String level;
    private String accountId;//获取分享url /umeng别名

    private String imAccount;
    private String imToken;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(String isHistory) {
        this.isHistory = isHistory;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

    public String getOtherMedical() {
        return otherMedical;
    }

    public void setOtherMedical(String otherMedical) {
        this.otherMedical = otherMedical;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getimAccount() {
        return imAccount;
    }

    public void setimAccount(String imAccount) {
        this.imAccount = imAccount;
    }

    public String getImToken() {
        return imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }
}
