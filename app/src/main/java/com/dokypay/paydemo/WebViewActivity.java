package com.dokypay.paydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.dokypay.paydemo.util.AppUtil;

import static android.webkit.WebSettings.LOAD_DEFAULT;

/**
 * if user's phone has not installed Paytm App
 * then open this activity, and post these parameters to paytm api url
 */
public class WebViewActivity extends AppCompatActivity {
    private final String URL_PAYTM_TEST = "https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage";
    private final String URL_PAYTM_PRODUCTION = "https://securegw.paytm.in/theia/api/v1/showPaymentPage";
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();

        String mid = null;
        String orderid = null;
        String postUrl = null;
        StringBuffer postData = new StringBuffer(128);
        try{
            //get parameters from intent
            Bundle bundle = getIntent().getBundleExtra("bill");
            mid = bundle.getString("mid");
            orderid = bundle.getString("orderid");
            //append parameters like form submit
            postData.append("MID=")
              .append(mid)
              .append("&txnToken=")
              .append(bundle.getString("txnToken"))
              .append("&ORDER_ID=").append(orderid);
            if (bundle.getInt("mode") == 1) {
                postUrl = URL_PAYTM_PRODUCTION;
            }else {
                postUrl = URL_PAYTM_TEST;
            }
            //post to paytm transaction api
            //we will notify the notifyUrl(Async) when the transaction is success
            //and redirect to returnUrl(sync) when the transaction is complete(success/failure/cancel)
            webView.postUrl(postUrl + "?mid=" + mid + "&orderId=" + orderid,
                    postData.toString().getBytes());
        }catch (Exception e) {
            StringBuffer error = new StringBuffer(1024);
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                error.append(stackTraceElement.toString()).append("\n");
            }
            AppUtil.toast(this, "throws an exception\n" + e.getClass().toString() + "\n" + error.toString());
        }


    }

    private void initView() {
        webView = findViewById(R.id.webView);
        initWebViewConfig(webView);
    }


    /**
     * set back button can back to last webpage
     */
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        }else {
            AppUtil.toast(this,"press back to close webpage and cancel this payment");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        destroy(webView);
        super.onDestroy();
    }

    public static void initWebViewConfig(WebView webview) {
        webview.setInitialScale(100);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setUseWideViewPort(true);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        webSettings.setCacheMode(LOAD_DEFAULT);
        //merchant can notice app transaction status with javascrpitInterface when the returnUrlPage(sync) is showing
//        webview.addJavascriptInterface(new Object(){
//            @JavascriptInterface
//            public void close(int code, String msg){
//                Context context = getContext();
//                if (context instanceof Activity) {
//                    ((Activity) context).finish();
//                }else {
//                    AppUtil.toast(getContext(), "server invoke the method close");
//                }
//            }
//        },"dokypay");
    }

    /**
     * destroy webview
     * @param webview the webview that need destroy
     */
    public static void destroy(WebView webview) {
        webview.stopLoading();
        ((ViewGroup) webview.getParent()).removeView(webview);
        webview.removeAllViews();
        webview.clearCache(true);
        webview.clearHistory();
        webview.destroy();
    }

    public static Context getContext(){
        return getContext();
    }

}
