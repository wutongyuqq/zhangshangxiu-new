package com.shoujia.zhangshangxiu.history;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseActivity;
import com.shoujia.zhangshangxiu.car.CarListActivity;
import com.shoujia.zhangshangxiu.db.DBManager;
import com.shoujia.zhangshangxiu.entity.ManageInfo;
import com.shoujia.zhangshangxiu.entity.OrderBean;
import com.shoujia.zhangshangxiu.history.adapter.HistoryListAdapter;
import com.shoujia.zhangshangxiu.history.help.HistoryDataHelper;
import com.shoujia.zhangshangxiu.manager.help.ManageDataHelper;
import com.shoujia.zhangshangxiu.order.ProjectOrderActivity;
import com.shoujia.zhangshangxiu.order.adapter.WxgzListAdapter;
import com.shoujia.zhangshangxiu.support.InfoSupport;
import com.shoujia.zhangshangxiu.support.NavSupport;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.DateUtil;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;
import com.shoujia.zhangshangxiu.util.Util;
import com.shoujia.zhangshangxiu.view.CustomDatePicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/23 0023.
 * 首页
 */
public class HistoryActivity extends BaseActivity implements View.OnClickListener{
	private final String TAG = "HomeActivity";
  private NavSupport navSupport;
  private List<ManageInfo> mInfoList;
	List<ManageInfo> mTotalBeans;
	private List<ManageInfo> manageInfoList;
  private HistoryListAdapter carListAdapter;
	ListView mListview;
	TextView select_date_start,select_date_end,select_gz;
	ImageView query_btn;
	String typeStr;
	private int mQueryType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history_list);
		initView();
		initData();

	}

	private void initView() {

		mInfoList = new ArrayList<>();
		navSupport = new NavSupport(this,19);
		mListview = findViewById(R.id.listview);
		carListAdapter = new HistoryListAdapter(this,mInfoList);
		mListview.setAdapter(carListAdapter);
		new InfoSupport(this);
		mTotalBeans = new ArrayList<>();
		select_date_start = findViewById(R.id.select_date_start);
		select_date_end = findViewById(R.id.select_date_end);
		query_btn = findViewById(R.id.query_btn);
		select_gz = findViewById(R.id.select_gz);

		select_date_start.setOnClickListener(this);
		select_date_end.setOnClickListener(this);
		query_btn.setOnClickListener(this);
		select_gz.setOnClickListener(this);
		mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				ManageInfo info = mInfoList.get(i);
				SharePreferenceManager sp = new SharePreferenceManager(HistoryActivity.this);
				sp.putString(Constance.JSD_ID,info.getJsd_id());
				sp.putString(Constance.CHEJIAHAO,info.getCjhm());
				sp.putString(Constance.CURRENTCP,info.getCp());
				sp.putString(Constance.CHEXING,info.getCx());
				sp.putString(Constance.JIECHEDATE,info.getJc_date());
				sp.putString(Constance.YUWANGONG,info.getYwg_date());
				startActivity(new Intent(HistoryActivity.this,ProjectOrderActivity.class));
			}
		});
	}

	//初始化数据
	private void initData(){
    	 typeStr = getIntent().getStringExtra("typeStr");
		String endDate = DateUtil.getCurrentDate();
		String startDate = endDate.substring(0,endDate.length()-2)+"01";
		select_date_end.setText(endDate);
		select_date_start.setText("2017-01-01");
    	getData();
		getWxgzList();
	}



	@Override
	protected void updateUIThread(int msgInt){
		if(msgInt==302){
			String gzStr = select_gz.getText().toString();
			if(mQueryType != 0){
				mInfoList.clear();
				for(ManageInfo bean : mTotalBeans){
					if((bean.getWxgz().contains(gzStr)||gzStr.equals("全部"))){
						mInfoList.add(bean);
					}
				}
				carListAdapter.notifyDataSetChanged();
			}else{
				carListAdapter.notifyDataSetChanged();
			}
		}
	}


	private void getData(){
		mInfoList.clear();
		HistoryDataHelper carDataHelper = new HistoryDataHelper(this);
		carDataHelper.setPreZero(new HistoryDataHelper.GetDataListener() {
			@Override
			public void getData(List<ManageInfo> manageInfoList) {
				if(manageInfoList!=null) {
					mTotalBeans = manageInfoList;
					mInfoList.addAll(manageInfoList);
					mQueryType = 0;
					mHandler.sendEmptyMessage(302);
				}

			}
		});
		String startDate = select_date_start.getText().toString();
		String endDate = select_date_end.getText().toString();
		String startDateStr = startDate+" 00:00:00";
		String endDateStr = endDate+" 23:59:59";
		if(typeStr==null){
			typeStr="";
		}
		carDataHelper.getCardList(startDateStr, endDateStr);
	}

    @Override
    public void onClick(View v) {
		switch (v.getId()){
			case R.id.select_date_start:
				selectDate(select_date_start);
				break;
			case R.id.select_date_end:
				selectDate(select_date_end);
				break;
			case R.id.query_btn:
				mQueryType = 1;
				getData();
				break;
			case R.id.select_gz:
				initPopWindow();
				break;
			default:

				break;
		}
    }

private void getWxgzList(){
	manageInfoList = new ArrayList<>();
	ManageDataHelper helper = new ManageDataHelper(this);
        helper.setPreZero();
        helper.getListData(0, new ManageDataHelper.GetDataListener() {
		@Override
		public void getData(List<ManageInfo> manageInfos) {
			manageInfoList.clear();
			if(manageInfos!=null) {
				manageInfoList.addAll(manageInfos);
			}
			DBManager dbManager = DBManager.getInstanse(HistoryActivity.this);
			dbManager.insertManagerListData(manageInfos);
		}
	});
}


	private void initPopWindow(){


		// 用于PopupWindow的View
		View contentView=LayoutInflater.from(this).inflate(R.layout.popwindow_bank_rate, null, false);
		ListView mListView = contentView.findViewById(R.id.listview);
		// 创建PopupWindow对象，其中：
		// 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
		// 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
		final PopupWindow mPopupWindow=new PopupWindow(contentView, Util.dp2px(this,120),
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.showAsDropDown(select_gz);
		mPopupWindow.setTouchable(true); // 设置屏幕点击事件
		final DBManager dbManager = DBManager.getInstanse(this);
		final List<String> wxgzList = new ArrayList<>();

		wxgzList.add("全部");
		wxgzList.addAll(dbManager.queryWxgzListData());

		WxgzListAdapter homeCarInfoAdapter = new WxgzListAdapter(this,wxgzList);//新建并配置ArrayAapeter
		mListView.setAdapter(homeCarInfoAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				mPopupWindow.dismiss();
				String info = wxgzList.get(position);
				select_gz.setText(info);
				mQueryType = 2;
				getData();
			}
		});

		// 设置PopupWindow的背景
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 设置PopupWindow是否能响应外部点击事件
		mPopupWindow.setOutsideTouchable(true);
		// 设置PopupWindow是否能响应点击事件
		mPopupWindow.setTouchable(true);
		// 显示PopupWindow，其中：
		// 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移

		// 或者也可以调用此方法显示PopupWindow，其中：
		// 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
		// 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
		// window.showAtLocation(parent, gravity, x, y);
	}



	private void selectDate(final TextView textView){
		CustomDatePicker customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
			@Override
			public void handle(String time) { // 回调接口，获得选中的时间
				Log.d("yyyyy", time);
				if(!TextUtils.isEmpty(time)&&time.length()>=10){
					String pickTime = time.substring(0,10);
					textView.setText(pickTime);
				}
			}
		},"2007-01-01 00:00","2025-12-31 00:00");
		customDatePicker.show();
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
