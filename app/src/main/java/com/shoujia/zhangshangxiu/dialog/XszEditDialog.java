package com.shoujia.zhangshangxiu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.home.help.XszBean;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;
import com.shoujia.zhangshangxiu.util.Util;

import java.util.HashMap;
import java.util.Map;

public class XszEditDialog {
    private Context mContext;
    Dialog mDialog;
    private OnClickListener onClickListener;
    XszBean  mBean;
    private EditText edit_cp,edit_cllx,edit_syr,edit_zz,edit_syxz,edit_clsb,edit_zcrq,edit_fzjg,edit_fzrq,edit_ppxh,edit_fdjh;
    TextView btn_tjjd,btn_scan;
    SharePreferenceManager sp;

    public XszEditDialog(Context context, XszBean bean) {
        this.mContext = context;
        mBean = bean;
        sp = new SharePreferenceManager(context.getApplicationContext());

    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    public void show() {
        //1、使用Dialog、设置style
        mDialog = new Dialog(mContext, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(mContext, R.layout.dialog_xsz_edit, null);
        final TextView edit_name = view.findViewById(R.id.edit_name);
        edit_cp = view.findViewById(R.id.edit_cp);
        btn_tjjd = view.findViewById(R.id.btn_tjjd);
        edit_cllx = view.findViewById(R.id.edit_cllx);
        edit_syr = view.findViewById(R.id.edit_syr);
        edit_zz = view.findViewById(R.id.edit_zz);
        edit_syxz = view.findViewById(R.id.edit_syxz);
        edit_clsb = view.findViewById(R.id.edit_clsb);
        edit_zcrq = view.findViewById(R.id.edit_zcrq);
        edit_fzjg = view.findViewById(R.id.edit_fzjg);
        edit_fzrq = view.findViewById(R.id.edit_fzrq);
        edit_ppxh = view.findViewById(R.id.edit_ppxh);
        edit_fdjh = view.findViewById(R.id.edit_fdjh);
        btn_scan = view.findViewById(R.id.btn_scan);
        edit_cp.setText(mBean.getHphmStr());
        edit_cllx.setText(mBean.getCllxStr());
        edit_syr.setText(mBean.getSyrStr());
        edit_zz.setText(mBean.getZzStr());
        edit_syxz.setText(mBean.getSyxzStr());
        edit_clsb.setText(mBean.getClsbdhStr());
        edit_zcrq.setText(mBean.getZcdjrqStr());
        edit_fzrq.setText(mBean.getFzrqStr());
        edit_ppxh.setText(mBean.getPpxhStr());
        edit_fdjh.setText(mBean.getFdjhmStr());

        mDialog.setContentView(view);
        Window window = mDialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.show();
        btn_tjjd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //接口请求
                if(mBean==null){
                    mBean=new XszBean();
                }
                mBean.setHphmStr(getTextStr(edit_cp));
                mBean.setCllxStr(getTextStr(edit_cllx));
                mBean.setSyrStr(getTextStr(edit_syr));
                mBean.setZzStr(getTextStr(edit_zz));
                mBean.setSyxzStr(getTextStr(edit_syxz));
                mBean.setClsbdhStr(getTextStr(edit_clsb));
                mBean.setZcdjrqStr(getTextStr(edit_zcrq));
                mBean.setFzrqStr(getTextStr(edit_fzrq));
                mBean.setPpxhStr(getTextStr(edit_ppxh));
                mBean.setFdjhmStr(getTextStr(edit_fdjh));
                updataXszInfo();

            }
        });
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onScan();
                dismiss();
            }
        });

    }



    private String getTextStr(EditText editText){
        if(editText.getText()!=null){
            return editText.getText().toString();
        }
        return "";
    }




    private void updataXszInfo(){

        /**
         * {"db":"mycon1","function":"sp_fun_upload_customer_info_xsz",
         * "company_code":"A","plate_number":"浙G3G821","cz":"车主张三",
         * "mobile":"13400500515","phone":"","linkman":"","custom5":"","cx":"",
         * "cjhm":"","fdjhm":"","oprater_code":"superuser",
         * ”cllx”:”车辆类型”,”address”:”地址”,”syxz”:”使用性质”,
         * ”fzjg”:”发证机关”,”zcdjrq”:”注册登记日期”,”fzrq”:”发证日期”}
         */
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_upload_customer_info_xsz");
        dataMap.put("company_code", sp.getString(Constance.COMP_CODE));
        dataMap.put("operater_code",  sp.getString(Constance.USERNAME));
        dataMap.put("plate_number", mBean.getHphmStr());
        dataMap.put("cz", mBean.getSyrStr());
        dataMap.put("mobile", "");
        dataMap.put("phone", "");
        dataMap.put("linkman", "");
        dataMap.put("custom5", "");
        dataMap.put("cx", mBean.getPpxhStr());//需要填写
        dataMap.put("cjhm", mBean.getClsbdhStr());//需要填写
        dataMap.put("fdjhm", mBean.getFdjhmStr());//需要填写
        dataMap.put("cllx", mBean.getCllxStr());//需要填写
        dataMap.put("address", mBean.getZzStr());
        dataMap.put("syxz", mBean.getSyxzStr());
        dataMap.put("fzjg", "");
        dataMap.put("zcdjrq", mBean.getZcdjrqStr());
        dataMap.put("fzrq", mBean.getFzrqStr());

        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                dismiss();
                onClickListener.onSuccess(mBean,json);


            }

            @Override
            public void onFail() {
                onClickListener.onFail();

            }
        });
    }



    public void dismiss(){
        if(mDialog!=null) {
            mDialog.dismiss();
        }
    }



    public void setData(GridLayout gridLayout) {

    }

    public interface OnClickListener {
        void onSuccess(XszBean beann,String json);
        void onFail();
        void onScan();
    }

}
