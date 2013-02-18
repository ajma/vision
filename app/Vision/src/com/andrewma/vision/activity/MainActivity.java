
package com.andrewma.vision.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.andrewma.vision.Preferences;
import com.andrewma.vision.R;
import com.andrewma.vision.database.DatabaseHelper;
import com.andrewma.vision.utils.WifiApManager;
import com.andrewma.vision.webserver.WebServerEvents;
import com.andrewma.vision.webserver.WebServerService;
import com.andrewma.vision.webserver.ConnectionInfo;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";

    private TextView serverStatusTextView;
    private TextView urlTextView;
    private TextView ssidTextView;
    private Button startStopServerButton;
    private Button webviewButton;
    private CheckBox autoStartServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverStatusTextView = (TextView) findViewById(R.id.serverStatus);
        ssidTextView = (TextView) findViewById(R.id.ssid);
        urlTextView = (TextView) findViewById(R.id.url);
        startStopServerButton = (Button) findViewById(R.id.startStopServerButton);
        webviewButton = (Button) findViewById(R.id.webviewButton);
        autoStartServer = (CheckBox) findViewById(R.id.autoStartServer);

        autoStartServer.setChecked(Preferences.getAutoStart());
        autoStartServer
                .setOnCheckedChangeListener(autoStartServerChangeListener);
        startStopServerButton.setOnClickListener(startStopClickListener);
        webviewButton.setOnClickListener(openWebViewListener);

        WebServerService.setEvents(new WebServerEvents() {
            @Override
            public void onStop() {
                setWebServerStatus(false, null);
            }

            @Override
            public void onStart(ConnectionInfo info) {
                setWebServerStatus(true, info);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_clear_database:
                deleteDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setWebServerStatus(boolean isRunning, ConnectionInfo info) {
        webviewButton.setEnabled(isRunning);

        final int buttonTextId = (isRunning ? R.string.stop_server : R.string.start_server);
        startStopServerButton.setText(buttonTextId);

        if (isRunning && info != null) {
            ssidTextView.setVisibility(View.VISIBLE);
            ssidTextView.setText("Wi-fi SSID: " + info.SSID);
            urlTextView.setText(info.getUrl());
        } else {
            ssidTextView.setVisibility(View.GONE);
            urlTextView.setText(R.string.server_na);
        }

        final int statusId = (isRunning ? R.string.server_status_running
                : R.string.server_status_stopped);
        serverStatusTextView.setText(statusId);
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
                WebServerService.start(getApplicationContext());
            } else {
                WebServerService.stop(getApplicationContext());
            }
        }
    };

    private final OnCheckedChangeListener autoStartServerChangeListener = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            Preferences.setAutoStart(isChecked);
        }
    };

    private void deleteDatabase() {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DatabaseHelper.deleteDatabase(MainActivity.this);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete the database?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }
}
