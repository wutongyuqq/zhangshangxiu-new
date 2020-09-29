package com.shoujia.zhangshangxiu.performance;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseActivity;
import com.shoujia.zhangshangxiu.db.DBManager;
import com.shoujia.zhangshangxiu.entity.CarInfo;
import com.shoujia.zhangshangxiu.entity.PartsBean;
import com.shoujia.zhangshangxiu.home.help.HomeDataHelper;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.order.adapter.PeijianSelectOneAdapter;
import com.shoujia.zhangshangxiu.order.adapter.PeijianSelectThreeAdapter;
import com.shoujia.zhangshangxiu.order.adapter.PeijianSelectTwoAdapter;
import com.shoujia.zhangshangxiu.order.entity.TwoBean;
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
public class PeijianQueryActivity extends BaseActivity implements View.OnClickListener {

    private SharePreferenceManager sp;
    private ListView rl_pj_one_list;
    List<PartsBean> mPartsBeans = new ArrayList<>();
    private String  previous_xh1 = "0";
    List<PartsBean> mPartsList=new ArrayList<>();
    PeijianSelectOneAdapter oneAdapter;
    View query_btn,quit_query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_peijian_query_select);
        initView();
        initData();

    }

    private void initView() {
        sp = new SharePreferenceManager(this);
        rl_pj_one_list = findViewById(R.id.rl_pj_one_list);
        query_btn = findViewById(R.id.query_btn);
        quit_query = findViewById(R.id.quit_query);
        oneAdapter = new PeijianSelectOneAdapter(this,mPartsBeans);
        rl_pj_one_list.setAdapter(oneAdapter);
        query_btn.setOnClickListener(this);
        quit_query.setOnClickListener(this);
        new NavSupport(this,21);
    }

    //初始化数据
    private void initData() {
        //searchData();
    }

    @Override
    protected void updateUIThread(int msgInt) {
        super.updateUIThread(msgInt);
        if(msgInt==10){
            oneAdapter.setList(mPartsList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_btn:
                searchData();
                break;
            case R.id.quit_query:
                finish();
                break;
            default:

                break;
        }
    }

    private void searchData(){
        mPartsList.clear();
        previous_xh1="0";
        EditText contentText = findViewById(R.id.content);
        String searchName = "";
        if(contentText.getText()!=null && !TextUtils.isEmpty(contentText.getText().toString())){
            searchName = contentText.getText().toString();
        }
        getListData(searchName);
    }

    //{"db":"mycon1","function":"sp_fun_serch_stock","comp_code":"A","pjmc":"配件名称","operater":"superuser"}
    private void getListData(final String searchWord){
        if(previous_xh1!=null&&previous_xh1.equals("end")){
            mHandler.sendEmptyMessage(10);
            return;
        }
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db",sp.getString(Constance.Data_Source_name));//sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_serch_stock");
        dataMap.put("pjmc", searchWord);
        dataMap.put("operater", sp.getString(Constance.USERNAME));
        dataMap.put("comp_code", sp.getString(Constance.COMP_CODE));


        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {

                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                previous_xh1 = (String) resMap.get("Previous");
                if ("ok".equals(state)) {
                    JSONArray dataArray = (JSONArray) resMap.get("data");
                    List<PartsBean> dataList = JSONArray.parseArray(dataArray.toJSONString(), PartsBean.class);
                    if(dataList!=null && dataList.size()>0) {
                        mPartsList.addAll(dataList);
                    }
                    getListData(searchWord);
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
