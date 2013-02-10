
package com.andrewma.vision.webserver.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.andrewma.vision.models.Glasses;
import com.andrewma.vision.models.ScoredGlasses;

public class GlassesScoring {

    public List<ScoredGlasses> score(Glasses search, List<Glasses> glasses, int max) {
        final List<ScoredGlasses> result = new ArrayList<ScoredGlasses>(glasses.size());
        for (Glasses g : glasses) {
            final ScoredGlasses scored = new ScoredGlasses(g);
            score(search, scored);
            if (scored.Score > 0) {
                result.add(scored);
            }
        }

        // sort
        Collections.sort(result, new ScoredGlassesComparator());

        return (result.size() <= max) ? result : result.subList(0, max);
    }

    private void score(Glasses search, ScoredGlasses glasses) {
        // OD scores
        float OD_Spherical = scoreSpherical(search.OD_Spherical, glasses.OD_Spherical);
        float OD_Cylindrical = scoreCylindrical(search.OD_Cylindrical, glasses.OD_Cylindrical);
        float OD_Axis = scoreAxis(search.OD_Cylindrical, search.OD_Axis, glasses.OD_Axis);
        // OS scores
        float OS_Spherical = scoreSpherical(search.OS_Spherical, glasses.OS_Spherical);
        float OS_Cylindrical = scoreCylindrical(search.OS_Cylindrical, glasses.OS_Cylindrical);
        float OS_Axis = scoreAxis(search.OS_Cylindrical, search.OS_Axis, glasses.OS_Axis);

        glasses.Score = (int) Math.floor(100 - OD_Spherical - OD_Cylindrical - OD_Axis
                - OS_Spherical - OS_Cylindrical - OS_Axis);
        glasses.ScoreDetails = String
                .format("OD_Spherical: %.1f\nOD_Cylindrical: %.1f\nOD_Axis: %.1f\nOS_Spherical: %.1f\nOS_Cylindrical: %.1f\nOS_Axis: %.1f\n",
                        OD_Spherical, OD_Cylindrical, OD_Axis,
                        OS_Spherical, OS_Cylindrical, OS_Axis);
    }

    private float scoreSpherical(float search, float glasses) {
        if ((search >= 0) && (glasses >= 0) && (search >= glasses)) {
            return (search - glasses) / 0.25f;
        } else if ((search < 0) && (glasses <= 0) && (search <= glasses)) {
            return (search - glasses) / -0.25f;
        }
        return 100f;
    }

    private float scoreCylindrical(float search, float glasses) {
        if ((search - 0.25f) <= glasses) {
            return Math.abs(search - glasses) / 0.25f;
        }
        return 100f;
    }

    private float scoreAxis(float search_cyl, int search_axis, int glasses_axis)
    {
        int reasonableRange = 5;
        if (search_cyl >= -1.0f) {
            reasonableRange = 20;
        } else if (search_cyl >= -2.0f) {
            reasonableRange = 15;
        } else if (search_cyl >= -3.0f) {
            reasonableRange = 10;
        }

        int distance = Math.abs(search_axis - glasses_axis);
        distance = Math.min(distance, 180 - distance);

        if (distance < reasonableRange) {
            return (8 * distance) / reasonableRange;
        } else if (distance < reasonableRange * 2) {
            return 15;
        } else if (distance < reasonableRange * 3) {
            return 25;
        } else {
            return 35;
        }
    }

    private class ScoredGlassesComparator implements Comparator<ScoredGlasses> {

        @Override
        public int compare(ScoredGlasses lhs, ScoredGlasses rhs) {
            return (lhs.Score > rhs.Score ? -1 : (lhs.Score == rhs.Score ? 0 : 1));
        }

    }
}
