package com.shoujia.zhangshangxiu.msgcenter.help;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shoujia.zhangshangxiu.base.BaseHelper;
import com.shoujia.zhangshangxiu.entity.ManageInfo;
import com.shoujia.zhangshangxiu.entity.OrderBean;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;
import com.shoujia.zhangshangxiu.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryOrderDataHelper extends BaseHelper {

    private Activity mActivity;

    private SharePreferenceManager sp;
    private String pre_row_number = "0";
    private  List<OrderBean> manageInfos;

    List<OrderBean> mManageInfos;
    GetDataListener mDataListener;
    public QueryOrderDataHelper(Activity activity){
        super(activity);
        this.mActivity = activity;
        sp = new SharePreferenceManager(mActivity);
        manageInfos = new ArrayList<>();

    }


    public void setPreZero(GetDataListener dataListener){
        manageInfos.clear();
        pre_row_number = "0";
        this.mDataListener = dataListener;
    }



    //获取车辆数据
    public void getListData(final String dates,final String datee){
        if(pre_row_number!=null&&pre_row_number.equals("end")){
            if(mDataListener!=null){
                mDataListener.getData(manageInfos);
                return;
            }
        }

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_query_repair_history");
        dataMap.put("company_code", sp.getString(Constance.COMP_CODE));
        dataMap.put("dates",dates);
        dataMap.put("datee", datee);
        dataMap.put("pre_row_number", pre_row_number);

        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                pre_row_number = (String) resMap.get("pre_row_number");
                if ( "ok".equals(state)) {
                    JSONArray dataArray = (JSONArray) resMap.get("data");
                    List<OrderBean> dataList = JSONArray.parseArray(dataArray.toJSONString(),OrderBean.class);
                    manageInfos.addAll(dataList);
                }else{
                    pre_row_number = "end";
                }

                getListData(dates,datee);
            }

            @Override
            public void onFail() {

            }
        });
    }

    private Activity getActivity(){
        return mActivity;
    }

    public interface GetDataListener{
        void getData(List<OrderBean> manageInfos);
    }

}
