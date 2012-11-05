package com.andrewma.vision.activity;

import com.andrewma.vision.R;
import com.andrewma.vision.webserver.WebServerService;

import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	private TextView serverIpAddressTextView;
	private Button startStopServerButton;
	private Button webviewButton;
	private Intent webServerServiceIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		webServerServiceIntent = new Intent(this, WebServerService.class);

		setContentView(R.layout.activity_main);

		serverIpAddressTextView = (TextView) findViewById(R.id.serverIpAddress);
		startStopServerButton = (Button) findViewById(R.id.startStopServerButton);
		webviewButton = (Button) findViewById(R.id.webviewButton);

		setWebServerStatus(WebServerService.isWebServerRunning());
	}

	public void startStopServerButtonClick(View v) {
		if (!WebServerService.isWebServerRunning()) {
			startService(webServerServiceIntent);
			setWebServerStatus(true);
		} else {
			stopService(webServerServiceIntent);
			setWebServerStatus(false);
		}
	}

	private void setWebServerStatus(boolean enabled) {
		webviewButton.setEnabled(enabled);
		startStopServerButton.setText(enabled ? R.string.stopServer
				: R.string.startServer);
		serverIpAddressTextView.setText(enabled ? WebServerService
				.getWebServerUrl(this) : "");
	}

	public void webviewButtonClick(View v) {
		startActivity(new Intent(this, WebViewActivity.class));
	}
}
