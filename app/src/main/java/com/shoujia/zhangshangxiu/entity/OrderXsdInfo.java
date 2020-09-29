package com.shoujia.zhangshangxiu.entity;

import android.text.TextUtils;

import java.io.Serializable;

public class OrderXsdInfo implements Serializable {
    String customer_name;
    String customer_id;
    String zje;
    String xs_id;


    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getZje() {
        return zje;
    }

    public void setZje(String zje) {
        this.zje = zje;
    }

    public String getXs_id() {
        return xs_id;
    }

    public void setXs_id(String xs_id) {
        this.xs_id = xs_id;
    }

    @Override
    public String toString() {
        return "OrderXsdInfo{" +
                "customer_name='" + customer_name + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", zje='" + zje + '\'' +
                ", xs_id='" + xs_id + '\'' +
                '}';
    }
}
