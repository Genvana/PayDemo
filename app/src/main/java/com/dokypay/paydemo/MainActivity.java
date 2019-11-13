package com.dokypay.paydemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.dokypay.paydemo.util.AppUtil;

public class MainActivity extends AppCompatActivity {

    private static final String PACKAGE_NAME_PAYTM = "net.one97.paytm";
    private final int REQUEST_CODE_PAYTM = 111;
    private EditText etParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        etParams = findViewById(R.id.et_params);
    }

    /**
     * onClick method of start payment button
     * @param v button view object
     */
    public void startPay(View v){
        //type of payment environment:1--product,2--test
        int mode;
        if (v.getId() == R.id.bt_product){
           mode = 1;
           AppUtil.toast(this,"start product payment");
        }else{
            mode = 2;
            AppUtil.toast(this,"start test payment");
        }
        //payment parameters
        JSONObject params = getJSONParams();
        if (params == null) {
            AppUtil.toast(this,"something wrong with params,please check it and submit again~");
        }else {

            //init intent
            Intent paytmIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putDouble("nativeSdkForMerchantAmount", params.getDoubleValue("amount"));
            bundle.putString("orderid", params.getString("orderid"));
            bundle.putString("txnToken", params.getString("txnToken"));
            bundle.putString("mid", params.getString("mid"));
            //the "mode" parameter is not from transaction api,and this parameter is not mandatory
            bundle.putInt("mode", mode);
            paytmIntent.putExtra("bill", bundle);
            //catch ActivityNotFoundException when start paytm activity fail
            try{
                //start paytm activity
                paytmIntent.setComponent(new ComponentName(PACKAGE_NAME_PAYTM,
                        "net.one97.paytm.AJRJarvisSplash"));
                paytmIntent.putExtra("paymentmode", 2);
                startActivityForResult(paytmIntent, REQUEST_CODE_PAYTM);
            }catch(ActivityNotFoundException e) {
                //start webview activity
                AppUtil.toast(this,"start paytm fail, open webview");
                paytmIntent.setComponent(new ComponentName(this, WebViewActivity.class));
                startActivity(paytmIntent);
            }

        }

    }

    public void startUpiPay(View v) {
        JSONObject params = getJSONParams();
        if (params == null) {
            AppUtil.toast(this,"something wrong with params,please check it and submit again~");
        }else {
            Object deepLink = params.get("deepLink");
            if (deepLink != null && !"".equals(deepLink.toString().trim())) {
                try{
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(deepLink.toString()));
                    startActivity(intent);
                }catch (Exception e) {
                    AppUtil.toast(this, "error occured when start upi deepLink");
                }
            }
        }
    }

    /**
     * receive the result of paytm activity
     * @param requestCode merchant defined code
     * @param resultCode paytm activity response:0--cancel transaction,1--success/fail,2--invalid input parameters
     * @param data response message,include "response" and "message"
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_PAYTM && data != null) {
            AppUtil.toast(this,"PayTmCallback:" + data.getStringExtra("response"));
        }
    }

    /**
     * get json parameters from EditText
     * @return payment parameters jsonobject
     */
    private JSONObject getJSONParams(){
        try{
            return JSON.parseObject(etParams.getText().toString());
        }catch (Exception e){
            return null;
        }
    }





}
