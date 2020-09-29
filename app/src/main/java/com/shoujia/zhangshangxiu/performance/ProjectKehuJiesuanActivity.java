package com.shoujia.zhangshangxiu.performance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseActivity;
import com.shoujia.zhangshangxiu.entity.CheckBeanInfo;
import com.shoujia.zhangshangxiu.entity.OrderXsdInfo;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.order.ProjectShouyinActivity;
import com.shoujia.zhangshangxiu.order.entity.JsBaseBean;
import com.shoujia.zhangshangxiu.order.entity.JsCompBean;
import com.shoujia.zhangshangxiu.order.entity.JsPartBean;
import com.shoujia.zhangshangxiu.order.entity.JsXmBean;
import com.shoujia.zhangshangxiu.performance.entity.XsdDetailInfo;
import com.shoujia.zhangshangxiu.support.InfoSupport;
import com.shoujia.zhangshangxiu.support.NavSupport;
import com.shoujia.zhangshangxiu.util.CheckUtil;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.DateUtil;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;
import com.shoujia.zhangshangxiu.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23 0023.
 * 首页
 */
public class ProjectKehuJiesuanActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "ProjectOrderActivity";


    private SharePreferenceManager sp;
    private TextView start_sy,sing_print,double_print,cancle_jiesuan,zongyingshou3,jiaoche_btn;
    JsBaseBean mJsBaseBean = new JsBaseBean();
    JsCompBean mJsCompBean;
    List<XsdDetailInfo> mPartBeans = new ArrayList<>();
    List<JsPartBean> mJsPartBeans = new ArrayList<>();
    double totalXlf = 0;
    double totalZkMoney = 0;
    float totalPartSl = 0;
    double totalPartMoney = 0;
    double totalCb = 0;
    String mXsdId="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pro_kehu_jiesuan);
        initView();
        initData();
        loginPrintPre();
    }

    private void initData() {
        mXsdId = sp.getString(Constance.XSD_ID);
        getBaseData();
        getXsdInfo();
        getBaseData2();
        getCompanyData();
    }
    private void initView() {
        sp = new SharePreferenceManager(this);
        new NavSupport(this, 12);

        new InfoSupport(this);
        start_sy = findViewById(R.id.start_sy);
        sing_print = findViewById(R.id.sing_print);
        double_print = findViewById(R.id.double_print);
        cancle_jiesuan = findViewById(R.id.cancle_jiesuan);
        zongyingshou3 = findViewById(R.id.zongyingshou3);
        jiaoche_btn = findViewById(R.id.jiaoche_btn);
        start_sy.setOnClickListener(this);
        sing_print.setOnClickListener(this);
        double_print.setOnClickListener(this);
        cancle_jiesuan.setOnClickListener(this);
        jiaoche_btn.setOnClickListener(this);
        TextView jsd_id_view = findViewById(R.id.jsd_id);
        TextView factoy_name = findViewById(R.id.factoy_name);
        TextView dytime = findViewById(R.id.dytime);
        jsd_id_view.setText("结算单号："+sp.getString(Constance.XSD_ID));
        factoy_name.setText(sp.getString(Constance.FACTORYNAME));
        dytime.setText("打印时间："+DateUtil.getCurDate());
        String jsdStatuStr = getIntent().getStringExtra("jsdStatu");
        if(TextUtils.isEmpty(jsdStatuStr)){
            return;
        }
        if(jsdStatuStr.equals("审核已结算")||jsdStatuStr.equals("已出厂")){
            start_sy.setClickable(false);
            start_sy.setBackgroundColor(Color.parseColor("#cccccc"));
        }
        jiaoche_btn.setEnabled(false);
        jiaoche_btn.setBackgroundColor(Color.parseColor("#cccccc"));
        //isEnble?"#89c997":
        if(jsdStatuStr.equals("审核已结算")||jsdStatuStr.equals("审核未结算")){
            jiaoche_btn.setEnabled(true);
            jiaoche_btn.setBackgroundColor(Color.parseColor("#89c997"));
        }
    }

    @Override
    protected void updateUIThread(int msgInt) {
        super.updateUIThread(msgInt);
        if(msgInt==101){
            updatePartView();
        }else if(msgInt==103){
            if(mJsCompBean==null){
                return;
            }
            TextView address = findViewById(R.id.address);
            TextView telPhone = findViewById(R.id.telPhone);
            address.setText("地址："+mJsCompBean.address);
            telPhone.setText("电话："+mJsCompBean.telphone);
        }else if(msgInt==201){
            finish();
        }else if(msgInt==109){
            TextView kehumingcheng = findViewById(R.id.custome_name);
            TextView kehuzongjine = findViewById(R.id.zongyingshou3);
            TextView xiaoshoudanhao = findViewById(R.id.jsd_id);
            kehumingcheng.setText(""+mCustomerName);
            xiaoshoudanhao.setText("销售单号："+mXsdId);
            kehuzongjine.setText("总金额："+mCustomerMoney+"元");
        }
    }



    private void getXsdInfo(){
        //{“db”:”mycon1”,”function”:”sp_fun_upload_sales_order_detail”,”xs_id”:” AXS2008080001”}
        mPartBeans.clear();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db",sp.getString(Constance.Data_Source_name));//"asa_to_sql");//sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_down_sales_order_detail");
        dataMap.put("xs_id", mXsdId);
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    JSONArray dataArray = (JSONArray) resMap.get("data");
                    List<XsdDetailInfo> dataList = JSONArray.parseArray(dataArray.toJSONString(), XsdDetailInfo.class);
                    if(dataList!=null && dataList.size()>0) {
                        mPartBeans.addAll(dataList);
                    }
                    mHandler.sendEmptyMessage(TOAST_MSG);

                } else {
                    if (resMap.get("msg") != null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    } else {
                        toastMsg = "网络异常";
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }
                }
                mHandler.sendEmptyMessage(101);
            }

            @Override
            public void onFail() {
                toastMsg = "网络连接异常";
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        });
    }

    private void updatePartView(){
        if(mPartBeans==null||mPartBeans.size()==0){
            return;
        }

        LinearLayout part_lay = findViewById(R.id.part_lay);
        part_lay.removeAllViews();
        for(XsdDetailInfo bean:mPartBeans){
            JsPartBean partBean = new JsPartBean();
            partBean.cb =bean.getJe();
            partBean.ssj = bean.getJe();
            partBean.sl = bean.getSl();
            partBean.pjmc = bean.getPjmc();
            mJsPartBeans.add(partBean);
            View partView = View.inflate(this,R.layout.view_part_item,null);
            String slStr = bean.getSl();
            float tmpSl = Float.parseFloat(slStr);
            totalPartSl+=tmpSl;
            String moneyStr = bean.getJe();
            double tmpDob = Double.parseDouble(moneyStr);
            totalPartMoney+=tmpDob * tmpSl;
            totalCb += Double.parseDouble(TextUtils.isEmpty(bean.getJe())?"0":bean.getJe());
            TextView part_num = partView.findViewById(R.id.part_num);
            TextView money_total = partView.findViewById(R.id.part_money_total);
            TextView part_name = partView.findViewById(R.id.part_name);
            TextView danjia = partView.findViewById(R.id.danjia);
            part_num.setText(Util.getDoubleStr(slStr));
            danjia.setText(Util.getDoubleStr(bean.getJe()));
            money_total.setText(Util.getDoubleStr((tmpDob * tmpSl)+""));
            part_name.setText(bean.getPjmc());
            part_lay.addView(partView);
        }
        View totalView = View.inflate(this,R.layout.view_part_item,null);
        TextView part_num = totalView.findViewById(R.id.part_num);
        TextView danjia = totalView.findViewById(R.id.danjia);
        danjia.setVisibility(View.INVISIBLE);
        part_num.setText(totalPartSl+"");
        TextView money_total = totalView.findViewById(R.id.part_money_total);
        TextView part_name = totalView.findViewById(R.id.part_name);
        part_name.setText("小计");
        money_total.setText(Util.getDoubleStr(totalPartMoney+""));
        part_lay.addView(totalView);
    }

    public void saveDataForLogin(String machine_code,
                                 String msign) {
        SharedPreferences shared_user_info = getSharedPreferences("user_info",
                MODE_PRIVATE);
        shared_user_info.edit().putString("Data_Source",sp.getString(Constance.FACTORYNAME)).commit();
        shared_user_info.edit().putString("machine_code", machine_code)
                .commit();
        shared_user_info.edit().putString("msign", msign).commit();

    }


    private void loginPrintPre(){
        /**
         * 登录
         */

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", "sjsoft_SQL");
        dataMap.put("function", "sp_fun_check_service_validity");
        dataMap.put("data_source", sp.getString(Constance.FACTORYNAME));
        dataMap.put("operater_code", sp.getString(Constance.USERNAME));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("onSuccess--json", json);
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("true".equals(state)) {
                    String machine_code = (String) resMap.get("machine_code");
                    String msign = (String) resMap.get("machine_key");
                    saveDataForLogin(machine_code,msign);
                } else {
                    if(resMap.get("msg")!=null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }else{
                        toastMsg = "网络连接异常";
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }
                }
            }



            @Override
            public void onFail() {
                toastMsg = "网络连接异常";
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        });


    }

    private void printData(){
        if(mJsCompBean!=null){
            mJsBaseBean.compName = mJsCompBean.company_name;
            mJsBaseBean.address = mJsCompBean.address;
            mJsBaseBean.telphone = mJsCompBean.telphone;
        }
        mJsBaseBean.totalPartMoney = totalPartMoney;
        mJsBaseBean.totalPartSl = totalPartSl;
        mJsBaseBean.totalZkMoney = totalZkMoney;
        mJsBaseBean.totalXlf = totalXlf;

        Util.print(this,mJsBaseBean,new ArrayList<JsXmBean>(),mJsPartBeans);
    }

    OrderXsdInfo mOrderXsdInfo=null;
    String mCustomerId = "";
    String mCustomerName = "";
    String mCustomerMoney = "";
    
    private void getBaseData() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_down_sales_order");
        dataMap.put("xs_id", sp.getString(Constance.XSD_ID));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                JSONArray dataArray = (JSONArray) resMap.get("data");
                List<OrderXsdInfo> projectBeans = JSONArray.parseArray(dataArray.toJSONString(), OrderXsdInfo.class);
                if(projectBeans!=null&&projectBeans.size()>0){
                    mOrderXsdInfo = projectBeans.get(0);
                    //getGuzhang();
                    mCustomerName = mOrderXsdInfo.getCustomer_name();
                    mCustomerId = mOrderXsdInfo.getCustomer_id();
                    mCustomerMoney = mOrderXsdInfo.getZje();

                }
                mHandler.sendEmptyMessage(109);
            }

            @Override
            public void onFail() {
                toastMsg = "网络连接异常";
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        });
    }


    private void getBaseData2() {

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_down_repair_list_main");
        dataMap.put("jsd_id", sp.getString(Constance.XSD_ID));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("onSuccess--json", json);
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    JSONArray dataArray = (JSONArray) resMap.get("data");
                    List<JsBaseBean> projectBeans = JSONArray.parseArray(dataArray.toJSONString(), JsBaseBean.class);
                   if(projectBeans!=null&&projectBeans.size()>0) {
                       mJsBaseBean = projectBeans.get(0);
                       //mHandler.sendEmptyMessage(100);
                   }
                } else {
                    if(resMap.get("msg")!=null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }else{
                        toastMsg = "网络连接异常";
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }
                }
            }

            @Override
            public void onFail() {
                toastMsg = "网络连接异常";
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        });
    }



    private void getCompanyData() {

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_get_company_info");
        dataMap.put("company_code", sp.getString(Constance.COMP_CODE));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("onSuccess--json", json);
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    JSONArray dataArray = (JSONArray) resMap.get("data");
                    List<JsCompBean> projectBeans = JSONArray.parseArray(dataArray.toJSONString(), JsCompBean.class);
                    if(projectBeans!=null&&projectBeans.size()>0) {
                        mJsCompBean = projectBeans.get(0);
                        mHandler.sendEmptyMessage(103);
                    }
                } else {
                    if(resMap.get("msg")!=null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }else{
                        toastMsg = "网络连接异常";
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }
                }
            }

            @Override
            public void onFail() {
                toastMsg = "网络连接异常";
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        });
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_sy:
                CheckBeanInfo beanInfo2 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                if(beanInfo2!=null && "1".equals(beanInfo2.getRz())) {

                }else{
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }
                startActivity(new Intent(ProjectKehuJiesuanActivity.this,ProjectKehuShouyinActivity.class));
                //uploadMoney();
               break;
                case R.id.sing_print:
                case R.id.double_print:
                    printData();
                break;
            case R.id.cancle_jiesuan:
                CheckBeanInfo beanInfo5 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10500");
                if(beanInfo5!=null && "1".equals(beanInfo5.getRz())) {

                }else{
                    Toast.makeText(this,"您没有该权限，请联系管理员",Toast.LENGTH_LONG).show();
                    return;
                }


                cancleJiesuan();
                //onBackPressed();
                break;
            case R.id.jiaoche_btn:
                jiaoChe();
                //onBackPressed();
                break;
            default:

                break;
        }
    }

    private void jiaoChe() {
        /*
        * {"db":"mycon1","function":"sp_fun_update_repair_list_state","jsd_id":"A1802260001","states":"已出厂","xm_state":""}
        *
        * */
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_update_repair_list_state");
        dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
        dataMap.put("states", "已出厂");
        dataMap.put("xm_state", "");
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("onSuccess--json", json);
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                toastMsg = (String) resMap.get("msg");
                mHandler.sendEmptyMessage(TOAST_MSG);

            }

            @Override
            public void onFail() {
                toastMsg = "网络连接异常";
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        });


    }

    private void cancleJiesuan() {


        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_delete_skd");
        dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("onSuccess--json", json);
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                toastMsg = (String) resMap.get("msg");
                mHandler.sendEmptyMessage(TOAST_MSG);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    mHandler.sendEmptyMessage(201);
                }

            }

            @Override
            public void onFail() {
                toastMsg = "网络连接异常";
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        });

    }

    private void uploadMoney() {
        ProjectShouyinActivity.totalZk = totalZkMoney+"";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_update_repair_main_money");
        dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
        dataMap.put("zje", Util.getDoubleStr(totalPartMoney+totalXlf+""));
        dataMap.put("wxfzj",  Util.getDoubleStr(totalXlf+"")+"");
        dataMap.put("clfzj",  Util.getDoubleStr(totalPartMoney+"")+"");
        dataMap.put("totalCb",  Util.getDoubleStr(totalCb+"")+"");
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("onSuccess--json", json);
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    startActivity(new Intent(ProjectKehuJiesuanActivity.this,ProjectShouyinActivity.class));

                } else {
                    if(resMap.get("msg")!=null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }else{
                        toastMsg = "网络连接异常";
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }
                }
            }

            @Override
            public void onFail() {
                toastMsg = "网络连接异常";
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        });
    }



    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     
    }


}
