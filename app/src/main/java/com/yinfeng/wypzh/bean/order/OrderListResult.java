package com.yinfeng.wypzh.bean.order;

import java.io.Serializable;
import java.util.List;

/**
 * @author Asen
 */
public class OrderListResult implements Serializable {
    private int total;
    private List<OrderDetailBean> list;
    private boolean isFirstPage;
    private boolean isLastPage;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<OrderDetailBean> getList() {
        return list;
    }

    public void setList(List<OrderDetailBean> list) {
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
