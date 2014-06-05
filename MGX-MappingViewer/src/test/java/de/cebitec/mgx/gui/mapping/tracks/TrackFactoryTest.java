/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.mapping.MappingCtx;
import de.cebitec.mgx.gui.mapping.TestMaster;
import de.cebitec.mgx.gui.mapping.ViewController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

//    @Test
//    public void testCreateTracks() {
//        System.out.println("createTracks");
//        SortedSet<MappedSequence> mappings = new TreeSet<>();
//
//        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
//        MappedSequence ms2 = new MappedSequence(1, 15, 25, 0);
//
//        mappings.add(ms1);
//        mappings.add(ms2);
//
//        List<Track> result = new ArrayList<>();
//        TrackFactory.createTracks(mappings, result);
//
//        assertEquals(2, result.size());
//    }
//
//    @Test
//    public void testCreateTracks2() {
//        System.out.println("createTracks2");
//        SortedSet<MappedSequence> mappings = new TreeSet<>();
//
//        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
//        MappedSequence ms2 = new MappedSequence(1, 25, 45, 0);
//
//        mappings.add(ms1);
//        mappings.add(ms2);
//
//        List<Track> result = new ArrayList<>();
//        TrackFactory.createTracks(mappings, result);
//
//        assertEquals(2, result.size());
//    }
    @Test
    public void testTiming() {
        System.out.println("testTiming");
        MGXMasterI master = TestMaster.getRO();
        Iterator<MappingI> iter = master.Mapping().fetchall();
        MappingI mapping = null;
        while (iter.hasNext()) {
            mapping = iter.next();
        }
        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
        JobI job = master.Job().fetch(mapping.getJobID());
        //UUID uuid = master.Mapping().openMapping(mapping.getId());
        MappingCtx ctx = new MappingCtx(mapping, ref, job);
        ViewController vc = new ViewController(ctx);

        vc.setBounds(0, ref.getLength() - 1);
        SortedSet<MappedSequenceI> mappings = vc.getMappings(0, ref.getLength() - 1);
        int numMappings = mappings.size();

        List<Track> tracks = new ArrayList<>();

        int numTracks1, numTracks2;

        long start = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            tracks.clear();
            TrackFactory.createTracks(0, mappings, tracks);
        }
        start = (System.currentTimeMillis() - start);
        System.err.println("  first took " + start + " ms for " + (500 * mappings.size() + " mappings"));
        numTracks1 = tracks.size();

        int sum = 0;
        for (Track t : tracks) {
            sum += t.size();
        }
        assertEquals(numMappings, sum);

        start = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            tracks.clear();
            TrackFactory.createTracks2(mappings, tracks);
        }
        start = (System.currentTimeMillis() - start);
        System.err.println("  second took " + start + " ms");
        numTracks2 = tracks.size();

        sum = 0;
        for (Track t : tracks) {
            sum += t.size();
        }
        assertEquals(numMappings, sum);

        assertEquals(numTracks1, numTracks2);
    }

    @Test
    public void testTiming2() {
        System.out.println("testTiming2 - worst case, all reads in same area");
        SortedSet<MappedSequenceI> mappings = new TreeSet<>();
        for (long i = 0; i < 20000; i++) {
            int start = (int) (500 + Math.random() * 25);
            int stop = (int) (800 + Math.random() * 250);
            int ident = (int) (Math.random() * 100);
            mappings.add(new MappedSequence(null, i, start, stop, ident));
        }
        List<Track> tracks = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            TrackFactory.createTracks(0, mappings, tracks);
        }
        start = (System.currentTimeMillis() - start);
        System.err.println(" took " + start + " ms for " + (100 * mappings.size() + " mappings"));
        
        assertEquals(20000, tracks.size());
//         took 18058 ms for 500000 mappings
//         took 18209 ms for 500000 mappings
//         took 17899 ms for 500000 mappings
        // added check-last
//         took 197 ms for 2000000 mappings
//         took 189 ms for 2000000 mappings
        // remember last track instead retrieving last from list
//         took 143 ms for 2000000 mappings
//         took 162 ms for 2000000 mappings
//         took 156 ms for 2000000 mappings
        
        assertTrue("Slow layout!", start < 1250);
    }
}
