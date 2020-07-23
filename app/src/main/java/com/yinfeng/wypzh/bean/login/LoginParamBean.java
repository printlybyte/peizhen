package com.yinfeng.wypzh.bean.login;

import com.yinfeng.wypzh.http.common.ApiContents;

public class LoginParamBean {
    private String username;
    private String password;
    private String userType = ApiContents.USER_TYPE;
    private String code;
    public LoginParamBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginParamBean() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
