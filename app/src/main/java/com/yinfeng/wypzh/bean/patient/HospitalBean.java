package com.yinfeng.wypzh.bean.patient;

import java.io.Serializable;

public class HospitalBean implements Serializable {

    /**
     * name : 测试添加
     * address : 123123
     * imgUrl : http://madou.oss-cn-beijing.aliyuncs.com/2018-09-03/a757dba4f1d34fe09de5f97e46d2a4bf.png
     * brief : 123
     * lat : 37.633752
     * lon : 117.162707
     * type : DEFAULT
     * id : 1f5cd588b22e4b98ae1da781549e822a
     * delFlag : 0
     * createTime : 2018-08-29 16:50:23
     * updateTime : 2018-08-29 16:50:23
     * createBy :
     * updateBy :
     * state : NORMAL
     * sort : 999
     */

    private String name;
    private String address;
    private String imgUrl;
    private String brief;
    private String lat;
    private String lon;
    private String type;
    private String id;
    private String delFlag;
    private String createTime;
    private String updateTime;
    private String createBy;
    private String updateBy;
    private String state;
    private int sort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
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
}
