package de.cebitec.mgx.gui.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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

        // Firmicutes
        AttributeI attr = master.Attribute().fetch(9230);
        assertNotNull(attr);
        Set<AttributeI> set = new HashSet<>();
        set.add(attr);
        
        SeqRunI run = master.SeqRun().fetch(49);
        AttributeTypeI atype = master.AttributeType().fetch(3);
        JobI job = master.Job().fetch(7);
        
        DistributionI<Long> dist = master.Attribute().getDistribution(atype, job, run);
        Long firmicutes = dist.get(attr);
        assertNotNull(firmicutes);
        assertEquals(10793, firmicutes);

        final AtomicInteger cnt = new AtomicInteger(0);
        final AtomicBoolean closed = new AtomicBoolean(false);

        SeqWriterI<DNASequenceI> dummy = new SeqWriterI<DNASequenceI>() {
            @Override
            public void addSequence(DNASequenceI seq) throws SeqStoreException {
                cnt.incrementAndGet();
            }

            @Override
            public void close() {
                closed.set(Boolean.TRUE);
            }
        };
        master.Sequence().downloadSequencesForAttributes(set, dummy, true);
        assertEquals(10793, cnt.get());
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
        SequenceI seq = master.Sequence().fetch(2109905);
        assertEquals("FI5LW4G01DZDXZ", seq.getName());
        assertEquals("TTTGCCATCGGCGCAGTCCTACTTATGAAGTTTGCAGAATAGCGTCAAGGCACTACCAAGGGG", seq.getSequence());
        assertEquals(63, seq.getLength());
    }
}
