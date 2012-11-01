package com.andrewma.vision.activity;

import com.andrewma.vision.R;
import com.andrewma.vision.webserver.WebServerService;

import android.os.Bundle;
import android.webkit.WebView;
import android.app.Activity;

public class WebViewActivity extends Activity {

	private WebView webview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_webview);
		
		webview = (WebView)findViewById(R.id.webview);
		webview.loadUrl(WebServerService.getWebServerUrl(this));
		webview.getSettings().setJavaScriptEnabled(true);
	}
}
