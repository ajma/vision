
package com.andrewma.vision.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.andrewma.vision.database.core.DbDataType;
import com.andrewma.vision.database.core.annotations.Column;
import com.andrewma.vision.database.core.annotations.PrimaryKey;
import com.andrewma.vision.database.core.annotations.Table;

/**
 * When glasses are added to the inventory in batches, this is used to keep
 * track of which glasses were added into which batch
 */
public class Batch {

    public int batchId;

    /**
     * Name of person working on this batch
     */
    public String name = "";

    /**
     * UTC Date that the batch was created on
     */
    public Date createdDate = new Date();

    /**
     * List of {@link Glasses#GlassesId} that are in this batch
     */
    public List<Integer> glassesIds = new ArrayList<Integer>();

    /**
     * When labels are printed, user can mark the batch as printed
     */
    public boolean printed = false;
}
