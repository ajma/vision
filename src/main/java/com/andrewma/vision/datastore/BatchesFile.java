package com.andrewma.vision.datastore;

import android.util.SparseArray;

import com.andrewma.vision.models.Batch;

public class BatchesFile {

    private static final BatchesFile instance = new BatchesFile();

    public static BatchesFile getInstance() {
        return instance;
    }

    private final SparseArray<Batch> mBatches = new SparseArray<Batch>();
    private int mNextBatchId = 1;

    protected BatchesFile() { }

    public int newBatch() {
        // create a new batch and save
        final Batch newBatch = new Batch();
        newBatch.batchId = mNextBatchId;
        mBatches.append(newBatch.batchId, newBatch);
        save();

        // iterate the next batch Id
        mNextBatchId++;

        return newBatch.batchId;
    }

    public Batch getBatch(int batchId) {
        for(int i = 0; i < mBatches.size(); i++) {
            final int key = mBatches.keyAt(i);
            if(batchId == key) {
                return mBatches.get(key);
            }
        }

        return null;
    }

    public void addGlasses(int batchId, int glassesId) {
        final Batch batch = mBatches.get(batchId);
        batch.glassesIds.add(glassesId);
    }

    /**
     * Save the current in-memory data to a file in a background thread
     */
    private synchronized void save() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO
            }
        }).run();
    }
}
