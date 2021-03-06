package com.shoujia.zhangshangxiu.support;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.home.HomeActivity;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;

public class NavSupport  implements View.OnClickListener{

    private ImageView right_btn,left_btn;
    private Activity mActivity;
    private TextView tv_title;
    private SharePreferenceManager sp;
    private int mFromType;
    public NavSupport(Activity activity, int fromType){
        this.mActivity = activity;
        this.mFromType = fromType;
        initView();

    }



    private void initView(){
        sp = new SharePreferenceManager(mActivity.getApplicationContext());
        right_btn = (ImageView) findViewById(R.id.right_btn);
        left_btn = (ImageView) findViewById(R.id.left_btn);
        tv_title = (TextView) findViewById(R.id.title);
        right_btn.setOnClickListener(this);
        left_btn.setOnClickListener(this);
        if(mFromType==1){
            setTittle("车辆接待");
            setLeftBtnVisible(false);
        }else if(mFromType==2){
            setTittle("业务导航");
            setLeftBtnVisible(true);
        }else if(mFromType==3){
            setTittle("在厂车辆");
            setLeftBtnVisible(true);
        }else if(mFromType==4){
            setTittle("项目选择");
            setLeftBtnVisible(true);
        }else if(mFromType==6){
            setTittle("搜索车辆");
            setLeftBtnVisible(true);
        }else if(mFromType==7){
            setTittle("已完成工单");
            setLeftBtnVisible(true);
        }else if(mFromType==8){
            setTittle("我的绩效");
            setLeftBtnVisible(true);
        }else if(mFromType==11){
            setTittle("工单("+sp.getString(Constance.JSD_ID)+")");
            setLeftBtnVisible(true);
        }else if(mFromType==12){
            setTittle("结算");
            setLeftBtnVisible(true);
        }else if(mFromType==13){
            setTittle("派工");
            setLeftBtnVisible(true);
        }else if(mFromType==14){
            setTittle("收银");
            setLeftBtnVisible(true);
        }else if(mFromType==15){
            setTittle("配件");
            setLeftBtnVisible(true);
        }else if(mFromType==16){
            setTittle("项目选择");
            setLeftBtnVisible(true);
        }else if(mFromType==17){
            setTittle("领工");
            setLeftBtnVisible(true);
        }else if(mFromType==18){
            setTittle("工单查询");
            setLeftBtnVisible(true);
        }else if(mFromType==19){
            setTittle("历史记录");
            setLeftBtnVisible(true);
        }else if(mFromType==20){
            setTittle("客户查询");
            setLeftBtnVisible(true);
        }else if(mFromType==21){
            setTittle("配件查询");
            setLeftBtnVisible(true);
        }else if(mFromType==22){
            setTittle("销售单");
            setLeftBtnVisible(true);
        }else if(mFromType==23){
            setTittle("入库单");
            setLeftBtnVisible(true);
        }else if(mFromType==24){
            setTittle("供应商");
            setLeftBtnVisible(true);
        }
    }

    public void setLeftBtnVisible(boolean isVisible){
        left_btn.setVisibility(isVisible?View.VISIBLE:View.GONE);
        right_btn.setVisibility(isVisible?View.VISIBLE:View.GONE);

    }



    public void setTittle(String title){
        tv_title.setText(title);
    }

    private View findViewById(int id){
        return mActivity.findViewById(id);
    }
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.left_btn:
                mActivity.finish();
                break;

            case R.id.right_btn:
                intent = new Intent(mActivity,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
                if(mActivity!=null) {
                    mActivity.finish();
                }
                break;
            default:
                break;
        }

    }
}
