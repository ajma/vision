package com.andrewma.vision.webserver.controllers;

import android.content.Context;

import com.andrewma.vision.database.DatabaseHelper;
import com.andrewma.vision.models.Glasses;
import com.andrewma.vision.webserver.core.Controller;
import com.andrewma.vision.webserver.core.annotations.Action;

public class GlassesController extends Controller {

	final DatabaseHelper dbHelper;
	
	public GlassesController(Context context) {
		super();
		dbHelper = DatabaseHelper.getInstance(context);
	}
	
	@Action
	public Object GetAll() {
		return dbHelper.getAll(Glasses.class);
	}
	
	@Action
	public Object Get(int id) {
		return dbHelper.get(Glasses.class, id);
	}
	
	@Action
	public Object Add(Glasses glasses) {
		dbHelper.insert(glasses);
		return true;
	}
}
