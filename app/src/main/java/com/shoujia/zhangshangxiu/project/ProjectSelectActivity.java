package com.shoujia.zhangshangxiu.project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseActivity;
import com.shoujia.zhangshangxiu.db.DBManager;
import com.shoujia.zhangshangxiu.entity.CarInfo;
import com.shoujia.zhangshangxiu.entity.CheckBeanInfo;
import com.shoujia.zhangshangxiu.entity.FirstIconInfo;
import com.shoujia.zhangshangxiu.entity.SecondIconInfo;
import com.shoujia.zhangshangxiu.home.HomeActivity;
import com.shoujia.zhangshangxiu.home.help.HomeDataHelper;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.order.ProjectOrderActivity;
import com.shoujia.zhangshangxiu.order.ProjectPaigongActivity;
import com.shoujia.zhangshangxiu.support.InfoSupport;
import com.shoujia.zhangshangxiu.support.NavSupport;
import com.shoujia.zhangshangxiu.util.CheckUtil;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;
import com.shoujia.zhangshangxiu.util.Util;
import com.shoujia.zhangshangxiu.view.ZnFlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23 0023.
 * 首页
 */
public class ProjectSelectActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "ProjectSelectActivity";
    private NavSupport navSupport;
    private TextView confirm_order, tv_kj, tv_cg, tv_by,back_jieche;

    private GridLayout gridLayout1;
    ZnFlowLayout gridLayout2;
    private SharePreferenceManager sp;
    InfoSupport mInFoupport;
    List<SecondIconInfo> secondIconInfos = new ArrayList<>();
    List<FirstIconInfo> mFirstIconInfos = new ArrayList<>();
    int currentIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pro_select);
        navSupport = new NavSupport(this, 4);
        confirm_order = findViewById(R.id.confirm_order);
        tv_kj = findViewById(R.id.tv_kj);
        tv_cg = findViewById(R.id.tv_cg);
        tv_by = findViewById(R.id.tv_by);
        gridLayout1 = findViewById(R.id.gridlayout1);
        gridLayout2 = findViewById(R.id.gridlayout2);
        back_jieche = findViewById(R.id.back_jieche);
        confirm_order.setOnClickListener(this);
        tv_kj.setOnClickListener(this);
        tv_cg.setOnClickListener(this);
        tv_by.setOnClickListener(this);
        mInFoupport = new InfoSupport(this);
        sp = new SharePreferenceManager(this);
        back_jieche.setOnClickListener(this);
        initView();
        initData();

    }


    @Override
    protected void updateUIThread(Message msg, int msgInt) {
        super.updateUIThread(msgInt);
        if (msgInt == 101) {
            gridLayout1.setVisibility(View.GONE);
            gridLayout2.setVisibility(View.VISIBLE);
            List<String> gridList = new ArrayList<>();
            if (secondIconInfos != null && secondIconInfos.size() > 0) {
                for (int i = 0; i < secondIconInfos.size(); i++) {
                    gridList.add(secondIconInfos.get(i).getMc());

                }
            }
            gridLayout2.setData(gridList);
            gridLayout2.setItemWidth(100);
            gridLayout2.setOnItemSelectListener(new ZnFlowLayout.OnItemSelectListener() {
                @Override
                public void onItemSelect(int pos, View view, String data, boolean isSelect) {
                    if (secondIconInfos == null || pos >= secondIconInfos.size()) {
                        return;
                    }

                    if (("返回").equals(secondIconInfos.get(pos).getMc())) {
                        secondIconInfos.clear();
                        gridLayout1.setVisibility(View.VISIBLE);
                        gridLayout2.setVisibility(View.GONE);
                    } else {
                        secondIconInfos.get(pos).setSelected(isSelect);
                    }

                }
            });
        } else if (msgInt == 102) {
            gridLayout1.setVisibility(View.VISIBLE);
            gridLayout2.setVisibility(View.GONE);
            DBManager db = DBManager.getInstanse(this);
            final List<FirstIconInfo> firstIconInfos = mFirstIconInfos;
            gridLayout1.removeAllViews();
            if (firstIconInfos != null && firstIconInfos.size() > 0) {
                gridLayout1.setRowCount(firstIconInfos.size());
                gridLayout1.setColumnCount(3);
                for (int i = 0; i < firstIconInfos.size(); i++) {
                    View subView = View.inflate(this, R.layout.view_grid_item, null);
                    TextView name = subView.findViewById(R.id.tv_item);
                    name.setText(firstIconInfos.get(i).getTcmc());
                    gridLayout1.addView(subView);
                    subView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView view1 = view.findViewById(R.id.tv_item);
                            if (view1 != null && view1.getText() != null) {
                                String name = view1.getText().toString();
                                DBManager dbManager = DBManager.getInstanse(ProjectSelectActivity.this);
                                secondIconInfos.clear();
                                SecondIconInfo info = new SecondIconInfo();
                                info.setMc("返回");
                                secondIconInfos.add(info);
                                if(currentIndex==2){
                                    secondIconInfos.addAll(dbManager.querySecondIconListDataByLb(name));
                                }else {
                                    secondIconInfos.addAll(dbManager.querySecondIconListData(name));
                                }
                                mHandler.sendEmptyMessage(101);

                            }
                        }
                    });

                }
            }
        }

    }


    private class TagBean {
        public int tag;
        public boolean isSelected;
    }

    int postNum = 0;
    int responseNum = 0;

    private void upLoadServer() {
        if (secondIconInfos == null || secondIconInfos.size() == 0) {
            toastMsg = "您还未选择项目";
            mHandler.sendEmptyMessage(TOAST_MSG);
            return;
        }
        List<SecondIconInfo> newInfos = new ArrayList<>();

        for (SecondIconInfo mSecondIconInfo : secondIconInfos) {
            if (mSecondIconInfo.isSelected()) {
                newInfos.add(mSecondIconInfo);
            }
        }

        if (newInfos == null || newInfos.size() == 0) {
            toastMsg = "您还未选择项目";
            mHandler.sendEmptyMessage(TOAST_MSG);
            return;
        }

        postNum = 0;
        responseNum = 0;
        for (SecondIconInfo mSecondIconInfo : newInfos) {
            if (mSecondIconInfo.isSelected()) {
                postNum++;
                uploadSinglePro(mSecondIconInfo);
            }
        }

    }

    private void uploadSinglePro(SecondIconInfo mSecondIconInfo) {

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_upload_maintenance_project_detail");
        dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
        dataMap.put("xlxm", mSecondIconInfo.getMc());
        dataMap.put("xlf", mSecondIconInfo.getXlf());
        dataMap.put("zk", "0.00");
        dataMap.put("wxgz", mSecondIconInfo.getWxgz());
        dataMap.put("pgzje", mSecondIconInfo.getSpj());
        dataMap.put("pgzgs", mSecondIconInfo.getPgzgs());
        dataMap.put("xh", "0");


        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    responseNum++;
                    if (responseNum == postNum) {
                        Intent intent2 = new Intent(ProjectSelectActivity.this, ProjectOrderActivity.class);
                        startActivity(intent2);
                    }
                } else {
                    if (resMap.get("msg") != null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                        //Intent intent2 = new Intent(ProjectSelectActivity.this, ProjectOrderActivity.class);
                        //startActivity(intent2);
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

    private void initView() {
        gridLayout1.setVisibility(View.VISIBLE);
        gridLayout2.setVisibility(View.GONE);
        gridLayout1.removeAllViews();
        DBManager db = DBManager.getInstanse(this);
        final List<FirstIconInfo> firstIconInfos = db.queryFirstIconListData();
        if (firstIconInfos != null && firstIconInfos.size() > 0) {
            gridLayout1.setRowCount(firstIconInfos.size());
            gridLayout1.setColumnCount(3);
            for (int i = 0; i < firstIconInfos.size(); i++) {
                View subView = View.inflate(this, R.layout.view_grid_item, null);
                TextView name = subView.findViewById(R.id.tv_item);
                name.setText(firstIconInfos.get(i).getWxgz());
                gridLayout1.addView(subView);
                subView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView view1 = view.findViewById(R.id.tv_item);
                        if (view1 != null && view1.getText() != null) {
                            String name = view1.getText().toString();
                            DBManager dbManager = DBManager.getInstanse(ProjectSelectActivity.this);
                            secondIconInfos.clear();
                            SecondIconInfo info = new SecondIconInfo();
                            info.setMc("返回");
                            secondIconInfos.add(info);
                            if(currentIndex==2){
                                secondIconInfos.addAll(dbManager.querySecondIconListDataByLb(name));
                            }else {
                                secondIconInfos.addAll(dbManager.querySecondIconListData(name));
                            }
                            mHandler.sendEmptyMessage(101);

                        }
                    }
                });

            }
        }
    }

    //初始化数据
    private void initData() {
        getSecondInconList();
        DBManager db = DBManager.getInstanse(this);
        List<FirstIconInfo> firstIconInfos = db.queryFirstIconListData();
        if (firstIconInfos == null || firstIconInfos.size() == 0) {
            HomeDataHelper homeDataHelper = new HomeDataHelper(this);
            homeDataHelper.getFirstIconList(new HomeDataHelper.InsertDataListener() {
                @Override
                public void onSuccess() {
                    DBManager.getInstanse(ProjectSelectActivity.this).close();

                }

                @Override
                public void onFail() {
                    DBManager.getInstanse(ProjectSelectActivity.this).close();
                    getSecondInconList();
                }
            });
        } else {
            mHandler.sendEmptyMessage(4);
        }
    }


    private void getSecondInconList() {
        DBManager db = DBManager.getInstanse(this);
        List<SecondIconInfo> secondIconInfos = db.querySecondIconListData();
        if (secondIconInfos == null || secondIconInfos.size() == 0) {

            HomeDataHelper homeDataHelper = new HomeDataHelper(this);
            homeDataHelper.getSecondIconList();

        } else {
            mHandler.sendEmptyMessage(5);
        }

    }

    private void setTextColor(int index) {
        currentIndex = index;
        TextView[] textViews = {tv_cg, tv_kj, tv_by};
        for (int i = 0; i < 3; i++) {
            if (i == index) {
                textViews[i].setTextColor(Color.parseColor("#ffffff"));
                textViews[i].setBackgroundColor(Color.parseColor("#ff9db4"));
            } else {
                textViews[i].setTextColor(Color.parseColor("#a4a3a3"));
                textViews[i].setBackgroundColor(Color.parseColor("#eeeeee"));
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_order:
                CheckBeanInfo beanInfo = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                if(beanInfo!=null && "1".equals(beanInfo.getNew_bill())) {
                    upLoadServer();
                }else{
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                }

                //startActivity(new Intent(this,ProjectOrderActivity.class));
                break;
            case R.id.tv_cg:
                setTextColor(0);
                initView();
                break;
            case R.id.tv_kj:
                setTextColor(1);
                initView();
                break;
            case R.id.tv_by:
                setTextColor(2);
                getByData();
                break;
            case R.id.back_jieche:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("from","select");
                startActivity(intent);
                break;
            default:

                break;
        }
    }

    private void getByData() {
        HomeDataHelper homeDataHelper = new HomeDataHelper(this);
        homeDataHelper.getBaoyangIconList(new HomeDataHelper.GetBaoyangDataListener() {


            @Override
            public void onSuccess(List<FirstIconInfo> dataList) {
                mFirstIconInfos.clear();
                if (dataList == null) {

                } else {
                    mFirstIconInfos.addAll(dataList);
                }
                mHandler.sendEmptyMessage(102);

            }

            @Override
            public void onFail() {
                mFirstIconInfos.clear();
                mHandler.sendEmptyMessage(102);
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
}
