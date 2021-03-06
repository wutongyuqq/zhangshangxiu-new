package com.shoujia.zhangshangxiu.order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseActivity;
import com.shoujia.zhangshangxiu.db.DBManager;
import com.shoujia.zhangshangxiu.dialog.OrderAddTmpDialog;
import com.shoujia.zhangshangxiu.dialog.OrderDeleteDialog;
import com.shoujia.zhangshangxiu.dialog.OrderPeijianEditDialog;
import com.shoujia.zhangshangxiu.dialog.OrderTempEditDialog;
import com.shoujia.zhangshangxiu.dialog.PeijianTipDialog;
import com.shoujia.zhangshangxiu.dialog.ProjectCancleDialog;
import com.shoujia.zhangshangxiu.entity.CheckBeanInfo;
import com.shoujia.zhangshangxiu.entity.OrderCarInfo;
import com.shoujia.zhangshangxiu.entity.PeijianBean;
import com.shoujia.zhangshangxiu.entity.ProjectBean;
import com.shoujia.zhangshangxiu.entity.RepairInfo;
import com.shoujia.zhangshangxiu.entity.SecondIconInfo;
import com.shoujia.zhangshangxiu.home.help.HomeDataHelper;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.order.adapter.PeijianOrderProAdapter;
import com.shoujia.zhangshangxiu.order.adapter.ProjectOrderProAdapter;
import com.shoujia.zhangshangxiu.order.entity.OrderBeanInfo;
import com.shoujia.zhangshangxiu.project.GuzhangListActivity;
import com.shoujia.zhangshangxiu.project.ProjectActivity;
import com.shoujia.zhangshangxiu.project.ProjectSelectActivity;
import com.shoujia.zhangshangxiu.project.help.ProjectDataHelper;
import com.shoujia.zhangshangxiu.support.InfoSupport;
import com.shoujia.zhangshangxiu.support.NavSupport;
import com.shoujia.zhangshangxiu.util.CheckUtil;
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
public class ProjectOrderActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "ProjectOrderActivity";
    private NavSupport navSupport;
    private RelativeLayout img_wg_lay;
    private SharePreferenceManager sp;
    List<ProjectBean> mProjectList = new ArrayList<>();
    List<PeijianBean> mPeiJianList = new ArrayList<>();
    ProjectOrderProAdapter mOrderAdapter;
    PeijianOrderProAdapter mPeijianAdapter;
    ListView listview,listview2;
    TextView guzhangmiaoshu;
    TextView beizhu;
    TextView wxfTotal;
    TextView zongyingshou;
    TextView tv_xlfZk;
    TextView pjfTotal;
    TextView zongyingshou2;
    TextView tv_pjfZk;
    TextView tv_pro, tv_pj,temp_pro,peijianku,project_ck,car_home_page,paigong,total_jiesuan,total_jiesuan2,car_home_page2,banjinpenqi;
    LinearLayout pro_btn_lay,pj_btn_lay;
    String gdStatu="";
    boolean isToPaigong;
    private boolean djztUnable = false;
    private boolean notDelete = false;
    private boolean yccType = false;
    private OrderCarInfo mOrderCarInfo;
    private boolean isToPeijian;
    private boolean isToJiesuan;
    InfoSupport infoSupport;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pro_order);
        initView();

        initData();
        getProjectListData();
        getPjListData();
        getJsdInfo();
    }

    private void initView() {
        sp = new SharePreferenceManager(this);
        navSupport = new NavSupport(this, 11);
        listview = findViewById(R.id.listview);

        listview2 = findViewById(R.id.listview2);

        temp_pro = findViewById(R.id.temp_pro);
        banjinpenqi = findViewById(R.id.banjinpenqi);
        pro_btn_lay = findViewById(R.id.pro_btn_lay);
        pj_btn_lay = findViewById(R.id.pj_btn_lay);
        peijianku = findViewById(R.id.peijianku);
        tv_pro = findViewById(R.id.tv_pro);
        tv_pj = findViewById(R.id.tv_pj);
        img_wg_lay = findViewById(R.id.img_wg_lay);
        project_ck = findViewById(R.id.project_ck);
        car_home_page = findViewById(R.id.car_home_page);
        car_home_page2 = findViewById(R.id.car_home_page2);
        paigong = findViewById(R.id.paigong);
        total_jiesuan = findViewById(R.id.total_jiesuan);
        total_jiesuan2 = findViewById(R.id.total_jiesuan2);
        tv_pro.setOnClickListener(this);
        tv_pj.setOnClickListener(this);
        temp_pro.setOnClickListener(this);
        peijianku.setOnClickListener(this);
        project_ck.setOnClickListener(this);
        car_home_page.setOnClickListener(this);
        car_home_page2.setOnClickListener(this);
        total_jiesuan.setOnClickListener(this);
        total_jiesuan2.setOnClickListener(this);
        paigong.setOnClickListener(this);
        infoSupport = new InfoSupport(this);
        mOrderAdapter = new ProjectOrderProAdapter(this, mProjectList);
        mPeijianAdapter = new PeijianOrderProAdapter(this, mPeiJianList);
        listview.setAdapter(mOrderAdapter);
        listview2.setAdapter(mPeijianAdapter);
        mOrderAdapter.setDeleteClickListener(new ProjectOrderProAdapter.DeleteClickListener() {
            @Override
            public void deleteClick(int position) {
                if( OrderBeanInfo.allBtnUnable){
                    return;
                }
                CheckBeanInfo beanInfo = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                if(beanInfo!=null && "1".equals(beanInfo.getDel())) {

                }else{
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }

                showDeleteDialog(position);

            }
        });
        mOrderAdapter.setEditClickListener(new ProjectOrderProAdapter.EditClickListener() {
            @Override
            public void editClick(int position) {


                CheckBeanInfo beanInfo2 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                if(beanInfo2!=null && "1".equals(beanInfo2.getModify())) {

                }else{
                    isToPaigong = false;
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }

                if( OrderBeanInfo.allBtnUnable){
                    return;
                }
                showEditDialog(position);
            }
        });


        mPeijianAdapter.setDeleteClickListener(new PeijianOrderProAdapter.DeleteClickListener() {
            @Override
            public void deleteClick(int position) {


                CheckBeanInfo beanInfo2 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                if(beanInfo2!=null && "1".equals(beanInfo2.getDel())) {

                }else{
                    isToPaigong = false;
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }

                if( OrderBeanInfo.allBtnUnable){
                    return;
                }
                showDeletePeijianDialog(position);

            }
        });
        mPeijianAdapter.setEditClickListener(new PeijianOrderProAdapter.EditClickListener() {
            @Override
            public void editClick(int position) {


                CheckBeanInfo beanInfo2 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                if(beanInfo2!=null && "1".equals(beanInfo2.getModify())) {

                }else{
                    isToPaigong = false;
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }


                if( OrderBeanInfo.allBtnUnable){
                    return;
                }
                showEditPeijianDialog(position);
            }
        });

        View headView = View.inflate(this, R.layout.project_order_head, null);
        View headView2 = View.inflate(this, R.layout.project_peijian_head, null);
        listview.addHeaderView(headView);
        TextView tv_gls = headView.findViewById(R.id.tv_gls);
        tv_gls.setText("公里数:"+sp.getString(Constance.GONGLISHU));

         guzhangmiaoshu = headView.findViewById(R.id.guzhangmiaoshu);
        guzhangmiaoshu.setText("故障描述:"+sp.getString(Constance.GUZHNAGMIAOSHU));

        guzhangmiaoshu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProjectOrderActivity.this,GuzhangListActivity.class));
            }
        });
        TextView tv_ywgdate = headView.findViewById(R.id.tv_ywgdate);
        String ywgDate = sp.getString(Constance.YUWANGONG);
        if(ywgDate!=null&&ywgDate.length()>10){
            ywgDate = ywgDate.substring(0,10);
        }
        tv_ywgdate.setText("预完工日期:"+ywgDate);



        View footView = View.inflate(this, R.layout.project_order_bottom, null);
         beizhu = footView.findViewById(R.id.beizhu);
        wxfTotal = footView.findViewById(R.id.xlfTotal);
        zongyingshou = footView.findViewById(R.id.zongyingshou);
        tv_xlfZk = footView.findViewById(R.id.tv_xlfZk);
        listview.addFooterView(footView);

        View footView2 = View.inflate(this, R.layout.peijian_order_bottom, null);
        pjfTotal = footView2.findViewById(R.id.pjfTotal);
        zongyingshou2 = footView2.findViewById(R.id.zongyingshou2);
        tv_pjfZk = footView2.findViewById(R.id.tv_pjfZk);
        listview2.addFooterView(footView2);
        listview2.addHeaderView(headView2);

        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PeijianBean peijianBean = mPeiJianList.get(i-1);
                PeijianTipDialog dialog = new PeijianTipDialog(ProjectOrderActivity.this,peijianBean);
                dialog.show();
            }
        });
    }

    private void showEditDialog(final int position) {
        OrderTempEditDialog editDialog = new OrderTempEditDialog(this,mProjectList.get(position));
        editDialog.setOnClickListener(new OrderTempEditDialog.OnClickListener() {
            @Override
            public void rightBtnClick(ProjectBean newBean) {
                if(newBean.isSaveNewPrice()) {
                    saveNewPrice(newBean, position);
                }
                addProjectDetailServer(position,newBean);
            }
        });
        editDialog.show();
    }

    private void showEditPeijianDialog(final int position) {
        OrderPeijianEditDialog editDialog = new OrderPeijianEditDialog(this,mPeiJianList.get(position));
        editDialog.setOnClickListener(new OrderPeijianEditDialog.OnClickListener() {
            @Override
            public void rightBtnClick(PeijianBean newBean) {
                mPeiJianList.set(position,newBean);
                mPeijianAdapter.notifyDataSetChanged();
                addPjToServer(newBean);
            }


        });
        editDialog.show();
    }


    private void saveNewPrice(final ProjectBean projectBean,final int position){

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_upload_maintenance_project_library");
        dataMap.put("xlxm", projectBean.getXlxm());
        dataMap.put("wxgz",projectBean.getWxgz());
        dataMap.put("xlf",projectBean.getXlf());
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("onSuccess--json", json);
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    mProjectList.set(position,projectBean);
                    mHandler.sendEmptyMessage(100);
                } else {
                    if(resMap.get("msg")!=null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }else{
                        toastMsg = "网络异常，删除失败";
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

    private void showDeleteDialog(final int position) {
        OrderDeleteDialog dialog = new OrderDeleteDialog(this, mProjectList.get(position).getXlxm());
        dialog.setOnClickListener(new OrderDeleteDialog.OnClickListener() {
            @Override
            public void rightBtnClick() {
                deleteProData(position);
            }
        });
        dialog.show();
    }


    private void showDeletePeijianDialog(final int position) {
        OrderDeleteDialog dialog = new OrderDeleteDialog(this, mPeiJianList.get(position).getPjmc());
        dialog.setOnClickListener(new OrderDeleteDialog.OnClickListener() {
            @Override
            public void rightBtnClick() {
                deletePeijianData(position);
                //mPeijianAdapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    private void deleteProData(int position) {

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_delete_maintenance_project_detail");
        dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
        dataMap.put("xh", mProjectList.get(position).getXh());
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("onSuccess--json", json);
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    getProjectListData();
                } else {
                    if(resMap.get("msg")!=null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }else{
                        toastMsg = "网络异常，删除失败";
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



    private void deletePeijianData(int position) {

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db",sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_delete_parts_project_detail");
        dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
        dataMap.put("xh", mPeiJianList.get(position).getXh());
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("onSuccess--json", json);
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    getPjListData();
                } else {
                    if(resMap.get("msg")!=null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }else{
                        toastMsg = "网络异常，删除失败";
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
        if (msgInt == 100) {
            try {
                if (mProjectList != null && mProjectList.size() > 0) {
                    if (mOrderAdapter == null) {
                        return;
                    }
                    mOrderAdapter.notifyDataSetChanged();
                    float totalXlf = 0;
                    totalXlfZk = 0;
                    for (ProjectBean bean : mProjectList) {
                        float xlf = Float.parseFloat(bean.getXlf());
                        float xlfZk = Float.parseFloat(bean.getZk());
                        totalXlf += xlf;
                        totalXlfZk += xlfZk;
                    }

                    wxfTotal.setText("" + totalXlf);
                    zongyingshou.setText("" + (totalXlf - totalXlfZk));

                    tv_xlfZk.setText(totalXlfZk + "");
                } else {
                    if (mOrderAdapter == null) {
                        return;
                    }
                    mOrderAdapter.notifyDataSetChanged();
                    wxfTotal.setText("0");
                    zongyingshou.setText("0");
                    tv_xlfZk.setText("0");
                }
            }catch (Exception e){

            }
        }else if(msgInt==101){
            try{
                if(mPeiJianList!=null&&mPeiJianList.size()>0){
                    mPeijianAdapter.notifyDataSetChanged();
                    float totalMoneyFloat = 0;
                    for(PeijianBean bean:mPeiJianList){
                        float totalMoney = Float.parseFloat(bean.getSl()) * Float.parseFloat(bean.getSsj());
                        totalMoneyFloat += totalMoney;
                    }
                    float totalPjfMoney = (float)(Math.round(totalMoneyFloat*100))/100;
                    pjfTotal.setText(totalPjfMoney+"");
                    zongyingshou2.setText(totalPjfMoney+"");
                }else{
                    mPeijianAdapter.notifyDataSetChanged();
                    pjfTotal.setText("0");
                    zongyingshou2.setText("0");
                }
            }catch (Exception e){

            }

        }else if(msgInt==109){
            try {
                if(mOrderCarInfo==null){
                    return;
                }
                guzhangmiaoshu.setText("故障描述："+mOrderCarInfo.getCar_fault());
                sp.putString(Constance.YUWANGONG,mOrderCarInfo.getYwg_date());
                sp.putString(Constance.GONGLISHU,mOrderCarInfo.getJclc());
                sp.putString(Constance.CURRENTCP,mOrderCarInfo.getCp());
                sp.putString(Constance.CUSTOMER_ID,mOrderCarInfo.getCustomer_id());
                sp.putString(Constance.CURRENTCZ,mOrderCarInfo.getCz());
                sp.putString(Constance.JIECHEDATE,mOrderCarInfo.getJc_date());
                sp.putString(Constance.BEIZHU,mOrderCarInfo.getMemo());
                sp.putString(Constance.CHEJIAHAO,mOrderCarInfo.getCjhm());
                beizhu.setText("备注:"+mOrderCarInfo.getMemo());
                wxfTotal.setText(""+mOrderCarInfo.getWxfzj());
                infoSupport.setCz(mOrderCarInfo.getCz());
                zongyingshou.setText(""+mOrderCarInfo.getZje());
                if (mOrderCarInfo.getDjzt().equals("待修")) {
                    paigong.setText("派工");
                    setBtnEnble(true);
                    // $scope.djzt = '派工';
                } else if (mOrderCarInfo.getDjzt().equals("已派工") || mOrderCarInfo.getDjzt().equals("修理中")) {
                    paigong.setText("全部完工");
                    setBtnEnble(true);
                    // $scope.djzt = '全部完工';
                } else if (mOrderCarInfo.getDjzt().equals("处理中")){
                    paigong.setText("派工");
                    setBtnEnble(true);
                    //$scope.djzt = '派工';
                } else if (mOrderCarInfo.getDjzt().equals("审核已结算")) {
                    img_wg_lay.setVisibility(View.VISIBLE);
                    djztUnable = true;
                    setBtnEnble(true);
                    OrderBeanInfo.allBtnUnable = true;
                    paigong.setText("取消完工");
                    OrderBeanInfo.allBtnUnable = true;
                    setBtnEnble(false);
                } else if (mOrderCarInfo.getDjzt().equals("审核未结算")) {
                    img_wg_lay.setVisibility(View.VISIBLE);
                    paigong.setText("取消完工");
                    paigong.setEnabled(true);
                    setBtnEnble(true);

                    if(mOrderAdapter==null||mPeijianAdapter==null){
                        return;
                    }
                    mOrderAdapter.notifyDataSetChanged();
                    mPeijianAdapter.notifyDataSetChanged();
                } else if (mOrderCarInfo.getDjzt().equals("已出厂")) {
                    img_wg_lay.setVisibility(View.VISIBLE);
                    OrderBeanInfo.notDelete = true;
                    OrderBeanInfo.allBtnUnable = true;
                    paigong.setText("取消完工");
                    yccType = true;
                    djztUnable = true;
                    //total_jiesuan.setEnabled(false);
                    //total_jiesuan2.setEnabled(false);
                    // total_jiesuan2.setBackgroundColor(Color.parseColor("#cccccc"));
                    setBtnEnble(false);
                    if(mOrderAdapter==null||mPeijianAdapter==null){
                        return;
                    }
                    mOrderAdapter.notifyDataSetChanged();
                    mPeijianAdapter.notifyDataSetChanged();

                    // setJiesuanEable(true);
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }else if(msgInt == 111){
            try {
                paigong.setText("取消完工");
                project_ck.setEnabled(false);
                temp_pro.setEnabled(false);
                peijianku.setEnabled(false);
                OrderBeanInfo.allBtnUnable = true;
                //total_jiesuan.setBackgroundColor(Color.parseColor("#cccccc"));
                //total_jiesuan2.setBackgroundColor(Color.parseColor("#cccccc"));
                setBtnEnble(false);
                if (mOrderAdapter == null || mPeijianAdapter == null) {
                    return;
                }
                mOrderAdapter.notifyDataSetChanged();
                mPeijianAdapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(msgInt == 112){
            try {
                OrderBeanInfo.allBtnUnable = false;
                setBtnEnble(true);
                total_jiesuan.setEnabled(true);
                total_jiesuan2.setEnabled(true);
                if (mOrderAdapter == null || mPeijianAdapter == null) {
                    return;
                }
                mOrderAdapter.notifyDataSetChanged();
                mPeijianAdapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void setBtnEnble(boolean isEnble){
        project_ck.setBackgroundColor(Color.parseColor(isEnble?"#89c997":"#cccccc"));
        temp_pro.setBackgroundColor(Color.parseColor(isEnble?"#89c997":"#cccccc"));
        peijianku.setBackgroundColor(Color.parseColor(isEnble?"#89c997":"#cccccc"));
        banjinpenqi.setBackgroundColor(Color.parseColor(isEnble?"#89c997":"#cccccc"));
        paigong.setBackgroundColor(Color.parseColor(isEnble?"#89c997":"#cccccc"));

        paigong.setEnabled(isEnble);
        peijianku.setEnabled(isEnble);
        banjinpenqi.setEnabled(isEnble);
        temp_pro.setEnabled(isEnble);
        project_ck.setEnabled(isEnble);
    }

    private void setJiesuanEable(boolean isEnable){
        djztUnable = isEnable;
        total_jiesuan.setEnabled(!isEnable);
        total_jiesuan.setBackgroundColor(isEnable?Color.parseColor("#89c997"):Color.parseColor("#cccccc"));
        total_jiesuan2.setEnabled(!isEnable);
        total_jiesuan2.setBackgroundColor(isEnable?Color.parseColor("#89c997"):Color.parseColor("#cccccc"));
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


    private void getProjectListData() {
        mProjectList.clear();
        mHandler.sendEmptyMessage(100);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_down_jsdmx_xlxm");
        dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    JSONArray dataArray = (JSONArray) resMap.get("data");

                    List<ProjectBean> projectBeans = JSONArray.parseArray(dataArray.toJSONString(), ProjectBean.class);
                    if (projectBeans != null) {
                        mProjectList.addAll(projectBeans);
                    }
                    mHandler.sendEmptyMessageDelayed(100,100);
                } else {
                    if(resMap.get("msg")!=null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }else{
                        toastMsg = "网络异常，删除失败";
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


    private void getPjListData(){
        mPeiJianList.clear();
        mHandler.sendEmptyMessage(101);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_down_jsdmx_pjclmx");
        dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ("ok".equals(state)) {
                    JSONArray dataArray = (JSONArray) resMap.get("data");
                    List<PeijianBean> projectBeans = JSONArray.parseArray(dataArray.toJSONString(), PeijianBean.class);
                    if (projectBeans != null) {
                        mPeiJianList.addAll(projectBeans);
                    }
                    mHandler.sendEmptyMessageDelayed(101,100);
                } else {
                    if(resMap.get("msg")!=null) {
                        toastMsg = (String) resMap.get("msg");
                        mHandler.sendEmptyMessage(TOAST_MSG);
                    }else{
                        toastMsg = "网络异常，删除失败";
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

    private void judgeToStatu(){

        if(djztUnable){
            return;
        }
        gdStatu = paigong.getText().toString();
        isToPaigong = true;

            if(gdStatu.equals("派工")) {
                CheckBeanInfo beanInfo2 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10200");
                if(beanInfo2!=null && "1".equals(beanInfo2.getOpen())) {

                }else{
                    isToPaigong = false;
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }

                startActivity(new Intent(ProjectOrderActivity.this,ProjectPaigongActivity.class));
            //去派工页面
            }else if(gdStatu.equals("全部完工")){

                CheckBeanInfo beanInfo2 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10200");
                if(beanInfo2!=null && "1".equals(beanInfo2.getSh())) {

                }else{
                    isToPaigong = false;
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }



                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("db", sp.getString(Constance.Data_Source_name));
                dataMap.put("function", "sp_fun_update_repair_list_state");
                dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
                dataMap.put("states", "审核未结算");
                dataMap.put("xm_state", "已完工");

                HttpClient client = new HttpClient();
                client.post(Util.getUrl(), dataMap, new IGetDataListener() {
                    @Override
                    public void onSuccess(String json) {
                        System.out.println("11111");
                        Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                        String state = (String) resMap.get("state");
                        if ("ok".equals(state)) {
                            toastMsg = "已完工";
                            mHandler.sendEmptyMessage(TOAST_MSG);
                            mHandler.sendEmptyMessage(111);
                        } else {
                            if (resMap.get("msg") != null) {
                                toastMsg = (String) resMap.get("msg");
                                mHandler.sendEmptyMessage(TOAST_MSG);
                            } else {
                                toastMsg = "网络异常，删除失败";
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
            }else if(gdStatu.equals("取消完工")){
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("db", sp.getString(Constance.Data_Source_name));
                dataMap.put("function", "sp_fun_update_repair_list_state");
                dataMap.put("jsd_id", sp.getString(Constance.JSD_ID));
                dataMap.put("states", "修理中");
                dataMap.put("xm_state", "待质检");

                HttpClient client = new HttpClient();
                client.post(Util.getUrl(), dataMap, new IGetDataListener() {
                    @Override
                    public void onSuccess(String json) {
                        System.out.println("11111");
                        Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                        String state = (String) resMap.get("state");
                        if ("ok".equals(state)) {

                            mHandler.sendEmptyMessage(112);
                        } else {
                            if (resMap.get("msg") != null) {
                                toastMsg = (String) resMap.get("msg");
                                mHandler.sendEmptyMessage(TOAST_MSG);
                            } else {
                                toastMsg = "网络异常，删除失败";
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

    }



    //更新项目详情
    private void addProjectDetailServer(final int position,final ProjectBean bean){

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_upload_maintenance_project_detail");
        dataMap.put("jsd_id",sp.getString(Constance.JSD_ID));
        dataMap.put("xlxm",bean.getXlxm());
        dataMap.put("xlf",bean.getXlf());
        dataMap.put("zk",bean.getZk());
        dataMap.put("wxgz",bean.getWxgz());

        dataMap.put("xh",bean.getXh());
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ( "ok".equals(state)) {
                    mProjectList.set(position,bean);
                    mHandler.sendEmptyMessage(100);
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



//增加临时项目
    private void addTmpServer(final ProjectBean bean){

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_upload_maintenance_project_detail");
        dataMap.put("jsd_id",sp.getString(Constance.JSD_ID));
        dataMap.put("xlxm",bean.getXlxm());
        dataMap.put("xlf",bean.getXlf());
        dataMap.put("zk","0.00");
        dataMap.put("wxgz",bean.getWxgz());
        dataMap.put("pgzje","0");
        dataMap.put("pgzgs","1");
        dataMap.put("xh","0");
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                System.out.println("11111");
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                if ( "ok".equals(state)) {
                    mProjectList.add(bean);
                    mHandler.sendEmptyMessage(100);
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


    //增加临时项目
    private void addPjToServer(final PeijianBean bean){

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_upload_parts_project_detail");
        dataMap.put("jsd_id",sp.getString(Constance.JSD_ID));
        dataMap.put("pjbm",bean.getPjbm());
        dataMap.put("pjmc",bean.getPjmc());
        dataMap.put("ck",bean.getCk());
        dataMap.put("cd",bean.getCd());
        dataMap.put("cx",bean.getCx());
        dataMap.put("dw","");
        dataMap.put("property","");
        dataMap.put("zt","");
        dataMap.put("ssj",bean.getSsj());
        dataMap.put("cb",bean.getCb());
        dataMap.put("xh",bean.getXh());
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
                if ( "ok".equals(state)) {
                   getPjListData();
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



    private void upDateTotalMoney() {

        float pjTotalCb = 0;
        if (mPeiJianList != null && mPeiJianList.size() > 0) {
            for (int i = 0; i < mPeiJianList.size(); i++) {
                PeijianBean pjData = mPeiJianList.get(i);
                pjTotalCb += Float.parseFloat(pjData.getCb());
            }
        }

        float pjfTotalFloat = 0;

        if(pjfTotal.getText()!=null&&!TextUtils.isEmpty(pjfTotal.getText().toString())){
            String pjfStr = pjfTotal.getText().toString().replaceAll("总计:","");
            if(!TextUtils.isEmpty(pjfStr)) {
                pjfTotalFloat = Float.parseFloat(pjfStr);
            }
        }
        float wxfTotalFloat = 0;

        if(wxfTotal.getText()!=null&&!TextUtils.isEmpty(wxfTotal.getText().toString())){
            String wxfStr = wxfTotal.getText().toString();
            wxfTotalFloat = Float.parseFloat(wxfStr.replaceAll("总计:",""))-totalXlfZk;
        }
        if (mOrderCarInfo==null|| (pjfTotalFloat +wxfTotalFloat) != Float.parseFloat(mOrderCarInfo.getZje())) {

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("db", sp.getString(Constance.Data_Source_name));
            dataMap.put("function", "sp_fun_update_repair_main_money");

            dataMap.put("jsd_id",sp.getString(Constance.JSD_ID));
            dataMap.put("zje",(pjfTotalFloat +wxfTotalFloat)+"");

            dataMap.put("wxfzj",wxfTotalFloat+"");
            dataMap.put("clfzj", pjfTotalFloat+"");
            dataMap.put("clcb",pjTotalCb+"");
            HttpClient client = new HttpClient();
            client.post(Util.getUrl(), dataMap, new IGetDataListener() {
                @Override
                public void onSuccess(String json) {

                    System.out.println("11111");
                    Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                    String state = (String) resMap.get("state");
                    if ( "ok".equals(state)) {
                        totalXlfZk=0;
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pj:
                listview.setVisibility(View.GONE);
                listview2.setVisibility(View.VISIBLE);
                tv_pro.setTextColor(Color.parseColor("#666666"));
                tv_pro.setBackgroundColor(Color.parseColor("#A4A3A3"));
                tv_pj.setTextColor(Color.parseColor("#ffffff"));
                tv_pj.setBackgroundColor(Color.parseColor("#ff9db4"));
                pro_btn_lay.setVisibility(View.GONE);
                pj_btn_lay.setVisibility(View.VISIBLE);

                break;
            case R.id.tv_pro:
                listview.setVisibility(View.VISIBLE);
                listview2.setVisibility(View.GONE);
                tv_pro.setTextColor(Color.parseColor("#ffffff"));
                tv_pro.setBackgroundColor(Color.parseColor("#ff9db4"));
                tv_pj.setTextColor(Color.parseColor("#666666"));
                tv_pj.setBackgroundColor(Color.parseColor("#A4A3A3"));
                pro_btn_lay.setVisibility(View.VISIBLE);
                pj_btn_lay.setVisibility(View.GONE);
                break;
            case R.id.temp_pro:
                OrderAddTmpDialog dialog1 = new OrderAddTmpDialog(this);
                dialog1.setOnClickListener(new OrderAddTmpDialog.OnClickListener() {
                    @Override
                    public void rightBtnClick(ProjectBean newBean) {
                        addTmpServer(newBean);

                    }
                });
                dialog1.show();
                break;
            case R.id.peijianku:
                CheckBeanInfo beanInfo4 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                if(beanInfo4!=null && "1".equals(beanInfo4.getNew_bill())) {

                }else{
                    toastMsg = "您没有该权限，请联系管理员";
                    mHandler.sendEmptyMessage(TOAST_MSG);
                    return;
                }


                isToPeijian = true;
                startActivity(new Intent(ProjectOrderActivity.this,PeijianSelectActivity.class));
                break;
                case R.id.project_ck:
                    CheckBeanInfo beanInfo3 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"10600");
                    if(beanInfo3!=null && "1".equals(beanInfo3.getNew_bill())) {

                    }else{
                        toastMsg = "您没有该权限，请联系管理员";
                        mHandler.sendEmptyMessage(TOAST_MSG);
                        return;
                    }
                startActivity(new Intent(ProjectOrderActivity.this,ProjectSelectActivity.class));

                break;
            case R.id.car_home_page:
            case R.id.car_home_page2:
                startActivity(new Intent(ProjectOrderActivity.this,ProjectActivity.class));
                break;
                case R.id.paigong:

                    judgeToStatu();

                break;
            case R.id.total_jiesuan:
            case R.id.total_jiesuan2:


                isToJiesuan=true;
                Intent intent = new Intent(ProjectOrderActivity.this,ProjectJiesuanActivity.class);
                if(mOrderCarInfo!=null) {
                    intent.putExtra("jsdStatu", mOrderCarInfo.getDjzt());
                }
                startActivity(intent);
                break;
            default:

                break;
        }
    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        upDateTotalMoney();
    }

    @Override
    protected void onDestroy() {
        mOrderAdapter = null;
        mPeijianAdapter = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        guzhangmiaoshu.setText("故障描述:"+sp.getString(Constance.GUZHNAGMIAOSHU));
        if(isToPeijian) {
            isToPeijian = false;
            getPjListData();
        }
        if(isToPaigong){
            isToPaigong = false;
            getJsdInfo();
            getProjectListData();
        }

        if(isToJiesuan){
            isToJiesuan=false;
            getJsdInfo();
        }

    }
}
