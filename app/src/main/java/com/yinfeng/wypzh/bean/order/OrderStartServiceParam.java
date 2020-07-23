package com.yinfeng.wypzh.bean.order;

import java.io.Serializable;

/**
 * @author Asen
 */
public class OrderStartServiceParam implements Serializable {
    public OrderStartServiceParam() {
    }

    public OrderStartServiceParam(String id) {
        this.id = id;
    }

    private String id;
    private String serviceLat;
    private String serviceLon;
    private String servicePosition;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceLat() {
        return serviceLat;
    }

    public void setServiceLat(String serviceLat) {
        this.serviceLat = serviceLat;
    }

    public String getServiceLon() {
        return serviceLon;
    }

    public void setServiceLon(String serviceLon) {
        this.serviceLon = serviceLon;
    }

    public String getServicePosition() {
        return servicePosition;
    }

    public void setServicePosition(String servicePosition) {
        this.servicePosition = servicePosition;
    }
}
