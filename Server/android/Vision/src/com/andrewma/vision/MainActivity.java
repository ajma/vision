package com.andrewma.vision;

import com.andrewma.vision.webserver.WebServerService;

import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	private boolean mServerStarted = false;
	
	private Button mStartStopServerButton;
	private Intent mWebServerService;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mWebServerService = new Intent(this, WebServerService.class);
        
        setContentView(R.layout.activity_main);
        
        mStartStopServerButton = (Button)findViewById(R.id.startStopServerButton);
    }
    
    public void startStopServerButtonClick(View v) {
    	if(!mServerStarted) {
    		startService(mWebServerService);
    	} else {
    		stopService(mWebServerService);
    	}
    	
    	mServerStarted = !mServerStarted;
    	mStartStopServerButton.setText(mServerStarted ? R.string.stopServer : R.string.startServer);
    }
}
