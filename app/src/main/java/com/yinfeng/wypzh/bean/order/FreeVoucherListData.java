package com.yinfeng.wypzh.bean.order;

import java.io.Serializable;
import java.util.List;

/**
 * @author Asen
 */
public class FreeVoucherListData implements Serializable {
    private int total;
    private List<FreeVoucherBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<FreeVoucherBean> getList() {
        return list;
    }

    public void setList(List<FreeVoucherBean> list) {
        this.list = list;
    }
}
