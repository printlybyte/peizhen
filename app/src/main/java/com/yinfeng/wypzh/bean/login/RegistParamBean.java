package com.yinfeng.wypzh.bean.login;


public class RegistParamBean {
    private String username;
    private String password;
    private String code;

    public RegistParamBean(String username, String password, String code) {
        this.username = username;
        this.password = password;
        this.code = code;
    }

    public RegistParamBean() {
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

}
