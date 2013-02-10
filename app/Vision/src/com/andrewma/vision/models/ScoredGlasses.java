package com.andrewma.vision.models;


/**
 * Extends the {@link Glasses} model to be used for search results.
 * 
 * @author ajma
 */
public class ScoredGlasses extends Glasses {

    public ScoredGlasses(Glasses glasses) {
        copyFrom(glasses);
    }

    public int Score;
    
    public String ScoreDetails;

    private void copyFrom(Glasses glasses) {
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

        AddedDate = glasses.AddedDate;
        RemovedDate = glasses.RemovedDate;
        RemovedReason = glasses.RemovedReason;
        RemovedBy = glasses.RemovedBy;
    }
}
