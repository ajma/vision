package com.andrewma.vision.webserver.controllers;

import java.util.Date;

import android.content.Context;

import com.andrewma.vision.database.DatabaseHelper;
import com.andrewma.vision.models.Batch;
import com.andrewma.vision.webserver.core.Controller;
import com.andrewma.vision.webserver.core.NanoHTTPD;
import com.andrewma.vision.webserver.core.VisionHTTPD;
import com.andrewma.vision.webserver.core.annotations.Action;

public class BatchesController extends Controller {

	final DatabaseHelper dbHelper;

	public BatchesController(Context context) {
		super();
		dbHelper = DatabaseHelper.getInstance(context);
	}

	@Action
	public Result Get(int id) {
		return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, 
				dbHelper.get(Batch.class, id));
	}

	@Action
	public Result New() {
		final Batch newBatch = new Batch();
		newBatch.CreatedDate = new Date();
		
		final long batchId = dbHelper.insert(newBatch);
		
		if(batchId == -1) {
			return ErrorResult("Could not create a new batch. batchId returned was -1.");
		}
		
		return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, batchId);
	}
	
	@Action
	public Result AddGlasses(Batch appendBatch) {
		dbHelper.executeSql(Batch.class,
				"UPDATE Batches SET Glasses = Glasses || \""
						+ appendBatch.Glasses + " \" WHERE BatchId = "
						+ appendBatch.BatchId);
		return Result(NanoHTTPD.HTTP_OK, VisionHTTPD.MIME_JSON, true);
	}
}
