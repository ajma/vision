package com.andrewma.vision.api;

import com.andrewma.vision.datastore.BatchesFile;
import com.andrewma.vision.datastore.GlassesFile;
import com.andrewma.vision.models.Batch;
import com.andrewma.vision.models.Glasses;
import com.andrewma.vision.models.ScoredGlasses;

import java.util.List;

public class JavascriptApi {

    private BatchesFile mBatchesFile = BatchesFile.getInstance();
    private GlassesFile mGlassesFile = GlassesFile.getInstance();

    public int newBatch() {
        return mBatchesFile.newBatch();
    }

    public Batch getBatch(int batchId) {
        return mBatchesFile.getBatch(batchId);
    }

    public List<ScoredGlasses> searchGlasses(Glasses search) {
        return null;
    }

    public List<Glasses> getAllGlasses() {
        return mGlassesFile.getAll();
    }

    public void addGlasses(Glasses glasses, int batchId) {
        mGlassesFile.addGlasses(glasses);
        mBatchesFile.addGlasses(batchId, glasses.GlassesId);
    }

    public void editGlasses(Glasses glasses)
    {
    }

    public void removeGlasses(int group, int number) {
    }

    public void undoRemove(int group, int number) {
    }
}
