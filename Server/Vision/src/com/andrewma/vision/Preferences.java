
package com.andrewma.vision;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    public static final String SHARED_PREFERENCES_NAME = "com.andrewma.vision";
    public static final String SERVER_AUTOSTART = "SERVER_AUTOSTART";

    private static SharedPreferences sharedPrefs;

    private Preferences() {
    }

    public static void init(Context context) {
        sharedPrefs = context.getSharedPreferences(Preferences.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
    }

    public static boolean getAutoStart() {
        return sharedPrefs.getBoolean(Preferences.SERVER_AUTOSTART, false);
    }

    public static void setAutoStart(boolean autoStart) {
        sharedPrefs
                .edit()
                .putBoolean(SERVER_AUTOSTART, autoStart)
                .commit();
    }
}
