
package com.andrewma.vision.webserver;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.IBinder;
import android.util.Log;

import com.andrewma.vision.activity.MainActivity;
import com.andrewma.vision.utils.NetworkUtils;
import com.andrewma.vision.webserver.core.VisionHTTPD;

public class WebServerService extends Service {

    public static final int WEBSERVER_PORT = 8765;

    private static final String TAG = "WebServerService";
    private static final int NOTIFICATION_ID = 1337;
    private static final String NOTIFICATION_TITLE = "Vision Web Server";

    private static Intent webServerServiceIntent;

    private static final AtomicBoolean mWebServerRunning = new AtomicBoolean(false);

    private VisionHTTPD server;

    private static WebServerEvents webServerEvents;

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

            if (webServerEvents != null) {
                webServerEvents.onStart(NetworkUtils.getConnectInfo(getApplicationContext()));
            }
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

        ConnectInfo info = NetworkUtils.getConnectInfo(getApplicationContext());
        note.setLatestEventInfo(this, NOTIFICATION_TITLE, info.toString(), pi);
        note.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(NOTIFICATION_ID, note);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Stopping Web Server Service");
        if (server != null) {
            server.stop();
            mWebServerRunning.set(false);

            if (webServerEvents != null) {
                webServerEvents.onStop();
            }
        }
        super.onDestroy();
    }

    public static void start(Context context) {
        webServerServiceIntent = new Intent(context, WebServerService.class);
        context.startService(webServerServiceIntent);
    }

    public static void stop(Context context) {
        if (webServerServiceIntent != null) {
            context.stopService(webServerServiceIntent);
            webServerServiceIntent = null;
        }
    }

    public static void setEvents(WebServerEvents events) {
        webServerEvents = events;
    }

    public static boolean isWebServerRunning() {
        return mWebServerRunning.get();
    }
}
