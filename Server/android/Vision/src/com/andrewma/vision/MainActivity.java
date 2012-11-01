package com.andrewma.vision;

import com.andrewma.vision.utils.NetworkUtils;
import com.andrewma.vision.webserver.WebServerService;

import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	private TextView mServerIpAddress;
	private Button mStartStopServerButton;
	private Intent mWebServerService;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mWebServerService = new Intent(this, WebServerService.class);
        
        setContentView(R.layout.activity_main);
        
        mServerIpAddress = (TextView)findViewById(R.id.serverIpAddress);
        mStartStopServerButton = (Button)findViewById(R.id.startStopServerButton);
        
        mServerIpAddress.setText(NetworkUtils.getWifiIpAddress(this));
        mStartStopServerButton.setText(WebServerService.isWebServerRunning() ? R.string.stopServer : R.string.startServer);
    }
    
    public void startStopServerButtonClick(View v) {
    	if(!WebServerService.isWebServerRunning()) {
    		startService(mWebServerService);
    		mStartStopServerButton.setText(R.string.stopServer);
    	} else {
    		stopService(mWebServerService);
    		mStartStopServerButton.setText(R.string.startServer);
    	}
    }
}
