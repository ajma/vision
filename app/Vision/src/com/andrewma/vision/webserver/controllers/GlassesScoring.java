
package com.andrewma.vision.webserver.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.andrewma.vision.models.Glasses;
import com.andrewma.vision.models.ScoredGlasses;

public class GlassesScoring {

    public List<ScoredGlasses> score(List<Glasses> glasses, int max) {
        final List<ScoredGlasses> result = new ArrayList<ScoredGlasses>(glasses.size());
        int i = 0;
        for (Glasses g : glasses) {
            final ScoredGlasses scored = new ScoredGlasses(g);
            scored.Score = i++ == 10 ? 10 : 1;

            result.add(scored);
        }

        // sort
        Collections.sort(result, new ScoredGlassesComparator());

        return (result.size() <= max) ? result : result.subList(0, max);
    }

    private class ScoredGlassesComparator implements Comparator<ScoredGlasses> {

        @Override
        public int compare(ScoredGlasses lhs, ScoredGlasses rhs) {
            return (lhs.Score > rhs.Score ? -1 : (lhs.Score == rhs.Score ? 0 : 1));
        }

    }
}
