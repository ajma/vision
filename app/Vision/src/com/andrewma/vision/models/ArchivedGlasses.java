package com.andrewma.vision.models;

import com.andrewma.vision.database.core.DbDataType;
import com.andrewma.vision.database.core.annotations.Column;
import com.andrewma.vision.database.core.annotations.Table;

@Table
public class ArchivedGlasses extends Glasses {
    
    public ArchivedGlasses(Glasses glasses) {
        copyFrom(glasses);
    }
    
    /** Epoch Time (seconds since 1/1/1970 */
    @Column(dataType = DbDataType.INTEGER)
    public long RemovedEpochTime;
    @Column(dataType = DbDataType.TEXT, nullable = true)
    public String RemovedReason;
    @Column(dataType = DbDataType.TEXT, nullable = true)
    public String RemovedBy;
}
