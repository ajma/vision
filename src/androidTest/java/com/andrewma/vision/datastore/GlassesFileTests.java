package com.andrewma.vision.datastore;

import android.test.InstrumentationTestCase;

import com.andrewma.vision.models.Glasses;

public class GlassesFileTests extends InstrumentationTestCase {
    public void testAddFirstGlasses() throws Exception {
        final GlassesFile mGlassesFile = new GlassesFile();

        final int expected = 1;
        final Glasses addGlasses = new Glasses();
        addGlasses.OD_Spherical = 1;
        mGlassesFile.addGlasses(addGlasses);
        assertEquals(expected, addGlasses.Number);
    }

    public void testAddTwoGlassesSameGroup() throws Exception {
        final GlassesFile mGlassesFile = new GlassesFile();

        final Glasses addGlasses1 = new Glasses();
        addGlasses1.OD_Spherical = 1;
        mGlassesFile.addGlasses(addGlasses1);

        final int expected = 2;
        final Glasses addGlasses2 = new Glasses();
        addGlasses2.OD_Spherical = 1;
        mGlassesFile.addGlasses(addGlasses2);
        assertEquals(expected, addGlasses2.Number);
    }

    public void testAddTwoGlassesDiffGroup() throws Exception {
        final GlassesFile mGlassesFile = new GlassesFile();

        final int expected1 = 1;
        final Glasses addGlasses1 = new Glasses();
        addGlasses1.OD_Spherical = 1;
        mGlassesFile.addGlasses(addGlasses1);
        assertEquals(expected1, addGlasses1.Number);

        final int expected2 = 1;
        final Glasses addGlasses2 = new Glasses();
        addGlasses2.OD_Spherical = 2;
        mGlassesFile.addGlasses(addGlasses2);
        assertEquals(expected2, addGlasses2.Number);
    }

    public void testAddHighGroupGlasses() throws Exception {
        final GlassesFile mGlassesFile = new GlassesFile();

        final int expectedGroup = 20;
        final Glasses addGlasses = new Glasses();
        addGlasses.OD_Spherical = 12;
        mGlassesFile.addGlasses(addGlasses);
        assertEquals(expectedGroup, addGlasses.Group);
    }

    public void testAddPositiveGroupGlasses() throws Exception {
        final GlassesFile mGlassesFile = new GlassesFile();

        final int expectedGroup = 2;
        final Glasses addGlasses = new Glasses();
        addGlasses.OD_Spherical = 1.5f;
        mGlassesFile.addGlasses(addGlasses);
        assertEquals(expectedGroup, addGlasses.Group);
    }
    public void testAddNegativeGroupGlasses() throws Exception {
        final GlassesFile mGlassesFile = new GlassesFile();

        final int expectedGroup = -2;
        final Glasses addGlasses = new Glasses();
        addGlasses.OD_Spherical = -1.5f;
        mGlassesFile.addGlasses(addGlasses);
        assertEquals(expectedGroup, addGlasses.Group);
    }
}
