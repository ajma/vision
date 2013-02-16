
package com.andrewma.vision.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.andrewma.vision.webserver.ConnectInfo;
import com.andrewma.vision.webserver.WebServerService;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtils {
    
    public static ConnectInfo getConnectInfo(Context context) {
        final WifiApManager wifiApManager = new WifiApManager(context);
        if (wifiApManager.isWifiApEnabled()) {
            WifiConfiguration wifiApConfig = wifiApManager.getWifiApConfiguration();
            return new ConnectInfo(wifiApConfig.SSID, getUrlAddress());
        } else {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID().replace("\"", "");
            ssid = (ssid != null ? ssid.replace("\"", "") : "");
            return new ConnectInfo(ssid, getUrlAddress());
        }
    }

    private static String getUrlAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && (inetAddress.getAddress().length == 4)) {
                            Log.d("IPs", inetAddress.getHostAddress());
                            return String.format("http://%s:%d",
                                    inetAddress.getHostAddress(), WebServerService.WEBSERVER_PORT);
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("NetworkUtils", ex.toString());
        }
        return null;
    }

    private static WifiConfiguration getWifiAPConfig(Context context) {
        WifiApManager wifiApManager = new WifiApManager(context);
        if (!wifiApManager.isWifiApEnabled()) {
            return null;
        } else {
            return wifiApManager.getWifiApConfiguration();
        }
    }
}
