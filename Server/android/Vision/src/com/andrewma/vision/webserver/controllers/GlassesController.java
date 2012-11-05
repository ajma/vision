package com.andrewma.vision.webserver.controllers;

import android.content.Context;

import com.andrewma.vision.database.DatabaseHelper;
import com.andrewma.vision.models.Glasses;
import com.andrewma.vision.webserver.core.Controller;
import com.andrewma.vision.webserver.core.NanoHTTPD;
import com.andrewma.vision.webserver.core.VisionHTTPD;
import com.andrewma.vision.webserver.core.annotations.Action;

public class GlassesController extends Controller {

	final DatabaseHelper dbHelper;

	public GlassesController(Context context) {
		super();
		dbHelper = DatabaseHelper.getInstance(context);
	}
	
	@Action
	public Result Search(Glasses query) {
		return GetAll();
	}

	@Action
	public Result GetAll() {
		return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON,
				dbHelper.getAll(Glasses.class));
	}

	@Action
	public Result Get(int id) {
		return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON,
				dbHelper.get(Glasses.class, id));
	}

	@Action
	public Result Add(Glasses glasses) {
		dbHelper.insert(glasses);
		return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, true);
	}
}
