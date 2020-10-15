package com.shoujia.zhangshangxiu.performance;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseActivity;
import com.shoujia.zhangshangxiu.db.DBManager;
import com.shoujia.zhangshangxiu.dialog.OrderDeleteDialog;
import com.shoujia.zhangshangxiu.dialog.OrderXsdEditDialog;
import com.shoujia.zhangshangxiu.entity.CheckBeanInfo;
import com.shoujia.zhangshangxiu.entity.OrderXsdInfo;
import com.shoujia.zhangshangxiu.entity.RepairInfo;
import com.shoujia.zhangshangxiu.home.help.HomeDataHelper;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.order.entity.OrderBeanInfo;
import com.shoujia.zhangshangxiu.performance.adapter.KeHuXsdProAdapter;
import com.shoujia.zhangshangxiu.performance.entity.XsdDetailInfo;
import com.shoujia.zhangshangxiu.project.ProjectSelectActivity;
import com.shoujia.zhangshangxiu.support.InfoSupport;
import com.shoujia.zhangshangxiu.support.NavSupport;
import com.shoujia.zhangshangxiu.util.CheckUtil;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.DateUtil;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;
import com.shoujia.zhangshangxiu.util.Util;
import com.shoujia.zhangshangxiu.view.CustomDatePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23 0023.
 * 首页
 */
public class XsdQueryDetailActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "ProjectOrderActivity";
    private SharePreferenceManager sp;
    KeHuXsdProAdapter mPeijianAdapter;
    ListView listview2;

    TextView peijianku,car_home_page,save_kehu,total_jiesuan2,car_home_page2;
    TextView select_date_start,select_date_end;
    boolean isToPaigong;
    String mXsdId="";

    private boolean isToPeijian;
    private boolean isToJiesuan;
    InfoSupport infoSupport;
    List<XsdDetailInfo> xsdInfos=new ArrayList<>();
    String mFrom="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_xsd_query);
        mXsdId = getIntent().getStringExtra("xsdId");
        mFrom = getIntent().getStringExtra("from");
        initView();
        initData();
        //getProjectListData();
        getListInfo();
    }

    private void initView() {
        sp = new SharePreferenceManager(this);
        new NavSupport(this, 22);
        listview2 = findViewById(R.id.listview2);
        peijianku = findViewById(R.id.peijianku);
        car_home_page = findViewById(R.id.car_home_page);
        car_home_page2 = findViewById(R.id.car_home_page2);
        save_kehu = findViewById(R.id.save_kehu);
        total_jiesuan2 = findViewById(R.id.total_jiesuan2);
        select_date_start = findViewById(R.id.select_date_start);
        select_date_end = findViewById(R.id.select_date_end);
        String endDate = DateUtil.getCurrentDate();
        String startDate = endDate.substring(0,endDate.length()-2)+"01";
        select_date_end.setText(endDate);
        select_date_start.setText(startDate);
        peijianku.setOnClickListener(this);
        car_home_page.setOnClickListener(this);
        car_home_page2.setOnClickListener(this);
        total_jiesuan2.setOnClickListener(this);
        select_date_start.setOnClickListener(this);
        select_date_end.setOnClickListener(this);
        save_kehu.setOnClickListener(this);
        infoSupport = new InfoSupport(this);
        mPeijianAdapter = new KeHuXsdProAdapter(this, xsdInfos,mFrom);
        listview2.setAdapter(mPeijianAdapter);

        View headView2 = View.inflate(this, R.layout.project_peijian_head, null);
        if("chakehu".equals(mFrom)){
            headView2.findViewById(R.id.caozuo).setVisibility(View.GONE);
        }
        listview2.addHeaderView(headView2);
    }

    //初始化数据
    private void initData() {
        OrderBeanInfo.allBtnUnable = false;
        OrderBeanInfo.notDelete = false;
        DBManager db = DBManager.getInstanse(this);
        List<RepairInfo> repairInfos = db.queryRepairListData();
        if(repairInfos!=null&&repairInfos.size()>0) {
            HomeDataHelper homeDataHelper = new HomeDataHelper(this);
            homeDataHelper.getPersonRepairList(new HomeDataHelper.InsertDataListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail() {

                }
            });
        }
    }

    @Override
    protected void updateUIThread(int msgInt) {
        super.updateUIThread(msgInt);
        if(msgInt==109){
            TextView kehumingcheng = findViewById(R.id.kehumingcheng);
            TextView kehuzongjine = findViewById(R.id.kehuzongjine);
            TextView xiaoshoudanhao = findViewById(R.id.xiaoshoudanhao);
            kehumingcheng.setText("客户名称："+mCustomerName);
            xiaoshoudanhao.setText("销售单号："+mXsdId);
            kehuzongjine.setText("总金额："+mCustomerMoney+"元");
            getXsdInfo();
        }else if(msgInt==10){
            if(mPeijianAdapter!=null) {
                mPeijianAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setBtnEnble(boolean isEnble){

        peijianku.setBackgroundColor(Color.parseColor(isEnble?"#89c997":"#cccccc"));
        peijianku.setEnabled(isEnble);
    }


    OrderXsdInfo mOrderXsdInfo=null;
    private void getListInfo() {
        //{“db”:”mycon1”,”function”:”sp_fun_down_sales_order”,”xs_id”:” AXS2008080001”}
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_down_sales_order");
        dataMap.put("xs_id", mXsdId);
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    Log.d("onSuccess--json", json);
                    System.out.println("11111");
                    Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                    String state = (String) resMap.get("state");
                    if ("ok".equals(state)) {
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
                    } else {

                    }
                }catch (Exception e){
                    e.printStackTrace();
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
            case R.id.peijianku:
                CheckBeanInfo beanInfo4 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                if(beanInfo4!=null && "1".equals(beanInfo4.getNew_bill())) {

                }else{
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }
                if(TextUtils.isEmpty(mXsdId)){
                    toastMsg = "请先选择客户";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }
                Intent intent2 = new Intent(XsdQueryDetailActivity.this,KeHuPeijianSelectActivity.class);
                intent2.putExtra("xsdId",mXsdId);
                startActivityForResult(intent2,101);
                break;
            case R.id.project_ck:
                CheckBeanInfo beanInfo3 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                if(beanInfo3!=null && "1".equals(beanInfo3.getNew_bill())) {

                }else{
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }
                startActivity(new Intent(XsdQueryDetailActivity.this,ProjectSelectActivity.class));
                break;
            case R.id.car_home_page:
            case R.id.car_home_page2:
                startActivityForResult(new Intent(XsdQueryDetailActivity.this,KeHuQueryActivity.class),100);
                break;
            case R.id.paigong:
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    String mCustomerId = "";
    String mCustomerName = "";
    String mCustomerMoney = "";

    private void getXsdInfo(){
        //{“db”:”mycon1”,”function”:”sp_fun_upload_sales_order_detail”,”xs_id”:” AXS2008080001”}
        xsdInfos.clear();
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
                        xsdInfos.addAll(dataList);
                    }


                } else {
                    if (resMap.get("msg") != null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    } else {
                        toastMsg = "网络异常";
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }
                }
                mHandler.sendEmptyMessage(10);
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
