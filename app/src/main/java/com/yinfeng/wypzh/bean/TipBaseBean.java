package com.yinfeng.wypzh.bean;

public class TipBaseBean {
    private String name;
    private boolean isSelected = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
