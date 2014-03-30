package com.andrewma.vision.datastore;

import android.test.InstrumentationTestCase;

import com.andrewma.vision.models.Batch;


public class BatchesFileTests extends InstrumentationTestCase {

    public void testFirstNewBatch() throws Exception {
        final BatchesFile batchesFile = new BatchesFile();

        final int expected = 1;
        final int batchId = batchesFile.newBatch();
        assertEquals("Ensure newBatch returns the right id", expected, batchId);

        final Batch batch = batchesFile.getBatch(1);
        assertEquals("Ensure getBatch shows the batch was actually added", expected, batch.batchId);
    }

    public void testSecondNewBatch() throws Exception {
        final BatchesFile batchesFile = new BatchesFile();

        final int expected = 2;
        batchesFile.newBatch();
        final int batchId = batchesFile.newBatch();
        assertEquals("Ensure newBatch returns the right id", expected, batchId);

        final Batch batch = batchesFile.getBatch(2);
        assertEquals("Ensure getBatch shows the batch was actually added", expected, batch.batchId);
    }

    public void testThirdNewBatch() throws Exception {
        final BatchesFile batchesFile = new BatchesFile();

        final int expected = 3;
        batchesFile.newBatch();
        batchesFile.newBatch();
        final int batchId = batchesFile.newBatch();
        assertEquals("Ensure newBatch returns the right id", expected, batchId);

        final Batch batch = batchesFile.getBatch(3);
        assertEquals("Ensure getBatch shows the batch was actually added", expected, batch.batchId);
    }

    public void testAddGlassesToBatch() throws Exception {
        final BatchesFile batchesFile = new BatchesFile();

        final int batchId = batchesFile.newBatch();
        final int glassesId = 5;
        batchesFile.addGlasses(batchId, glassesId);

        final Batch batch = batchesFile.getBatch(batchId);
        assertEquals("Ensure glasses was added to batch", 1, batch.glassesIds.size());

        batchesFile.addGlasses(batchId, glassesId + 1);
        assertEquals("Ensure glasses was added to batch (2)", 2, batch.glassesIds.size());
    }
}
