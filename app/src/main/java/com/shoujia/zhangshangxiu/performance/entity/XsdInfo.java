package com.shoujia.zhangshangxiu.performance.entity;

import android.text.TextUtils;

public class XsdInfo {
    private String xh;
    private String xs_id;
    private String khmc;
    private String pjbm;
    private String pjmc;
    private String cd;
    private String dw;
    private String rq;
    private String sl;
    private String ssj;
    private String je;
    private String dj;

    public String getXs_id() {
        return xs_id;
    }

    public void setXs_id(String xs_id) {
        this.xs_id = xs_id;
    }

    public String getKhmc() {
        return khmc;
    }

    public void setKhmc(String khmc) {
        this.khmc = khmc;
    }

    public String getPjbm() {
        return pjbm;
    }

    public void setPjbm(String pjbm) {
        this.pjbm = pjbm;
    }

    public String getPjmc() {
        return pjmc;
    }

    public void setPjmc(String pjmc) {
        this.pjmc = pjmc;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getDw() {
        return dw;
    }

    public void setDw(String dw) {
        this.dw = dw;
    }

    public String getRq() {
        return rq;
    }

    public void setRq(String rq) {
        this.rq = rq;
    }

    public String getSl() {
        return TextUtils.isEmpty(sl)?"0":sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getSsj() {
        return  TextUtils.isEmpty(ssj)?"0":ssj;
    }

    public void setSsj(String ssj) {
        this.ssj = ssj;
    }

    public String getJe() {
        return TextUtils.isEmpty(je)?"0":je;
    }

    public void setJe(String je) {
        this.je = je;
    }

    public String getDj() {
        return dj;
    }

    public void setDj(String dj) {
        this.dj = dj;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }
}
