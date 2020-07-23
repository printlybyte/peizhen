package com.yinfeng.wypzh.bean.realmbean;

import com.yinfeng.wypzh.bean.TipBaseBean;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

/**
 * @author Asen
 */
//@RealmClass
//public class SickBean extends TipBaseBean implements Serializable, RealmModel {
public class SickBean extends RealmObject  implements Serializable {


    /**
     * id : fc96435ac8dd4f27bad7c03d60216656
     * delFlag : 0
     * createTime : 2018-08-28 15:52:38
     * updateTime : 2018-08-28 15:52:38
     * state : NORMAL
     * sort : 999
     * name : 其他
     */

    @PrimaryKey
    private String id;
    private String delFlag;
    @Ignore
    private String createTime;//不存储
    @Ignore
    private String updateTime;
    private String state;
    private int sort;
    @Required
    private String name;//非空

    @Ignore
    private boolean isSelected = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

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
