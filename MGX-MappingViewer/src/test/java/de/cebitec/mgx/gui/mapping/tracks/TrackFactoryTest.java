/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class TrackFactoryTest {

    public TrackFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateTracks() {
        System.out.println("createTracks");
        SortedSet<MappedSequence> mappings = new TreeSet<>();

        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
        MappedSequence ms2 = new MappedSequence(1, 15, 25, 0);

        mappings.add(ms1);
        mappings.add(ms2);

        List<Track> result = TrackFactory.createTracks(mappings);

        assertEquals(2, result.size());
    }

    @Test
    public void testCreateTracks2() {
        System.out.println("createTracks2");
        SortedSet<MappedSequence> mappings = new TreeSet<>();

        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
        MappedSequence ms2 = new MappedSequence(1, 25, 45, 0);

        mappings.add(ms1);
        mappings.add(ms2);

        List<Track> result = TrackFactory.createTracks(mappings);

        assertEquals(1, result.size());
    }

}
