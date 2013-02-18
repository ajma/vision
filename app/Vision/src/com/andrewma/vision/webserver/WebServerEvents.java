
package com.andrewma.vision.webserver;

public interface WebServerEvents {

    public void onStart(ConnectionInfo info);

    public void onStop();
}
