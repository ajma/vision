
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
}
