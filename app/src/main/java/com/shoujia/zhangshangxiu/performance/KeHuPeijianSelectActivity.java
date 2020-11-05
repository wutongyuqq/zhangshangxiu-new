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
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseActivity;
import com.shoujia.zhangshangxiu.db.DBManager;
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
public class KeHuPeijianSelectActivity extends BaseActivity implements View.OnClickListener {

    private SharePreferenceManager sp;
    private ListView rl_pj_one_list;
    private RelativeLayout rl_pj_one;
    List<PartsBean> mPartsBeans = new ArrayList<>();

    PeijianSelectOneAdapter oneAdapter;
    private String mFrom;

    private int postNum = 0;
    private int totalPostNum = 0;
    int currentSelectInt = 0;
    ImageView query_btn;
    EditText headInput;
    TextView make_sure_one;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_peijian_kehu_select);
        initView();
        initData();

    }

    private void initView() {
        sp = new SharePreferenceManager(this);

        View footView = View.inflate(this, R.layout.listview_bottom, null);

        rl_pj_one_list = findViewById(R.id.rl_pj_one_list);
        make_sure_one = findViewById(R.id.make_sure_one);
        View headView = View.inflate(this,R.layout.view_select_head,null);
         headInput = headView.findViewById(R.id.headInput);
         query_btn = headView.findViewById(R.id.query_btn);
        rl_pj_one_list.addHeaderView(headView);
        rl_pj_one_list.addFooterView(footView);
        new NavSupport(this,15);
        query_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mPartsBeans==null){
                    mPartsBeans=new ArrayList<>();
                }
                try {
                    mPartsBeans.clear();
                    if (headInput.getText() != null && !TextUtils.isEmpty(headInput.getText().toString().trim())) {

                        String contentStr = headInput.getText().toString().trim();
                        DBManager dbManager = DBManager.getInstanse(KeHuPeijianSelectActivity.this);
                        List<PartsBean> beans = dbManager.getPartsListData(contentStr);
                        if (beans != null && beans.size() > 0) {
                            mPartsBeans.addAll(beans);
                            mHandler.sendEmptyMessage(101);
                        }
                    } else {
                        DBManager dbManager = DBManager.getInstanse(KeHuPeijianSelectActivity.this);
                        List<PartsBean> beans = dbManager.getPartsListData();
                        if (beans != null && beans.size() > 0) {
                            mPartsBeans.addAll(beans);
                            mHandler.sendEmptyMessage(101);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        rl_pj_one = findViewById(R.id.rl_pj_one);


        oneAdapter = new PeijianSelectOneAdapter(this,mPartsBeans);
        rl_pj_one_list.setAdapter(oneAdapter);
        make_sure_one.setOnClickListener(this);
        mFrom = getIntent().getStringExtra("from");
        if(!TextUtils.isEmpty(mFrom) && mFrom.equals("msgCenterFrom")){
            findViewById(R.id.pro_btn_lay).setVisibility(View.GONE);
        }

    }


    private void getPeijianDataList(){
        HomeDataHelper helper = new HomeDataHelper(this);
        helper.updateParts(new HomeDataHelper.UpdateDataListener() {
            @Override
            public void onSuccess() {
                mPartsBeans.clear();
                DBManager dbManager = DBManager.getInstanse(KeHuPeijianSelectActivity.this);
                List<PartsBean> beans = dbManager.getPartsListData();
                if(beans!=null&&beans.size()>0) {
                    mPartsBeans.addAll(beans);
                    mHandler.sendEmptyMessage(101);
                }else{
                    mPartsBeans.clear();
                    mHandler.sendEmptyMessage(101);
                }
            }
        });
    }



    //初始化数据
    private void initData() {
        mPartsBeans.clear();
        DBManager dbManager = DBManager.getInstanse(this);
        mPartsBeans.addAll(dbManager.getPartsListData());
        if(mPartsBeans==null||mPartsBeans.size()==0){
            getPeijianDataList();
        }else{
            mHandler.sendEmptyMessage(101);
        }

    }

    @Override
    protected void updateUIThread(int msgInt) {
        super.updateUIThread(msgInt);
        if(msgInt==101){
            oneAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.make_sure_one:
                makeSureOne();
                break;
            default:

                break;
        }
    }

    private void makeSureOne() {
        postNum = 0;
        totalPostNum = 0;
        if(currentSelectInt ==0 ) {
            totalPostNum = 0;
            for (PartsBean bean : mPartsBeans) {
                if (bean.isSelected()) {
                    totalPostNum++;
                    makeSureData(bean);
                }
            }
        }

    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    String mXsId="";

    private void makeSureData(PartsBean bean){
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_upload_sales_order_detail");
        dataMap.put("xs_id",getIntent().getStringExtra("xsdId"));
        dataMap.put("pjbm",bean.getPjbm());
        dataMap.put("pjmc",bean.getPjmc());
        dataMap.put("ck",bean.getCk());
        dataMap.put("cd",bean.getCd());
        dataMap.put("cx",bean.getCx());
        dataMap.put("bz","");
        dataMap.put("dw","");
        dataMap.put("cangwei","");
        dataMap.put("property","");
        dataMap.put("zt","");
        dataMap.put("xsj",bean.getXsj());
        dataMap.put("cb",bean.getCd());
        dataMap.put("xh","0");
        dataMap.put("sl",bean.getSl());
        dataMap.put("comp_code",sp.getString(Constance.COMP_CODE));
        dataMap.put("operater_code", sp.getString(Constance.USERNAME));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    postNum ++;
                    if(postNum==totalPostNum) {
                        //mHandler.sendEmptyMessage(103);
                        setResult(101);
                        finish();
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
