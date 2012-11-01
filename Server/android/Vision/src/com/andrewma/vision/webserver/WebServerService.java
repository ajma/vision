package com.andrewma.vision.webserver;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.andrewma.vision.activity.MainActivity;
import com.andrewma.vision.utils.NetworkUtils;
import com.andrewma.vision.webserver.core.VisionHTTPD;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WebServerService extends Service {

	public static final int WEBSERVER_PORT = 8765;

	private static final String TAG = "WebServerService";
	private static final int NOTIFICATION_ID = 1337;
	private static final String NOTIFICATION_TITLE = "Vision Web Server";

	private static final AtomicBoolean mWebServerRunning = new AtomicBoolean(
			false);

	private VisionHTTPD server;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Starting Web Server Service");

		try {
			server = new VisionHTTPD(this);
			mWebServerRunning.set(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		showNotification();

		return (START_NOT_STICKY);
	}

	private void showNotification() {
		final Notification note = new Notification(
				android.R.drawable.ic_dialog_info,
				"Starting Vision Web Server Service",
				System.currentTimeMillis());
		final Intent i = new Intent(this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

		note.setLatestEventInfo(this, NOTIFICATION_TITLE,
				WebServerService.getWebServerUrl(this), pi);
		note.flags |= Notification.FLAG_NO_CLEAR;

		startForeground(NOTIFICATION_ID, note);

	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "Stopping Web Server Service");
		if (server != null) {
			server.stop();
			mWebServerRunning.set(false);
		}
		super.onDestroy();
	}

	public static boolean isWebServerRunning() {
		return mWebServerRunning.get();
	}
	
	public static String getWebServerUrl(Context context) {
		return String.format("http://%s:%d",
				NetworkUtils.getWifiIpAddress(context),
				WEBSERVER_PORT);
	}
}
