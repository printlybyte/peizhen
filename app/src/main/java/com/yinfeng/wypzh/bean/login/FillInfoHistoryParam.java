package com.yinfeng.wypzh.bean.login;

import java.io.Serializable;

/**
 * @author Asen
 */
public class FillInfoHistoryParam implements Serializable {
    private String isHistory;
    private String medicalHistory;
    private String otherMedical;

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

    public String getOtherMedical() {
        return otherMedical;
    }

    public void setOtherMedical(String otherMedical) {
        this.otherMedical = otherMedical;
    }
}
