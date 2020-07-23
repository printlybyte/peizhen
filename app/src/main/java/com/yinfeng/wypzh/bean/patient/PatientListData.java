package com.yinfeng.wypzh.bean.patient;

import java.io.Serializable;
import java.util.List;

/**
 * @author Asen
 */
public class PatientListData implements Serializable {

    private int total;//全部的数据，非list.size
    private boolean isFirstPage;
    private boolean isLastPage;
    private List<PatientInfo> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public List<PatientInfo> getList() {
        return list;
    }

    public void setList(List<PatientInfo> list) {
        this.list = list;
    }
}
