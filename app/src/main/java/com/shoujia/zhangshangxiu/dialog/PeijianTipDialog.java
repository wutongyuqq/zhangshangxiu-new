

package com.shoujia.zhangshangxiu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.entity.PeijianBean;
import com.shoujia.zhangshangxiu.util.Util;

public class PeijianTipDialog {
    private Context mContext;
    Dialog mDialog;
    private PeijianBean mPeijianBean;

    public PeijianTipDialog(Context context, PeijianBean peijianBean) {
        this.mContext = context;
        this.mPeijianBean = peijianBean;
    }


    public void show() {
        if (mPeijianBean == null) {
            return;
        }
        //1、使用Dialog、设置style
        mDialog = new Dialog(mContext, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(mContext, R.layout.dialog_peijian_tip, null);
        TextView content = view.findViewById(R.id.content);
        String contentStr = "名称：" + mPeijianBean.getPjmc() + "\n";
        contentStr += "型号：" + mPeijianBean.getPjbm() + "\n";
        contentStr += "仓库：" + mPeijianBean.getCk() + "\n";
        contentStr += "数量：" + mPeijianBean.getSl() + "\n";
        contentStr += "单价：" + mPeijianBean.getSsj() + "\n";

        float totalMoney = Float.parseFloat(mPeijianBean.getSl()) * Float.parseFloat(mPeijianBean.getSsj());
        contentStr += "合计：" + totalMoney;

        content.setText(contentStr);

        mDialog.setContentView(view);

        Window window = mDialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


}
