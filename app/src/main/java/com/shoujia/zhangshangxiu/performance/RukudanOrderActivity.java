package com.shoujia.zhangshangxiu.performance;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseActivity;
import com.shoujia.zhangshangxiu.db.DBManager;
import com.shoujia.zhangshangxiu.dialog.OrderDeleteDialog;
import com.shoujia.zhangshangxiu.entity.CheckBeanInfo;
import com.shoujia.zhangshangxiu.entity.OrderCarInfo;
import com.shoujia.zhangshangxiu.entity.RepairInfo;
import com.shoujia.zhangshangxiu.home.help.HomeDataHelper;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.order.entity.OrderBeanInfo;
import com.shoujia.zhangshangxiu.performance.adapter.KeHuPejianProAdapter;
import com.shoujia.zhangshangxiu.performance.entity.XsdInfo;
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
public class RukudanOrderActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "ProjectOrderActivity";
    private SharePreferenceManager sp;
    KeHuPejianProAdapter mPeijianAdapter;
    ListView listview2;
    TextView wxfTotal;
    TextView zongyingshou;
    TextView tv_xlfZk;
    TextView pjfTotal;
    TextView zongyingshou2;
    TextView tv_pjfZk;
    TextView peijianku,car_home_page,save_kehu,total_jiesuan2,car_home_page2,total_saomiao;
    String gdStatu="";
    TextView select_date_start,select_date_end;
    boolean isToPaigong;
    private boolean djztUnable = false;
    private boolean notDelete = false;
    private boolean yccType = false;
    private OrderCarInfo mOrderCarInfo;
    private boolean isToPeijian;
    private boolean isToJiesuan;
    InfoSupport infoSupport;
    List<XsdInfo> xsdInfos=new ArrayList<>();
    TextView kehumingcheng,kehuzongjine;
    float totalMoney = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_kehu_order);
        initView();

        initData();
        //getProjectListData();
        getJsdInfo();
        requestPermissionSelf();
    }

    private void requestPermissionSelf() {
        /*申请手机权限，在oncreate()方法中调用*/
            String [] permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.VIBRATE,
            };
            ActivityCompat.requestPermissions(this,permissions,100);
    }

    private void initView() {
        sp = new SharePreferenceManager(this);
        new NavSupport(this, 22);
        listview2 = findViewById(R.id.listview2);
        peijianku = findViewById(R.id.peijianku);
        car_home_page = findViewById(R.id.car_home_page);
        total_saomiao = findViewById(R.id.total_saomiao);
        car_home_page2 = findViewById(R.id.car_home_page2);
        save_kehu = findViewById(R.id.save_kehu);
        total_jiesuan2 = findViewById(R.id.total_jiesuan2);
        select_date_start = findViewById(R.id.select_date_start);
        select_date_end = findViewById(R.id.select_date_end);
         kehumingcheng = findViewById(R.id.kehumingcheng);
         kehuzongjine = findViewById(R.id.kehuzongjine);
        TextView gysView = findViewById(R.id.car_home_page2);
        gysView.setText("供应商");
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
        total_saomiao.setOnClickListener(this);
        save_kehu.setOnClickListener(this);
        infoSupport = new InfoSupport(this);
        mPeijianAdapter = new KeHuPejianProAdapter(this, xsdInfos);
        listview2.setAdapter(mPeijianAdapter);



        mPeijianAdapter.setDeleteClickListener(new KeHuPejianProAdapter.DeleteClickListener() {
            @Override
            public void deleteClick(int position) {

                if( OrderBeanInfo.allBtnUnable){
                    return;
                }

                //showDeletePeijianDialog("");
            }


        });

        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(xsdInfos==null||xsdInfos.size()==0){
                    return;
                }
                Intent intent = new Intent(RukudanOrderActivity.this,XsdQueryDetailActivity.class);
                intent.putExtra("xsdId",mXsId);
                startActivity(intent);
            }
        });


        View headView2 = View.inflate(this, R.layout.project_peijian_kehu_head, null);
        View footView = View.inflate(this, R.layout.project_order_bottom, null);
        wxfTotal = footView.findViewById(R.id.xlfTotal);
        zongyingshou = footView.findViewById(R.id.zongyingshou);
        tv_xlfZk = footView.findViewById(R.id.tv_xlfZk);

        View footView2 = View.inflate(this, R.layout.peijian_order_bottom, null);
        pjfTotal = footView2.findViewById(R.id.pjfTotal);
        zongyingshou2 = footView2.findViewById(R.id.zongyingshou2);
        tv_pjfZk = footView2.findViewById(R.id.tv_pjfZk);
        listview2.addFooterView(footView2);
        listview2.addHeaderView(headView2);
    }

    private void deleteXspj(String xhStr) {

        //{"db":"mycon1","function":"sp_fun_delete_sales_order_detail","xs_id":" AXS2008080001","xh":"1"}
        if(TextUtils.isEmpty(mXsId)) {
            toastMsg = "请先选择客户";
            mHandler.sendEmptyMessage(TOAST_MSG);
            return;
        }
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("db", sp.getString(Constance.Data_Source_name));
            dataMap.put("function", "sp_fun_delete_sales_order_detail");
            dataMap.put("xs_id", mXsId);
            dataMap.put("xh", xhStr);
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
                            getJsdInfo(mCustomerId,mCustomerName);
                        } else {

                        }
                    } catch (Exception e) {
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





    private void showDeletePeijianDialog(final String xh) {
        OrderDeleteDialog dialog = new OrderDeleteDialog(this, "提示");
        dialog.setOnClickListener(new OrderDeleteDialog.OnClickListener() {
            @Override
            public void rightBtnClick() {
                deleteXspj(xh);
                //mPeijianAdapter.notifyDataSetChanged();
            }
        });
        dialog.show();
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
    float totalXlfZk = 0;
    @Override
    protected void updateUIThread(int msgInt) {
        super.updateUIThread(msgInt);
        if(msgInt == 112){
            try {
                OrderBeanInfo.allBtnUnable = false;
                setBtnEnble(true);
                total_jiesuan2.setEnabled(true);
                if (mPeijianAdapter == null) {
                    return;
                }
                mPeijianAdapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(msgInt==119){

            TextView xiaoshoudanhao = findViewById(R.id.xiaoshoudanhao);
            kehuzongjine.setText("总金额："+totalMoney);
            kehumingcheng.setText("客户名称："+mCustomerName);
            xiaoshoudanhao.setText("销售单号："+mXsId);
            //getXsdInfo();

        }else if(msgInt==10){
            mPeijianAdapter.notifyDataSetChanged();
            totalMoney = 0;
            if(xsdInfos!=null && xsdInfos.size()>0){

                for(int i=0;i<xsdInfos.size();i++){
                    XsdInfo bean = xsdInfos.get(i);
                    //float itemFloat = Float.parseFloat(bean.getSl()) * Float.parseFloat(bean.getSsj());
                    float itemFloat =  Float.parseFloat(bean.getSl()) * Float.parseFloat(bean.getJe());
                    totalMoney += itemFloat;
                }
            }
            TextView kehuzongjine = findViewById(R.id.kehuzongjine);
            kehuzongjine.setText("总金额："+totalMoney);
        }
    }

    private void setBtnEnble(boolean isEnble){

        peijianku.setBackgroundColor(Color.parseColor(isEnble?"#89c997":"#cccccc"));
        peijianku.setEnabled(isEnble);
    }


    String djzt="";
    private void getJsdInfo() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_down_repair_list_main");
        dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
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
                        List<OrderCarInfo> projectBeans = JSONArray.parseArray(dataArray.toJSONString(), OrderCarInfo.class);
                        if(projectBeans!=null&&projectBeans.size()>0){
                            mOrderCarInfo = projectBeans.get(0);

                            //getGuzhang();
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




    private void getXsdInfo(){
        //{“db”:”mycon1”,”function”:” sp_fun_serch_sales_order”,
        // ”customer_id”:”A2020N00001”,”dates”:”2020-07-01”,
        // ”datee”:”2020-07-31” ,”comp_code”:”A” ,"pre_row_number":"0"}
        if(TextUtils.isEmpty(mXsId)){
            return;
        }

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_down_sales_order_detail");
        dataMap.put("xs_id",mXsId);

        //dataMap.put("operater_code", sp.getString(Constance.USERNAME));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                xsdInfos.clear();
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ( "ok".equals(state)) {
                    JSONArray dataArray = (JSONArray) resMap.get("data");
                    List<XsdInfo> dataList = JSONArray.parseArray(dataArray.toJSONString(), XsdInfo.class);
                    if(dataList!=null && dataList.size()>0) {
                        xsdInfos.addAll(dataList);
                    }
                    mHandler.sendEmptyMessage(10);
                }else{

                }
            }
            @Override
            public void onFail() {
                toastMsg ="网络连接异常";
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
                if(TextUtils.isEmpty(mXsId)){
                    toastMsg = "请先选择客户";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }
                Intent intent2 = new Intent(RukudanOrderActivity.this,KeHuPeijianSelectActivity.class);
                intent2.putExtra("xsdId",mXsId);
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
                startActivity(new Intent(RukudanOrderActivity.this,ProjectSelectActivity.class));

                break;

            case R.id.car_home_page2:

                startActivityForResult(new Intent(RukudanOrderActivity.this,GongyingshangQueryActivity.class),100);
               // overridePendingTransition(0,0);


                break;
            case R.id.car_home_page:
                startActivityForResult(new Intent(RukudanOrderActivity.this, RukudanOrderActivity.class),100);
                overridePendingTransition(0,0);
                finish();
                break;
                case R.id.paigong:

                    //judgeToStatu();

                break;
            case R.id.total_saomiao:
                toScanPage();
                break;
            case R.id.total_jiesuan2:


                isToJiesuan=true;
                Intent intent = new Intent(RukudanOrderActivity.this,ProjectKehuJiesuanActivity.class);

                sp.putString(Constance.XSD_ID,mXsId);
                sp.putString(Constance.CUSTOMER_NAME,mCustomerName);
                sp.putString(Constance.CUSTOMER_ID,mCustomerId);

                startActivity(intent);
                break;
            case R.id.save_kehu:
                saveXsd();
                break;

            case R.id.select_date_start:
                selectDate(select_date_start);
                break;
            case R.id.select_date_end:
                selectDate(select_date_end);
                break;
            default:

                break;
        }
    }

    private void toScanPage() {
        //Intent sweep = new Intent(KeHuOrderActivity.this, CaptureActivity.class);
        /*Intent sweep = new Intent("com.google.zxing.client.android.SCAN");
        sweep.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(sweep,1003);*/

        //扫描操作
   /* IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("请扫描条码"); //底部的提示文字，设为""可以置空
        integrator.set
    integrator.initiateScan();*/


        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device

        integrator.initiateScan();


    }


    private void saveXsd() {
        //{“db”:”mycon1”,”function”:”sp_fun_update_sales_order”,
        // ”comp_code”:”A”,”customer_name”:”客户名称”,
        // ”customer_id”:”A2020N00001”,”operater”:”superuser”,”zje”:”0.00”,”xs_id”:””}
        if(TextUtils.isEmpty(mXsId)){
            toastMsg = "请先选择客户";
            mHandler.sendEmptyMessage(TOAST_MSG);
            return;
        }
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db",sp.getString(Constance.Data_Source_name));//"asa_to_sql");//sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_upload_sales_order");
        dataMap.put("customer_id", mCustomerId);
        dataMap.put("zje", totalMoney+"");
        dataMap.put("xs_id", TextUtils.isEmpty(mXsId)?"":mXsId);
        dataMap.put("comp_code", sp.getString(Constance.COMP_CODE));
        dataMap.put("customer_name", mCustomerName);
        dataMap.put("operater", sp.getString(Constance.USERNAME));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    String xs_id = (String) resMap.get("xs_id");
                    mXsId = xs_id;
                    //sp.putString(Constance.CUSTOMER_ID,customer_id);
                    toastMsg = "销售单号："+xs_id+"保存成功";
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
            }

            @Override
            public void onFail() {
                toastMsg = "网络连接异常";
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        });
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
            kehuzongjine.setText(mCustomerName);
            kehuzongjine.setText("￥：0元");
            getJsdInfo(mCustomerId,mCustomerName);

        }else if(resultCode==101){
            getXsdInfo();
        }else if(requestCode==1003){
            if(data==null||data.getExtras()==null){
                return;
            }
            String result = data.getExtras().getString("result");
            if(result!=null) {
                toastMsg = result;
                mHandler.sendEmptyMessage(TOAST_MSG);
            }
        }

        // 跳转扫描页面返回扫描数据

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    String mXsId = "";
    String mCustomerId = "";
    String mCustomerName = "";

    private void getJsdInfo(final String customer_id,final String customer_name){

        //{“db”:”mycon1”,”function”:”sp_fun_update_sales_order”,
        // ”comp_code”:”A”,”customer_name”:”客户名称”,
        // ”customer_id”:”A2020N00001”,”operater”:”superuser”,”zje”:”0.00”,”xs_id”:””}
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db",sp.getString(Constance.Data_Source_name));//"asa_to_sql");//sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_update_purchase_order");
        dataMap.put("customer_id", customer_id);
        dataMap.put("zje", "0.00");
        dataMap.put("jhd_id", "");
        dataMap.put("comp_code", sp.getString(Constance.COMP_CODE));
        dataMap.put("customer_name", customer_name);
        dataMap.put("operater", sp.getString(Constance.USERNAME));


        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    String jhd_id = (String) resMap.get("jhd_id");
                    mXsId = jhd_id;
                    mCustomerId = customer_id;
                    mCustomerName = customer_name;
                    //sp.putString(Constance.JSD_ID,xs_id);
                    sp.putString(Constance.CUSTOMER_ID,customer_id);
                    //sp.putString(Constance.CUSTOMER_ID,customer_id);
                    mHandler.sendEmptyMessage(119);


                } else {
                    if (resMap.get("msg") != null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    } else {
                        toastMsg = "网络异常";
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
            getJsdInfo();
            //getProjectListData();
        }

    }


}
