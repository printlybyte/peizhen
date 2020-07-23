package com.yinfeng.wypzh.bean.message;

import java.io.Serializable;
import java.util.List;

/**
 * @author Asen
 */
public class MsgOrderNoticeResult implements Serializable {
    private List<MessageOrderNotice> list;

    public List<MessageOrderNotice> getList() {
        return list;
    }

    public void setList(List<MessageOrderNotice> list) {
        this.list = list;
    }
}
