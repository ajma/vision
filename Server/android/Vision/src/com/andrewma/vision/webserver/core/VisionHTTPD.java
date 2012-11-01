package com.andrewma.vision.webserver.core;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.andrewma.vision.webserver.WebServerService;

public class VisionHTTPD extends NanoHTTPD {
	
	private static final String TAG = "VisionHTTPD";
	private final AssetManager assets;
	private final MimeTypeMap mimeTypeMap;
	
	public VisionHTTPD(Context c) throws IOException {
		super(WebServerService.WEBSERVER_PORT, null);
		assets = c.getApplicationContext().getResources().getAssets();
		mimeTypeMap = MimeTypeMap.getSingleton();
	}

	@Override
	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {
		Log.v(TAG, "Request " + uri);
		
		if(uri.toLowerCase().startsWith("/api/")) {
			return serveApi(uri);
		} else {
			return serveAsset(uri);
		}
	}
	
	private Response serveAsset(String uri) {
		try {
			String truncatedUri = uri.substring(1);
			if(truncatedUri.length() == 0) {
				truncatedUri = "index.html";
			}
			final String mimeType = getMimeTypeFromUrl(truncatedUri);
			final Response assetResponse = new Response(HTTP_OK, mimeType, assets.open(truncatedUri)); 
			Log.v(TAG, String.format("Serving from assets (mime type: %s): /%s", mimeType, truncatedUri));
			return assetResponse;
		} catch (IOException e) {
			if(uri == "" || uri.endsWith("/")) {
				return serveAsset(uri + "index.html");
			} else {
				Log.e(TAG, "File Not Found: " + uri);
				return new Response(HTTP_NOTFOUND, MIME_HTML, "<html><head><head><body><h1>404 Not Found</h1></body></html>");
			}
		}
	}
	
	private String getMimeTypeFromUrl(String url) {
		final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		return mimeTypeMap.getMimeTypeFromExtension(extension);
	}
	
	private Response serveApi(String uri) {
		Log.v(TAG, "Serving from API: " + uri);
		final String html = "<html><head><head><body><h1>API request placeholder</h1></body></html>";
		return new Response(HTTP_OK, MIME_HTML, html);
	}
}
