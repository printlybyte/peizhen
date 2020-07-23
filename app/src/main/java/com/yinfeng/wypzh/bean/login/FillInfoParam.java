package com.yinfeng.wypzh.bean.login;

import java.io.Serializable;

/**
 * @author Asen
 */
public class FillInfoParam implements Serializable {
    private String nickname;
    private String sex;
    private String idCard;
    private String profile;
    private FillInfoHistoryParam memberPatient;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public FillInfoHistoryParam getMemberPatient() {
        return memberPatient;
    }

    public void setMemberPatient(FillInfoHistoryParam memberPatient) {
        this.memberPatient = memberPatient;
    }
}
