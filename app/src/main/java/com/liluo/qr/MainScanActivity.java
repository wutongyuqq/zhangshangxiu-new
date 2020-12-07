package com.liluo.qr;

import com.shoujia.zhangshangxiu.R;
import com.zbar.lib.MipcaActivityCapture;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainScanActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scan);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toScan();
            }
        });
    }
    public void toScan()
    {
    	Intent i=new Intent(MainScanActivity.this,MipcaActivityCapture.class);
    	startActivity(i);
    }

}
