package com.andrewma.vision.activity;

import com.andrewma.vision.R;
import com.andrewma.vision.utils.SharedPreferenceKeys;
import com.andrewma.vision.webserver.WebServerService;

import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainActivity extends Activity {

	private TextView serverIpAddressTextView;
	private Button startStopServerButton;
	private Button webviewButton;
	private Intent webServerServiceIntent;
	private CheckBox autoStartServer;
	private SharedPreferences sharedPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		webServerServiceIntent = new Intent(this, WebServerService.class);
		sharedPrefs = getSharedPreferences(
				SharedPreferenceKeys.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

		serverIpAddressTextView = (TextView) findViewById(R.id.serverIpAddress);

		startStopServerButton = (Button) findViewById(R.id.startStopServerButton);
		startStopServerButton.setOnClickListener(startStopClickListener);

		webviewButton = (Button) findViewById(R.id.webviewButton);
		webviewButton.setOnClickListener(openWebViewListener);

		autoStartServer = (CheckBox) findViewById(R.id.autoStartServer);
		autoStartServer.setChecked(sharedPrefs.getBoolean(
				SharedPreferenceKeys.SERVER_AUTOSTART, false));
		autoStartServer
				.setOnCheckedChangeListener(autoStartServerChangeListener);
		
		if(WebServerService.isWebServerRunning()) {
			setWebServerStatus(true); 
		} else if (autoStartServer.isChecked()) {
			startService(webServerServiceIntent);
			setWebServerStatus(true);
		}
	}

	private void setWebServerStatus(boolean enabled) {
		webviewButton.setEnabled(enabled);
		startStopServerButton.setText(enabled ? R.string.stopServer
				: R.string.startServer);
		serverIpAddressTextView.setText(enabled ? WebServerService
				.getWebServerUrl(this) : "");
	}

	private final OnClickListener openWebViewListener = new OnClickListener() {
		public void onClick(View v) {
			startActivity(new Intent(getApplicationContext(),
					WebViewActivity.class));
		}
	};

	private final OnClickListener startStopClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (!WebServerService.isWebServerRunning()) {
				startService(webServerServiceIntent);
				setWebServerStatus(true);
			} else {
				stopService(webServerServiceIntent);
				setWebServerStatus(false);
			}
		}
	};

	private final OnCheckedChangeListener autoStartServerChangeListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			sharedPrefs
					.edit()
					.putBoolean(SharedPreferenceKeys.SERVER_AUTOSTART,
							isChecked).commit();
		}
	};
}
