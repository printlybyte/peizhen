package com.yinfeng.wypzh.bean.patient;

import java.io.Serializable;
import java.util.List;

/**
 * @author Asen
 */
public class HospitalListData implements Serializable {
    private int total;//list 条目总数
    private List<HospitalBean> list;
    private boolean isFirstPage;
    private boolean isLastPage;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<HospitalBean> getList() {
        return list;
    }

    public void setList(List<HospitalBean> list) {
        this.list = list;
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
}
