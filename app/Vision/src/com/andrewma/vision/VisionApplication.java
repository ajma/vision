
package com.andrewma.vision;

import android.app.Application;

import com.andrewma.vision.webserver.WebServerService;

public class VisionApplication extends Application {

    @Override
    public void onCreate() {
        Preferences.init(getApplicationContext());

        if (Preferences.getAutoStart()) {
            WebServerService.start(getApplicationContext());
        }
    }
}
