package com.shoujia.zhangshangxiu.entity;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/2/27 0027.
 */
public class PeijianBean {
	private String cb;
	private String cd;
	private String ck;
	private String cx;
	private String pjbm;
	private String pjmc;
	private String sl;
	private String ssj;
	private String xh;
	private boolean isSaveNewPrice;

	public String getCb() {
		return cb;
	}

	public void setCb(String cb) {
		this.cb = cb;
	}

	public String getCd() {
		return cd;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public String getCk() {
		return ck==null?"":ck;
	}

	public void setCk(String ck) {
		this.ck = ck;
	}

	public String getCx() {
		return cx;
	}

	public void setCx(String cx) {
		this.cx = cx;
	}

	public String getPjbm() {
		return pjbm==null?"":pjbm;
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

	public String getSsj() {
		if(TextUtils.isEmpty(ssj)){
			return "0";
		}
		return ssj;
	}

	public void setSsj(String ssj) {
		this.ssj = ssj;
	}

	public String getXh() {
		return TextUtils.isEmpty(xh)?"0":xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public boolean isSaveNewPrice() {
		return isSaveNewPrice;
	}

	public void setSaveNewPrice(boolean saveNewPrice) {
		isSaveNewPrice = saveNewPrice;
	}
}
