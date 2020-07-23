package com.yinfeng.wypzh.bean.patient;

/**
 * @author Asen
 */
public class PatientListParam {
    private int pageNum;
    private int pageSize;

    public PatientListParam(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;

    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
