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
import de.cebitec.mgx.gui.mapping.MappingCtx;
import de.cebitec.mgx.gui.mapping.TestMaster;
import de.cebitec.mgx.gui.mapping.ViewController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
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
            TrackFactory.createTracks(mappings, tracks);
        }
        start = (System.currentTimeMillis() - start);
        System.err.println("  first took " + start + " ms");
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
}
