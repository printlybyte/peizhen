package com.yinfeng.wypzh.bean.patient;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class PatientInfo implements Serializable, MultiItemEntity {
    /**
     * id : d713ab0a473543a7b8efc4f653b77648
     * delFlag : 0
     * createTime : 2018-08-24 18:02:47
     * updateTime : 2018-08-24 18:02:47
     * createBy :
     * updateBy :
     * state : NORMAL
     * sort : 999
     * name : 就诊人2
     * phone : 15806699526
     * idcard : 123445566
     * isHistory : 1 是否有既往病史 0无 1有
     * medicalHistory : 病种1,病种2
     * memberId : 123456
     * type : 0  //0自己 1他人
     * otherMedical : 其他病史描述
     * sex : "0"
     */

    private String id;
    private String delFlag;
    private String createTime;
    private String updateTime;
    private String createBy;
    private String updateBy;
    private String state;
    private String name;
    private String phone;
    private String idcard;
    private String isHistory;//是否有既往病史 0无 1有
    private String medicalHistory;
    private String memberId;
    private String type; //0自己 1他人
    private String otherMedical;
    private String sex;//0男 1女

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(String isHistory) {
        this.isHistory = isHistory;
    }

    @Override
    public int getItemType() {
        return Integer.parseInt(this.type);
    }
}
