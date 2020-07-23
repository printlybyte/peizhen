package com.yinfeng.wypzh.bean.login;

import java.io.Serializable;

/**
 * @author Asen
 */
public class IMLogInfo implements Serializable {
    private String token;
    private String accid;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccid() {
        return accid;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }
}
