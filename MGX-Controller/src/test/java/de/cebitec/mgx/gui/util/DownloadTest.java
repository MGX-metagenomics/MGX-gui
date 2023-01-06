package de.cebitec.mgx.gui.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class DownloadTest {

    @Test
    public void testDownloadSequencesForAttribute() throws MGXException {
        System.out.println("testDownloadSequencesForAttribute");
        MGXMasterI master = TestMaster.getRO();
        
        // GC, 50.8 
        AttributeI attr = master.Attribute().fetch(1);
        assertNotNull(attr);
        Set<AttributeI> set = new HashSet<>();
        set.add(attr);

        final Holder<Integer> cnt = new Holder<>();
        cnt.set(0);
        final Holder<Boolean> closed = new Holder<>();
        closed.set(Boolean.FALSE);
        
        SeqWriterI<DNASequenceI> dummy = new SeqWriterI<DNASequenceI>() {
            @Override
            public void addSequence(DNASequenceI seq) throws SeqStoreException {
                cnt.set(cnt.get() + 1);
            }

            @Override
            public void close() throws Exception {
                closed.set(Boolean.TRUE);
            }
        };
        master.Sequence().downloadSequencesForAttributes(set, dummy, true);
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
    public void testFetchSequence() throws MGXException {
        System.out.println("testFetchSequence");
        MGXMasterI master = TestMaster.getRO();
        SequenceI seq = master.Sequence().fetch(1);
        assertEquals("FI5LW4G01DZDXZ", seq.getName());
        assertEquals("TTTGCCATCGGCGCAGTCCTACTTATGAAGTTTGCAGAATAGCGTCAAGGCACTACCAAGGGG", seq.getSequence());
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