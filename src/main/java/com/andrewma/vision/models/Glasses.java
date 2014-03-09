
package com.andrewma.vision.models;

import com.andrewma.vision.database.core.DbDataType;
import com.andrewma.vision.database.core.annotations.Column;
import com.andrewma.vision.database.core.annotations.PrimaryKey;
import com.andrewma.vision.database.core.annotations.Table;

@Table
public class Glasses {
    @PrimaryKey
    public int GlassesId;

    @Column(dataType = DbDataType.INTEGER)
    public int Group;
    @Column(dataType = DbDataType.INTEGER)
    public int Number;

    @Column(dataType = DbDataType.INTEGER)
    public boolean OD_Blind;
    @Column(dataType = DbDataType.REAL)
    public float OD_Spherical;
    @Column(dataType = DbDataType.REAL)
    public float OD_Cylindrical;
    @Column(dataType = DbDataType.INTEGER)
    public int OD_Axis;
    @Column(dataType = DbDataType.REAL)
    public float OD_Add;

    @Column(dataType = DbDataType.INTEGER)
    public boolean OS_Blind;
    @Column(dataType = DbDataType.REAL)
    public float OS_Spherical;
    @Column(dataType = DbDataType.REAL)
    public float OS_Cylindrical;
    @Column(dataType = DbDataType.INTEGER)
    public int OS_Axis;
    @Column(dataType = DbDataType.REAL)
    public float OS_Add;

    @Column(dataType = DbDataType.TEXT, nullable = true)
    public Boolean Sunglasses;
    @Column(dataType = DbDataType.TEXT, nullable = true)
    public char Size;
    @Column(dataType = DbDataType.TEXT, nullable = true)
    public char Gender;

    /** Epoch Time (seconds since 1/1/1970 */
    @Column(dataType = DbDataType.INTEGER)
    public long AddedEpochTime;
    
    protected void copyFrom(Glasses glasses) {
        GlassesId = glasses.GlassesId;

        Group = glasses.Group;
        Number = glasses.Number;

        OD_Blind = glasses.OD_Blind;
        OD_Spherical = glasses.OD_Spherical;
        OD_Cylindrical = glasses.OD_Cylindrical;
        OD_Axis = glasses.OD_Axis;
        OD_Add = glasses.OD_Add;

        OS_Blind = glasses.OS_Blind;
        OS_Spherical = glasses.OS_Spherical;
        OS_Cylindrical = glasses.OS_Cylindrical;
        OS_Axis = glasses.OS_Axis;
        OS_Add = glasses.OS_Add;

        Sunglasses = glasses.Sunglasses;
        Size = glasses.Size;
        Gender = glasses.Gender;

        AddedEpochTime = glasses.AddedEpochTime;
    }
}
