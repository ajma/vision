
package com.andrewma.vision.webserver;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.andrewma.vision.utils.WifiApManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Information needed for the user to connect to the web server
 * 
 * @author ajma
 */
public class ConnectionInfo {

    public ConnectionInfo(String ssid, String ipAddress) {
        SSID = ssid;
        IpAddress = ipAddress;
    }

    public String SSID;
    public String IpAddress;

    /**
     * Uses the {@link #IpAddress} and {@link WebServerService#WEBSERVER_PORT}
     * to create a URL
     * 
     * @return
     */
    public String getUrl() {
        return String.format("http://%s:%d", IpAddress, WebServerService.WEBSERVER_PORT);
    }

    @Override
    public String toString() {
        return "SSID: " + SSID + " " + getUrl();
    }

    /**
     * Get the current {@link ConnectionInfo} for this device
     * 
     * @param c
     * @return
     */
    public static ConnectionInfo getCurrent(Context c) {
        final WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            String ssid = wifiInfo.getSSID();
            ssid = (ssid != null ? ssid.replace("\"", "") : "");

            String ipAddress = intToIp(wifiInfo.getIpAddress());

            return new ConnectionInfo(ssid, ipAddress);
        } else {
            final WifiApManager wifiApManager = new WifiApManager(wifiManager);
            if (wifiApManager.isWifiApEnabled()) {
                WifiConfiguration wifiApConfig = wifiApManager.getWifiApConfiguration();
                return new ConnectionInfo(wifiApConfig.SSID, wifiApManager.getWifiApIpAddress());
            } else {
                // Wifi is not enabled and Wifi access point does not appear to
                // be enabled. There is no connection info available
                return null;
            }
        }
    }

    private static String intToIp(int i) {
        return ((i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF));
    }
}
