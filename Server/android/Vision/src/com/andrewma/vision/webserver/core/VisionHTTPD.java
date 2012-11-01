package com.andrewma.vision.webserver.core;

import java.io.IOException;
import java.util.Properties;

import android.util.Log;

import com.andrewma.vision.webserver.WebServerService;

public class VisionHTTPD extends NanoHTTPD {
	
	private static final String TAG = "VisionHTTPD";
	
	public VisionHTTPD() throws IOException {
		super(WebServerService.WEBSERVER_PORT, null);
	}

	@Override
	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {
		Log.v(TAG, "Requested " + uri);
		final String html = "<html><head><head><body><h1>Hello, World !!!</h1></body></html>";
		return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, html);
	}
}
