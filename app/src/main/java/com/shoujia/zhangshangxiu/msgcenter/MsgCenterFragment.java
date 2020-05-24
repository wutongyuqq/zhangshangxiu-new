package com.shoujia.zhangshangxiu.msgcenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseFragment;
import com.shoujia.zhangshangxiu.entity.CheckBeanInfo;
import com.shoujia.zhangshangxiu.performance.PerformanceActivity;
import com.shoujia.zhangshangxiu.util.CheckUtil;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;

/**
 * Created by Administrator on 2017/2/23 0023.
 *
 */
public class MsgCenterFragment extends BaseFragment implements View.OnClickListener{

    private View mView;
    private TextView performance,query_order;
    private SharePreferenceManager sp;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = View.inflate(getActivity(), R.layout.fragment_msg_center, null);
        performance = mView.findViewById(R.id.performance);
        query_order = mView.findViewById(R.id.query_order);
        performance.setOnClickListener(this);
        query_order.setOnClickListener(this);
        sp = new SharePreferenceManager(getContext());

        return mView;
    }

    public void updateUIThread(Message msg) {
        int msgInt = msg.what;

    }


    private View findViewById(int id){
        return mView.findViewById(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.performance:
                startActivity(new Intent(getActivity(),PerformanceActivity.class));
                break;
                case R.id.query_order:
                    CheckBeanInfo beanInfo2 = CheckUtil.getCheckInfo(sp.getString(Constance.CHECKE_DATA),"20200");
                    if(beanInfo2!=null && "1".equals(beanInfo2.getSh())) {

                    }else{
                        toastMsg = "您没有该权限，请联系管理员";
                        mHandler.sendEmptyMessage(TOAST_MSG);
                        return;
                    }

                startActivity(new Intent(getActivity(),QueryOrderActivity.class));
                break;

            default:
                break;
        }

    }
}
