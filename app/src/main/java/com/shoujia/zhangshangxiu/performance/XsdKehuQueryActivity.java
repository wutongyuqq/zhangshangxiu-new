package com.shoujia.zhangshangxiu.performance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
public class XsdKehuQueryActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "ProjectOrderActivity";
    private SharePreferenceManager sp;
    KeHuXsdProAdapter mPeijianAdapter;
    ListView listview2;
    TextView peijianku,car_home_page,save_kehu,total_jiesuan2,car_home_page2,chakehuBtn;
    TextView select_date_start,select_date_end,chakehuText;
    boolean isToPaigong;
    String mXsdId="";

    private boolean isToPeijian;
    private boolean isToJiesuan;
    InfoSupport infoSupport;
    List<XsdDetailInfo> xsdInfos=new ArrayList<>();
    String mFrom="";
    View query_btn;
    String pre_row_number = "0";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_kehu_xsd_query);
        mXsdId = getIntent().getStringExtra("xsdId");
        mFrom = getIntent().getStringExtra("from");
        initView();
        initData();
        getListInfo();
    }

    private void initView() {
        sp = new SharePreferenceManager(this);
        new NavSupport(this, 22);
        listview2 = findViewById(R.id.listview2);
        peijianku = findViewById(R.id.peijianku);
        car_home_page = findViewById(R.id.car_home_page);
        car_home_page2 = findViewById(R.id.car_home_page2);
        chakehuBtn = findViewById(R.id.chakehuBtn);
        save_kehu = findViewById(R.id.save_kehu);
        total_jiesuan2 = findViewById(R.id.total_jiesuan2);
        select_date_start = findViewById(R.id.select_date_start);
        select_date_end = findViewById(R.id.select_date_end);
        chakehuText = findViewById(R.id.chakehuText);
        query_btn = findViewById(R.id.query_btn);
        chakehuBtn.setOnClickListener(this);
        query_btn.setOnClickListener(this);
        chakehuText.setOnClickListener(this);
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
        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(xsdInfos==null||xsdInfos.size()==0){
                    return;
                }
                Intent intent = new Intent(XsdKehuQueryActivity.this,XsdQueryDetailActivity.class);
                intent.putExtra("xsdId",xsdInfos.get(position-1<0?0:position-1).getXs_id());
                intent.putExtra("from","chakehu");
                startActivity(intent);
            }
        });
    }

    private void selectDate(final TextView textView){
        CustomDatePicker customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                Log.d("yyyyy", time);
                if(!TextUtils.isEmpty(time)&&time.length()>=10){
                    String pickTime = time.substring(0,10);
                    textView.setText(pickTime);
                }
            }
        },"2007-01-01 00:00","2025-12-31 00:00");
        customDatePicker.show();
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
            getXsdInfo();
        }else if(msgInt==10){
            mPeijianAdapter.notifyDataSetChanged();
        }
    }

    private void  getListInfo(){
        if(xsdInfos!=null) {
            xsdInfos.clear();
        }
        pre_row_number = "0";
        //点击软键盘外部，收起软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        getListRealInfo();

    }
    private void getListRealInfo() {
        if(pre_row_number!=null && pre_row_number.equals("end")){
            mHandler.sendEmptyMessage(10);
            return;
        }
        if( select_date_start.getText()==null || select_date_end.getText()==null){
            return;
        }
        String startDate = select_date_start.getText().toString();
        String endDate = select_date_end.getText().toString();
        //{“db”:”mycon1”,”function”:”sp_fun_down_sales_order”,”xs_id”:” AXS2008080001”}
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_serch_sales_order");
        dataMap.put("customer_id", mCustomerId);
        dataMap.put("dates", startDate+" 00:00:00");
        dataMap.put("datee", endDate+" 23:59:00");
        dataMap.put("pre_row_number", pre_row_number);
        dataMap.put("comp_code", sp.getString(Constance.COMP_CODE));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    System.out.println("11111");
                    Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                    String state = (String) resMap.get("state");
                    if ("ok".equals(state)) {
                        pre_row_number = (String) resMap.get("pre_row_number");
                        JSONArray dataArray = (JSONArray) resMap.get("data");
                        List<XsdDetailInfo> dataList = JSONArray.parseArray(dataArray.toJSONString(), XsdDetailInfo.class);
                        if(dataList!=null && dataList.size()>0) {
                            xsdInfos.addAll(dataList);
                        }
                        getListRealInfo();

                    } else {
                        if (resMap.get("msg") != null) {
                            toastMsg = (String) resMap.get("msg");
                            mHandler.sendEmptyMessage(TOAST_MSG);
                        } else {
                            toastMsg = "网络异常";
                            mHandler.sendEmptyMessage(TOAST_MSG);
                        }
                        mHandler.sendEmptyMessage(10);
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
                if(TextUtils.isEmpty(mXsdId)){
                    toastMsg = "请先选择客户";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }
                Intent intent2 = new Intent(XsdKehuQueryActivity.this,KeHuPeijianSelectActivity.class);
                intent2.putExtra("xsdId",mXsdId);
                startActivityForResult(intent2,101);
                break;
            case R.id.project_ck:

                startActivity(new Intent(XsdKehuQueryActivity.this,ProjectSelectActivity.class));

                break;
            case R.id.car_home_page:
            case R.id.car_home_page2:
            case R.id.chakehuBtn:
            case R.id.chakehuText:

                startActivityForResult(new Intent(XsdKehuQueryActivity.this,KeHuQueryActivity.class),100);
                break;
            case R.id.paigong:

                //judgeToStatu();

                break;

            case R.id.select_date_start:
                selectDate(select_date_start);
                break;
            case R.id.select_date_end:
                selectDate(select_date_end);
                break;
            case R.id.query_btn:
                getListInfo();
                break;
            default:

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==100){
            Bundle bundle = data.getExtras();
            String customer_id=bundle.getString("customer_id");//str即为回传的值
            String customer_name=bundle.getString("customer_name");//str即为回传的值
            mCustomerName = customer_name;
            mCustomerId = customer_id;
            chakehuText.setText(customer_name);
            getListInfo();
        }
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
        mPeijianAdapter = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isToPeijian) {
            isToPeijian = false;
        }
        if(isToPaigong){
            isToPaigong = false;

            //getProjectListData();
        }

        if(isToJiesuan){
            isToJiesuan=false;
        }

    }
}
