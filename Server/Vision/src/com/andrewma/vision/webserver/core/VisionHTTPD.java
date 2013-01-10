package com.andrewma.vision.webserver.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.andrewma.vision.webserver.WebServerService;
import com.andrewma.vision.webserver.controllers.GlassesController;
import com.google.gson.Gson;

public class VisionHTTPD extends NanoHTTPD {

	private static final String TAG = "VisionHTTPD";
	public static final String MIME_JSON = "application/json";
	private final Response notFoundResponse = new Response(HTTP_NOTFOUND,
			MIME_HTML,
			"<html><head><head><body><h1>404 Not Found</h1></body></html>");

	private final AssetManager assets;
	private final MimeTypeMap mimeTypeMap;
	private final Gson gson = new Gson();

	private final Map<String, Controller> controllers = new HashMap<String, Controller>();

	public VisionHTTPD(Context c) throws IOException {
		super(WebServerService.WEBSERVER_PORT, null);
		assets = c.getApplicationContext().getResources().getAssets();
		mimeTypeMap = MimeTypeMap.getSingleton();
		controllers.put("glasses", new GlassesController(c));
	}

	@Override
	public Response serve(String uri, String method, Properties header,
			Properties params, Properties files) {
		Log.v(TAG, "Requesting " + uri);

		if (uri.toLowerCase().startsWith("/api/")) {
			return serveApi(uri, method, header, params);
		} else {
			return serveAsset(uri);
		}
	}

	private Response serveAsset(String uri) {
		try {
			String truncatedUri = uri.substring(1);
			if (truncatedUri.length() == 0) {
				truncatedUri = "index.html";
			}
			final String mimeType = getMimeTypeFromUrl(truncatedUri);
			final Response assetResponse = new Response(HTTP_OK, mimeType,
					assets.open(truncatedUri));
			Log.v(TAG, String.format(
					"Serving from assets (mime type: %s): /%s", mimeType,
					truncatedUri));
			return assetResponse;
		} catch (IOException e) {
			if (uri == "" || uri.endsWith("/")) {
				return serveAsset(uri + "index.html");
			} else {
				Log.e(TAG, "File Not Found: " + uri);
				return notFoundResponse;
			}
		}
	}

	private String getMimeTypeFromUrl(String url) {
		final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		return mimeTypeMap.getMimeTypeFromExtension(extension);
	}

	private Response serveApi(String uri, String method, Properties header,
			Properties params) {
		Log.v(TAG, "Serving from API: " + uri);
		final Scanner scanner = new Scanner(uri).useDelimiter("/");

		// skip over the /api part of the url
		if (scanner.hasNext()) {
			scanner.next();
		} else {
			return notFoundResponse;
		}

		final String controllerUrl = (scanner.hasNext() ? scanner.next()
				.toLowerCase() : null);
		final String actionUrl = (scanner.hasNext() ? scanner.next()
				.toLowerCase() : null);
		final String idUrl = (scanner.hasNext() ? scanner.next() : null);

		Log.v(TAG, String.format("Model: %s Action: %s Id: %s", controllerUrl,
				actionUrl, idUrl));

		if (!controllers.containsKey(controllerUrl)) {
			return notFoundResponse;
		}

		final Controller controller = controllers.get(controllerUrl);
		final Object obj = controller.execute(actionUrl, idUrl, params);
		return new Response(HTTP_OK, MIME_JSON, gson.toJson(obj));
	}
}