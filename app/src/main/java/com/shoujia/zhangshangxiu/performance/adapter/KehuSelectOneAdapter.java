package com.shoujia.zhangshangxiu.performance.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.entity.PartsBean;
import com.shoujia.zhangshangxiu.performance.entity.CustomInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/2/26 0026.
 */
public class KehuSelectOneAdapter extends BaseAdapter {
    private  List<CustomInfo> listData;
    Context context;
    Handler handler;
    private EditClickListener editClickListener;
    private DeleteClickListener deleteClickListener;
    public KehuSelectOneAdapter(Context context, List<CustomInfo> listData) {
        this.context = context;
        this.listData = listData;
    }

   /* public void setList(List<CustomInfo> list){
        if(listData!=null){
            listData.clear();
        }
        listData.addAll(list);
    }
*/    @Override
    public int getCount() {
        if (listData.size() == 0) {
            return 0;
        }
        return listData.size();
    }

    public void setEditClickListener(EditClickListener listener){
        this.editClickListener = listener;
    }

    public void setDeleteClickListener(DeleteClickListener listener){
        this.deleteClickListener = listener;
    }

    public void setListData(List<CustomInfo> carInfos){
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
                    R.layout.kehu_select_one_item, null);
            hodler.pj_name = convertView.findViewById(R.id.pj_name);
            hodler.pj_cang = convertView.findViewById(R.id.pj_cang);
            hodler.pj_xsj = convertView.findViewById(R.id.pj_xsj);
            hodler.pj_gg = convertView.findViewById(R.id.pj_gg);
            hodler.pj_kcl = convertView.findViewById(R.id.pj_kcl);
            hodler.pj_xh = convertView.findViewById(R.id.pj_xh);
            hodler.select_one = convertView.findViewById(R.id.select_one);
            hodler.pj_pjjj = convertView.findViewById(R.id.pj_pjjj);
            convertView.setTag(hodler);
        }else{
            hodler = (Hodler) convertView.getTag();
        }
        CustomInfo bean = listData.get(position);
        if(bean!=null){
            hodler.pj_name.setText("客户名称："+bean.getCustomer_name());
            hodler.pj_cang.setText("手机号码："+bean.getMobile());
            hodler.pj_xsj.setText("会员余额：￥"+bean.getCard_ye());
            hodler.pj_gg.setText("欠款金额："+bean.getDebt());
            hodler.pj_kcl.setText("固定电话："+bean.getPhone());
            hodler.pj_xh.setText("储值余额："+bean.getCz_ye());
            hodler.pj_pjjj.setText("累计消费："+bean.getExpend());
        }
        return convertView;
    }
    public interface EditClickListener{
        void editClick(int position);
    }

    public interface DeleteClickListener{
        void deleteClick(int position);
    }

    class Hodler {
        TextView pj_name;
        TextView pj_cang;
        TextView pj_xsj;
        TextView pj_gg;
        TextView pj_kcl;
        TextView pj_xh;
        TextView pj_pjjj;
        LinearLayout select_one;

    }
}





