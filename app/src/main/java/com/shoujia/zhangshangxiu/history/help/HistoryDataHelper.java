package com.shoujia.zhangshangxiu.history.help;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shoujia.zhangshangxiu.base.BaseHelper;
import com.shoujia.zhangshangxiu.entity.ManageInfo;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;
import com.shoujia.zhangshangxiu.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryDataHelper extends BaseHelper {

    private Activity mActivity;

    private SharePreferenceManager sp;
    private String pre_row_number="0";
     GetDataListener mDataListener;


    public HistoryDataHelper(Activity activity){
        super(activity);
        this.mActivity = activity;
        sp = new SharePreferenceManager(mActivity);
    }

    public void setPreZero(GetDataListener dataListener){
        pre_row_number = "0";
        this.mDataListener = dataListener;
    }


    //获取车辆数据
    public void getCardList(final String startDateStr, final String endDateStr){


        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("db", sp.getString(Constance.Data_Source_name));
        dataMap.put("function", "sp_fun_down_repair_history");
        dataMap.put("customer_id", sp.getString(Constance.CUSTOMER_ID));
        dataMap.put("dates", startDateStr);
        dataMap.put("datee", endDateStr);
        HttpClient client = new HttpClient();
        client.post(Util.getUrl(), dataMap, new IGetDataListener() {
            @Override
            public void onSuccess(String json) {
                Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
                String state = (String) resMap.get("state");
                pre_row_number = (String) resMap.get("pre_row_number");
                if ( "ok".equals(state)) {

                    JSONArray dataArray = (JSONArray) resMap.get("data");
                    List<ManageInfo> dataList = JSONArray.parseArray(dataArray.toJSONString(),ManageInfo.class);
                    mDataListener.getData(dataList);
                }else {
                    mDataListener.getData(new ArrayList<ManageInfo>());
                }
            }

            @Override
            public void onFail() {
                mDataListener.onFail();

            }
        });
    }

    private Activity getActivity(){
        return mActivity;
    }

    public interface GetDataListener{
        public static final int TYPE_UN_IN = 1;//车辆已进厂未完工
        void getData(List<ManageInfo> manageInfoList);
        void onFail();
    }
}
