package com.yinfeng.wypzh.bean.waiter;

import java.io.Serializable;
import java.util.List;

/**
 * @author Asen
 */
public class CommentListResult implements Serializable {
    private List<CommentBean> list;

    public List<CommentBean> getList() {
        return list;
    }

    public void setList(List<CommentBean> list) {
        this.list = list;
    }
}
