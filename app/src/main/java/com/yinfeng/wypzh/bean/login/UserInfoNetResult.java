package com.yinfeng.wypzh.bean.login;

import com.google.gson.annotations.SerializedName;
import com.yinfeng.wypzh.bean.patient.PatientInfo;

import java.io.Serializable;

/**
 * @author Asen
 */
public class UserInfoNetResult implements Serializable {
    private String profile;//用户头像地址
    @SerializedName("nickname")
    private String name;
    private String phone;
    private String idcard;
    private String accountId;
    private String sex;//0男 1女
    private String level;
    private PatientInfo memberPatient;


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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public PatientInfo getMemberPatient() {
        return memberPatient;
    }

    public void setMemberPatient(PatientInfo memberPatient) {
        this.memberPatient = memberPatient;
    }
}

