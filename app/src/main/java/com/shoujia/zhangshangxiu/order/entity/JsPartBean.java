package com.shoujia.zhangshangxiu.order.entity;

import android.text.TextUtils;

public class JsPartBean {
    public String cb;
    public String cd;
    public String ck;
    public String cx;
    public String pjbm;
    public String pjmc;
    public String sl;
    public String ssj;
    public String xh;


    public String getSl() {
        if(TextUtils.isEmpty(sl)){
            return "1";
        }
        if(Float.parseFloat(sl)<=0){
            return "1";
        }
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }
}
