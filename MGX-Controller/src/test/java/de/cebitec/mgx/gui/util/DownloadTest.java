package de.cebitec.mgx.gui.util;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class DownloadTest {

    private MGXMaster master;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        master = TestMaster.get();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDownloadSequencesForAttribute() {
        System.out.println("testDownloadSequencesForAttribute");
        Attribute attr = master.Attribute().fetch(1);
        assertNotNull(attr);
        Set<Attribute> set = new HashSet<>();
        set.add(attr);

        final Holder<Integer> cnt = new Holder<>();
        cnt.set(new Integer(0));
        final Holder<Boolean> closed = new Holder<>();
        closed.set(Boolean.FALSE);
        SeqWriterI dummy = new SeqWriterI() {
            @Override
            public void addSequence(DNASequenceI seq) throws IOException {
                cnt.set(cnt.get() + 1);
            }

            @Override
            public void close() throws Exception {
                closed.set(Boolean.TRUE);
            }
        };
        master.Sequence().downloadSequencesForAttributes(set, dummy);
        assertEquals(220, cnt.get().intValue());
        assertTrue(closed.get());
    }

//    @Test
//    public void testDownloadSeqRun() {
//        System.out.println("testDownloadSeqRun");
//
//        final Holder<Integer> cnt = new Holder<>();
//        cnt.set(new Integer(0));
//        final Holder<Boolean> closed = new Holder<>();
//        closed.set(Boolean.FALSE);
//        SeqWriterI dummy = new SeqWriterI() {
//            @Override
//            public void addSequence(DNASequenceI seq) throws IOException {
//                cnt.set(cnt.get() + 1);
//            }
//
//            @Override
//            public void close() throws Exception {
//                closed.set(Boolean.TRUE);
//            }
//        };
//        master.Sequence().downloadSequences(1, dummy);
//        assertEquals(59482, cnt.get().intValue());
//        assertTrue(closed.get());
//    }
    @Test
    public void testFetchSequence() {
        System.out.println("testFetchSequence");
        Sequence seq = master.Sequence().fetch(1);
        assertEquals("FI5LW4G01DZDXZ", seq.getName());
        assertEquals("tttgccatcggcgcagtcctacttatgaagtttgcagaatagcgtcaaggcactaccaagggg", seq.getSequence());
        assertEquals(63, seq.getLength());
    }

    private static class Holder<T> {

        T val = null;

        public void set(T newVal) {
            val = newVal;
        }

        public T get() {
            return val;
        }
    }
}