
package com.andrewma.vision.utils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Wifi AP Manager Portions from:
 * http://www.whitebyte.info/android/android-wifi-hotspot-manager-class
 */
public class WifiApManager {

    private static final String TAG = "WifiApManager";

    public enum WifiApState {
        WIFI_AP_STATE_DISABLING,
        WIFI_AP_STATE_DISABLED,
        WIFI_AP_STATE_ENABLING,
        WIFI_AP_STATE_ENABLED,
        WIFI_AP_STATE_FAILED
    }

    private final WifiManager mWifiManager;

    public WifiApManager(WifiManager wifiManager) {
        mWifiManager = wifiManager;
    }

    /**
     * Gets the Wi-Fi enabled state.
     * 
     * @return {@link WifiApState}
     * @see #isWifiApEnabled()
     */
    public WifiApState getWifiApState() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer) method.invoke(mWifiManager));

            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }

            return WifiApState.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return WifiApState.WIFI_AP_STATE_FAILED;
        }
    }

    /**
     * Return whether Wi-Fi AP is enabled or disabled.
     * 
     * @return {@code true} if Wi-Fi AP is enabled
     * @see #getWifiApState()
     * @hide Dont open yet
     */
    public boolean isWifiApEnabled() {
        return getWifiApState() == WifiApState.WIFI_AP_STATE_ENABLED;
    }

    /**
     * Gets the Wi-Fi AP Configuration.
     * 
     * @return AP details in {@link WifiConfiguration}
     */
    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return null;
        }
    }
    
    public WifiInfo getWifiApinfo() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApInfo");
            return (WifiInfo) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return null;
        }  
    }

    public String getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            Log.d(TAG, inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }
}
