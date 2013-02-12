
package com.andrewma.vision.activity;

import com.andrewma.vision.R;
import com.andrewma.vision.webserver.WebServerService;

import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.annotation.SuppressLint;
import android.app.Activity;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends Activity {

    private WebView webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);

        webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl(WebServerService.getWebServerUrl(this));
        webview.getSettings().setJavaScriptEnabled(true);
    }
}
