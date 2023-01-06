package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class TrackFactoryTest {

    @Test
    public void testNoMappings() {
        System.out.println("testNoMappings");
        @SuppressWarnings("unchecked")
        List<MappedSequenceI> mappings = Collections.EMPTY_LIST;
        List<TrackI> result = new ArrayList<>();
        TrackFactory.createTracks(mappings, result);
        assertEquals(0, result.size());
    }

    @Test
    public void testCreateOneTrack() {
        System.out.println("testCreateOneTrack");
        List<MappedSequenceI> mappings = new ArrayList<>();

        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
        MappedSequence ms2 = new MappedSequence(1, 250, 270, 0);

        mappings.add(ms1);
        mappings.add(ms2);

        List<TrackI> result = new ArrayList<>();
        TrackFactory.createTracks(mappings, result);

        //    ---------   ---------
        assertEquals(1, result.size());
    }

    @Test
    public void testCreateTwoTracks() {
        System.out.println("testCreateTwoTracks");
        List<MappedSequenceI> mappings = new ArrayList<>();

        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
        MappedSequence ms2 = new MappedSequence(1, 15, 25, 0);

        mappings.add(ms1);
        mappings.add(ms2);

        List<TrackI> result = new ArrayList<>();
        TrackFactory.createTracks(mappings, result);

        //    ---------
        //         -----------
        assertEquals(2, result.size());
        assertTrue(result.get(0).sequences().contains(ms1));
        assertTrue(result.get(1).sequences().contains(ms2));
    }

    @Test
    public void testCreateTracks2() {
        System.out.println("createTracks2");
        List<MappedSequenceI> mappings = new ArrayList<>();

        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
        MappedSequence ms2 = new MappedSequence(1, 15, 25, 0);
        MappedSequence ms3 = new MappedSequence(1, 22, 35, 0);

        mappings.add(ms1);
        mappings.add(ms2);
        mappings.add(ms3);

        List<TrackI> result = new ArrayList<>();
        TrackFactory.createTracks(mappings, result, 1);

        //    ---------   ---------
        //         -----------
        assertEquals(2, result.size());
        assertTrue(result.get(0).sequences().contains(ms1));
        assertTrue(result.get(0).sequences().contains(ms3));
        assertTrue(result.get(1).sequences().contains(ms2));
    }

//    @Test
//    public void testTiming() {
//        System.out.println("testTiming");
//
//        int numIter = 100;
//        long duration = 0;
//        
//        for (int i = 0; i < numIter; i++) {
//            Iterator<MappedSequenceI> iter = new Iterator<MappedSequenceI>() {
//
//                int num = 0;
//
//                @Override
//                public boolean hasNext() {
//                    return num < 500_000;
//                }
//
//                @Override
//                public MappedSequenceI next() {
//                    num++;
//                    return new MappedSequence(42, num, num + 40, 99.5f);
//                }
//            };
//
//            Collection<TrackI> result = new ArrayList<>();
//            long start = System.currentTimeMillis();
//            TrackFactory.createTracks(iter, result);
//            start = System.currentTimeMillis() - start;
//            duration += start;
//            
//            assertEquals(41, result.size());
//        }
//        
//        duration /= numIter;
//        System.err.println("layout took " + duration + "ms");
//
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
//    @Test
//    public void testTiming() throws MGXException {
//        System.out.println("testTiming");
//        MGXMasterI master = TestMaster.getRO();
//        Iterator<MappingI> iter = master.Mapping().fetchall();
//        MappingI mapping = null;
//        while (iter.hasNext()) {
//            mapping = iter.next();
//        }
//        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
//        JobI job = master.Job().fetch(mapping.getJobID());
//        //UUID uuid = master.Mapping().openMapping(mapping.getId());
//        MappingCtx ctx = new MappingCtx(mapping, ref, job, master.SeqRun().fetch(mapping.getSeqrunID()));
//        ViewController vc = new ViewController(ctx);
//
//        vc.setBounds(0, ref.getLength() - 1);
//        SortedSet<MappedSequenceI> mappings = ctx.getMappings(0, ref.getLength() - 1);
//        int numMappings = mappings.size();
//
//        List<Track> tracks = new ArrayList<>();
//
//        int numTracks1, numTracks2;
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 500; i++) {
//            tracks.clear();
//            TrackFactory.createTracks(0, mappings, tracks);
//        }
//        start = (System.currentTimeMillis() - start);
//        System.err.println("  first took " + start + " ms for " + (500 * mappings.size() + " mappings"));
//        numTracks1 = tracks.size();
//
//        int sum = 0;
//        for (Track t : tracks) {
//            sum += t.size();
//        }
//        assertEquals(numMappings, sum);
//
//        start = System.currentTimeMillis();
//        for (int i = 0; i < 500; i++) {
//            tracks.clear();
//            TrackFactory.createTracks2(mappings, tracks);
//        }
//        start = (System.currentTimeMillis() - start);
//        System.err.println("  second took " + start + " ms");
//        numTracks2 = tracks.size();
//
//        sum = 0;
//        for (Track t : tracks) {
//            sum += t.size();
//        }
//        assertEquals(numMappings, sum);
//
//        assertEquals(numTracks1, numTracks2);
//    }
//    @Test
//    public void testTiming2() {
//        System.out.println("testTiming2 - worst case, all reads in same area");
//        SortedSet<MappedSequenceI> mappings = new TreeSet<>();
//        for (long i = 0; i < 2000; i++) {
//            int start = (int) (500 + FastMath.random() * 25);
//            int stop = (int) (800 + FastMath.random() * 250);
//            int ident = (int) (FastMath.random() * 100);
//            mappings.add(new MappedSequence(null, i, start, stop, ident));
//        }
//        List<Track> tracks = new ArrayList<>();
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 100; i++) {
//            TrackFactory.createTracks(0, mappings, tracks);
//        }
//        start = (System.currentTimeMillis() - start);
//        System.err.println(" took " + start + " ms for " + (100 * mappings.size() + " mappings"));
//        
//        assertEquals(2000, tracks.size());
//    }
}
