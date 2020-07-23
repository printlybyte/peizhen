package com.yinfeng.wypzh.bean.message;

import java.io.Serializable;
import java.util.List;

/**
 * @author Asen
 */
public class PushMessageListResult implements Serializable {
    private List<PushMessage> list;

    public List<PushMessage> getList() {
        return list;
    }

    public void setList(List<PushMessage> list) {
        this.list = list;
    }
}
