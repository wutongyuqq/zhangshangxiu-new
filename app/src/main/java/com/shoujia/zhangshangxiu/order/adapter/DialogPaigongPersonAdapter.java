package com.shoujia.zhangshangxiu.order.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.entity.RepairInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/2/26 0026.
 */
public class DialogPaigongPersonAdapter extends BaseAdapter {
    private  List<RepairInfo> listData;
    Context context;
    Handler handler;

    public DialogPaigongPersonAdapter(Context context, List<RepairInfo> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        if (listData.size() == 0) {
            return 0;
        }
        return listData.size();
    }


    public void setListData(List<RepairInfo> carInfos){
        if(carInfos==null){
            return;
        }
        if(listData!=null){
            listData.clear();
        }
        listData.addAll(carInfos);
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Hodler hodler;
        if (convertView == null) {
            hodler = new Hodler();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.dialog_paigong_person_item, null);
            hodler.name = convertView.findViewById(R.id.name);
            hodler.iv_select = convertView.findViewById(R.id.iv_select);
            convertView.setTag(hodler);
        }else{
            hodler = (Hodler) convertView.getTag();
        }
        RepairInfo bean = listData.get(position);
        hodler.name.setText(bean.getXlg());
        if(listData.get(position).isSelected()){
            hodler.iv_select.setImageResource(R.drawable.right_now);
        }else{
            hodler.iv_select.setImageResource(R.drawable.right_now_no);
        }
        return convertView;
    }


    class Hodler {
        TextView name;
        ImageView iv_select;

    }
}





