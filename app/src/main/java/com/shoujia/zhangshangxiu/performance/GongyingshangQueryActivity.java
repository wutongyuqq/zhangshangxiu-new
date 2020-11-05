package com.shoujia.zhangshangxiu.performance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseActivity;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.performance.adapter.KehuSelectOneAdapter;
import com.shoujia.zhangshangxiu.performance.entity.CustomInfo;
import com.shoujia.zhangshangxiu.support.NavSupport;
import com.shoujia.zhangshangxiu.util.Constance;
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
public class GongyingshangQueryActivity extends BaseActivity implements View.OnClickListener {

    private SharePreferenceManager sp;
    private ListView rl_pj_one_list;
    private String  previous_xh1 = "0";
    List<CustomInfo> mPartsList=new ArrayList<>();
    KehuSelectOneAdapter oneAdapter;
    View query_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_kehu_query_select);
        initView();
        initData();

    }

    private void initView() {
        sp = new SharePreferenceManager(this);
        rl_pj_one_list = findViewById(R.id.rl_pj_one_list);
        query_btn = findViewById(R.id.query_btn);
        oneAdapter = new KehuSelectOneAdapter(this,mPartsList);
        rl_pj_one_list.setAdapter(oneAdapter);
        new NavSupport(this,20);
        query_btn.setOnClickListener(this);
        rl_pj_one_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if("chakehu".equals(getIntent().getStringExtra("from"))){
                    //getJsdInfo(mPartsList.get(position).getCustomer_id(),mPartsList.get(position).getCustomer_name());
                    Intent intent = new Intent();
                    intent.setClass(GongyingshangQueryActivity.this, KeHuQueryOrderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("customer_id", mPartsList.get(position).getCustomer_id());
                    bundle.putString("customer_name", mPartsList.get(position).getCustomer_name());
                    bundle.putString("from", "chakehu");
                    intent.putExtras(bundle);
                    startActivity(intent);

                }else if("msgCenter".equals(getIntent().getStringExtra("from"))) {

                }else{
                    if(mPartsList==null||mPartsList.size()==0){
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(GongyingshangQueryActivity.this, XsdKehuQueryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("customer_id", mPartsList.get(position).getCustomer_id());
                    bundle.putString("customer_name", mPartsList.get(position).getCustomer_name());
                    bundle.putString("from", "msg");
                    intent.putExtras(bundle);
                    setResult(100, intent);
                    finish();
                }

            }
        });
    }
    //初始化数据
    private void initData() {
       // searchData();
    }

    @Override
    protected void updateUIThread(int msgInt) {
        super.updateUIThread(msgInt);
        if(msgInt==10){
            //oneAdapter.setList(mPartsList);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    oneAdapter.notifyDataSetChanged();
                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_btn:
                searchData();
                break;
            default:

                break;
        }
    }


    private void searchData(){
        mPartsList.clear();
        EditText contentText = findViewById(R.id.content);
        String searchName = "";
        if(contentText.getText()!=null && !TextUtils.isEmpty(contentText.getText().toString())){
            searchName = contentText.getText().toString();
        }
        //点击软键盘外部，收起软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        getListData(searchName);
    }

    //{{“db”:”mycon1”,”function”:” sp_fun_serch_customer “,”customer_name”:”客户名称”,”operater”:”superuser”}
    private void getListData(final String searchWord){

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db",sp.getString(Constance.Data_Source_name));//sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_serch_customer");
        dataMap.put("customer_name", searchWord);
        dataMap.put("operater", sp.getString(Constance.USERNAME));


        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    JSONArray dataArray = (JSONArray) resMap.get("data");
                    List<CustomInfo> dataList = JSONArray.parseArray(dataArray.toJSONString(), CustomInfo.class);
                    if(dataList!=null && dataList.size()>0) {
                        mPartsList.addAll(dataList);
                    }
                    mHandler.sendEmptyMessage(10);
                } else {
                    previous_xh1="end";
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
    public void onResume() {
        super.onResume();
    }
}
