package com.yinfeng.wypzh.bean.patient;

import com.yinfeng.wypzh.bean.realmbean.SickBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Asen
 */
public class SickListData implements Serializable {
    private List<SickBean> list;

    public List<SickBean> getList() {
        return list;
    }

    public void setList(List<SickBean> list) {
        this.list = list;
    }
}
