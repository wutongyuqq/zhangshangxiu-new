package com.shoujia.zhangshangxiu.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shoujia.zhangshangxiu.entity.CheckBeanInfo;

import java.util.List;
import java.util.Map;

public class CheckUtil {

    public static CheckBeanInfo getCheckInfo(String json, String numStr){
        Map<String, Object> resMap = (Map<String, Object>) JSON.parse(json);
        String state = (String) resMap.get("state");
        if ("ok".equals(state)) {
            JSONArray dataArray = (JSONArray) resMap.get("data");
            List<CheckBeanInfo> dataList = JSONArray.parseArray(dataArray.toJSONString(), CheckBeanInfo.class);
            if(dataList!=null && !dataList.isEmpty()){
                for(CheckBeanInfo info:dataList){
                    if(!TextUtils.isEmpty(info.getMenu_right())&&info.getMenu_right().equals(numStr)){
                        return info;
                    }
                }
            }

        }
        return null;
    }
}
