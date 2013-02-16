package com.andrewma.vision.webserver;

/**
 * Information needed for the user to connect to the web server
 * 
 * @author ajma
 *
 */
public class ConnectInfo {
    
    public ConnectInfo(String ssid, String url) {
        SSID = ssid;
        Url = url;
    }
    
    public String SSID;
    public String Url;
    
    @Override
    public String toString() {
        return "SSID: " + SSID + " " + Url;
    }
}
