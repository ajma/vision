package com.andrewma.vision.webserver.core;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.andrewma.vision.database.DatabaseHelper;
import com.andrewma.vision.webserver.WebServerService;

public class VisionHTTPD extends NanoHTTPD {

	private static final String TAG = "VisionHTTPD";
	private static final String MIME_JSON = "application/json";
	private final Response notFoundResponse = new Response(HTTP_NOTFOUND,
			MIME_HTML,
			"<html><head><head><body><h1>404 Not Found</h1></body></html>");

	private final AssetManager assets;
	private DatabaseHelper dbHelper;
	private final MimeTypeMap mimeTypeMap;

	public VisionHTTPD(Context c) throws IOException {
		super(WebServerService.WEBSERVER_PORT, null);
		assets = c.getApplicationContext().getResources().getAssets();
		mimeTypeMap = MimeTypeMap.getSingleton();
		dbHelper = DatabaseHelper.getInstance(c);
	}

	@Override
	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {
		Log.v(TAG, "Requesting " + uri);

		if (uri.toLowerCase().startsWith("/api/")) {
			return serveApi(uri);
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

	private Response serveApi(String uri) {
		Log.v(TAG, "Serving from API: " + uri);
		final Scanner scanner = new Scanner(uri).useDelimiter("/");

		// skip over the /api part of the url
		if (scanner.hasNext()) {
			scanner.next();
		} else {
			return notFoundResponse;
		}

		final String model = (scanner.hasNext() ? scanner.next() : null);
		final String method = (scanner.hasNext() ? scanner.next().toLowerCase()
				: null);
		final String idString = (scanner.hasNext() ? scanner.next() : null);
		int id = 0;
		if(idString != null) {
			try {
				id = Integer.parseInt(idString);
			} catch (NumberFormatException nfe) {
				return notFoundResponse;
			}
		}

		if ("getall".equals(method)) {
			final List<?> getAll = dbHelper.getAll(model);
			return new Response(HTTP_OK, MIME_JSON, "count: " + getAll.size());
		} else if ("get".equals(method)) {
			final Object getAll = dbHelper.get(model, id);
			return new Response(HTTP_OK, MIME_JSON, getAll.toString());
		} else {
			return notFoundResponse;
		}
	}
}
