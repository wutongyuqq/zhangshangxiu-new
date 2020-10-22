package com.shoujia.zhangshangxiu.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.shoujia.zhangshangxiu.R;
import com.shoujia.zhangshangxiu.base.BaseFragment;
import com.shoujia.zhangshangxiu.db.DBManager;
import com.shoujia.zhangshangxiu.dialog.DatePickerDialog;
import com.shoujia.zhangshangxiu.entity.CarInfo;
import com.shoujia.zhangshangxiu.entity.FirstIconInfo;
import com.shoujia.zhangshangxiu.entity.RepairInfo;
import com.shoujia.zhangshangxiu.entity.SecondIconInfo;
import com.shoujia.zhangshangxiu.home.help.HomeDataHelper;
import com.shoujia.zhangshangxiu.http.HttpClient;
import com.shoujia.zhangshangxiu.http.IGetDataListener;
import com.shoujia.zhangshangxiu.support.NavSupport;
import com.shoujia.zhangshangxiu.support.TabSupport;
import com.shoujia.zhangshangxiu.util.Constance;
import com.shoujia.zhangshangxiu.util.SharePreferenceManager;
import com.shoujia.zhangshangxiu.util.Util;
import com.shoujia.zhangshangxiu.view.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23 0023.
 * 首页
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener{
	private final String TAG = "HomeActivity";
	private NavSupport navSupport;
	SharePreferenceManager sp;
	private int mLastIndex;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};
	@Override
	public void onSaveInstanceState(Bundle outState) {
		//super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		sp = new SharePreferenceManager(getApplicationContext());

		TabSupport tabSupport = new TabSupport(this);
		navSupport = new NavSupport(this,1);
		//initData();
		final RelativeLayout root_view = findViewById(R.id.root_view);
		final RelativeLayout rl_bottom = findViewById(R.id.rl_bottom);
		final LinearLayout ll_top_title = findViewById(R.id.ll_top_title);
		String fromStr = getIntent().getStringExtra("from");
		if(!TextUtils.isEmpty(fromStr)&&fromStr.equals("paigong")){
			tabSupport.chooseManagerTab();

		}if(!TextUtils.isEmpty(fromStr)&&fromStr.equals("select")){
			tabSupport.chooseHomeTab();

		}else {
			getSupportFragmentManager()    //
					.beginTransaction()
					.add(R.id.fragment_tab, new HomeZsxFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
					.commit();
		}
		final View rootView = getWindow().getDecorView();


		root_view.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener(){
					@Override
					public void onGlobalLayout()
					{


						rl_bottom.postDelayed(new Runnable() {
							@Override
							public void run() {


								final Rect rect = new Rect();
								rootView.getWindowVisibleDisplayFrame(rect);

								final int screenHeight = rootView.getRootView().getHeight();
								int heightDiff = screenHeight -  (rect.bottom - rect.top);
								if(heightDiff==0){
									return;
								}

								if (heightDiff > screenHeight/4)
								{ // 说明键盘是弹出状态
									Log.v(TAG, "键盘弹出状态");
									rl_bottom.setVisibility(View.GONE);
									ll_top_title.setVisibility(View.GONE);
								} else{
									Log.v(TAG, "键盘收起状态");
									rl_bottom.setVisibility(View.VISIBLE);
									ll_top_title.setVisibility(View.VISIBLE);


								}
							}
						},100);

					}
				});
		requestPermission();
	}



	public void setTittle(String title){
		navSupport.setTittle(title);
	}
	@Override
	public void onClick(View v) {
	}

	private void requestPermission(){
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
			}
		}
	}



	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	long mLastPress = 0;

	@Override
	public void onBackPressed() {
		long curTimes = System.currentTimeMillis();
		if(curTimes-mLastPress>2000){
			mLastPress = System.currentTimeMillis();
			Toast.makeText(this,"再按一次退出应用",Toast.LENGTH_SHORT).show();
		}else {
			super.onBackPressed();
		}

	}

	private void checkData(){
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("db", sp.getString(Constance.Data_Source_name));
		dataMap.put("function", "sp_fun_get_oprater_right");
		dataMap.put("operater_code", sp.getString(Constance.USERNAME));
		HttpClient client = new HttpClient();
		client.post(Util.getUrl(), dataMap, new IGetDataListener() {
			@Override
			public void onSuccess(String json) {
				try {
					if(!TextUtils.isEmpty(json)) {
						sp.putString(Constance.CHECKE_DATA, json);
					}
					//2020-05-24 01:23:54.191 2433-2590/com.shoujia.zhangshangxiu E/ checkData(): 222222{"state":"ok","data":[{"menu_right":"10600","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"10200","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"10500","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"90100","new":"1","del":"1","sh":"0","modify":"1","rz":"1","open":"1"},{"menu_right":"20200","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"70100","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"62700","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"21600","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"12000","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"11700","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"11000","new":"1","del":"1","sh":"1","modify":"1","rz":"1","open":"1"},{"menu_right":"71900","new":"0","del":"0","sh":"0","modify":"0","rz":"0","open":"0"}]}
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onFail() {
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		checkData();


	}
}
