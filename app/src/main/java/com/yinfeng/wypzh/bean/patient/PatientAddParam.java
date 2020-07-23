package com.yinfeng.wypzh.bean.patient;

import java.io.Serializable;

/**
 * @author Asen
 */
public class PatientAddParam implements Serializable {
    protected String name;
    protected String phone;
    protected int isHistory;//	是否有既往病史 0无 1有
    protected String idcard;
    protected String medicalHistory;//既往病史 多个逗号隔开
    protected String otherMedical;
    protected int sex;//0男 1女

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

    public int getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(int isHistory) {
        this.isHistory = isHistory;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getOtherMedical() {
        return otherMedical;
    }

    public void setOtherMedical(String otherMedical) {
        this.otherMedical = otherMedical;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
