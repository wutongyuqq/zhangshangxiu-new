package com.shoujia.zhangshangxiu.entity;

public class CheckBeanInfo {
    /**
     * "menu_right": "10600",
     * 		"new": "1",
     * 		"del": "1",
     * 		"sh": "1",
     * 		"modify": "1",
     * 		"rz": "1",
     * 		"open": "1"
     */

    private String menu_right;
    private String rz;
    private String open;
    private String modify;
    private String del;
    private String sh;
    private String new_bill;

    public String getMenu_right() {
        return menu_right;
    }

    public void setMenu_right(String menu_right) {
        this.menu_right = menu_right;
    }

    public String getRz() {
        return rz;
    }

    public void setRz(String rz) {
        this.rz = rz;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getModify() {
        return modify;
    }

    public void setModify(String modify) {
        this.modify = modify;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public String getSh() {
        return sh;
    }

    public void setSh(String sh) {
        this.sh = sh;
    }

    public String getNew_bill() {
        return new_bill;
    }

    public void setNew_bill(String new_bill) {
        this.new_bill = new_bill;
    }
}
