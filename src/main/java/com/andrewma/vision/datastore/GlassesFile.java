package com.andrewma.vision.datastore;

import android.util.SparseArray;

import com.andrewma.vision.models.Glasses;

import java.util.ArrayList;
import java.util.List;

public class GlassesFile {

    private static final GlassesFile instance = new GlassesFile();

    public static GlassesFile getInstance() {
        return instance;
    }

    private SparseArray<Integer> mNextNumber = new SparseArray<Integer>(20);
    private final List<Glasses> mGlasses = new ArrayList<Glasses>();

    protected GlassesFile() { }

    public List<Glasses> getAll() {
        return mGlasses;
    }

    public void addGlasses(Glasses glasses) {
        final int group = calculateGroup(glasses);
        Integer nextNumber = mNextNumber.get(group);
        if(nextNumber == null) {
            nextNumber = new Integer(1);
            mNextNumber.put(group, nextNumber);
        }

        glasses.Group = group;
        glasses.Number = nextNumber.intValue();

        // update set of next numbers
        mNextNumber.put(group, glasses.Number + 1);

        save();
    }

    private int calculateGroup(Glasses glasses) {
        int group;
        // figure out which group this pair goes to. the algorithm for this is
        // rounding the OD (right) Sph value away from 0
        boolean positive = (glasses.OD_Spherical >= 0);
        group = (int) Math.ceil(Math.abs(glasses.OD_Spherical));
        // anything over 10 gets grouped into the 20 group
        if (group > 10) {
            group = 20;
        }
        if (!positive) {
            group *= -1;
        }
        return group;
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
