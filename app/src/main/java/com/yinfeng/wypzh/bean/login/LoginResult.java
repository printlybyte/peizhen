package com.yinfeng.wypzh.bean.login;

import com.yinfeng.wypzh.bean.BaseBean;

import java.io.Serializable;

/**
 * @author Asen
 */
public class LoginResult extends BaseBean<String> implements Serializable {
    private boolean complete;

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
