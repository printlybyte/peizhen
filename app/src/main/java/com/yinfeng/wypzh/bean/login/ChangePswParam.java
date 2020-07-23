package com.yinfeng.wypzh.bean.login;

import java.io.Serializable;

/**
 * @author Asen
 */
public class ChangePswParam implements Serializable {
    private String code;
    private String newPsw;
    private String mobile;
    private String userType = "MEMBER";


    public ChangePswParam(String mobile, String newPsw, String code) {
        this.mobile = mobile;
        this.code = code;
        this.newPsw = newPsw;
    }
}
