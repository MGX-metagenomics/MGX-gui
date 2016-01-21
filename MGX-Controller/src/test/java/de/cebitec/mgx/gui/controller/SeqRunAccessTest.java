package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.util.TestMaster;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccessTest {

    @Test
    public void testFetch() throws MGXException {
        System.out.println("fetch");
        MGXMasterI m = TestMaster.getRO();
        SeqRunI sr1 = m.SeqRun().fetch(1);
        assertNotNull(sr1);
        assertNotNull(sr1.getMaster());
    }

    @Test
    public void testgetJobsAndAttributeTypes() throws MGXException {
        System.out.println("getJobsAndAttributeTypes");
        MGXMasterI m = TestMaster.getRO();
        SeqRunI sr1 = m.SeqRun().fetch(1);
        Map<JobI, Set<AttributeTypeI>> data = m.SeqRun().getJobsAndAttributeTypes(sr1);
        assertNotNull(data);
        assertEquals(10, data.size());

        int paramCnt = 0;

        for (JobI j : data.keySet()) {
            assertNotNull(j.getSeqrun());
            paramCnt += j.getParameters().size();
            for (JobParameterI jp : j.getParameters()) {
                assertNotNull(jp.getType());
            }
        }
        assertEquals(7, paramCnt);
    }

    @Test
    public void testEquality() throws MGXException {
        System.out.println("equals");
        MGXMasterI m = TestMaster.getRO();
        SeqRunI sr1 = m.SeqRun().fetch(1);
        SeqRunI sr2 = m.SeqRun().fetch(1);
        assertNotNull(sr1);
        assertNotNull(sr2);
        assertEquals(sr1, sr2);
    }

    @Test
    public void testDownload() throws IOException {
        System.out.println("testDownload");
        File tmpFile = File.createTempFile("down", "xx");

        try {
            final SeqWriterI<DNASequenceI> writer = new FastaWriter(tmpFile.getAbsolutePath());

            MGXMasterI m = TestMaster.getRO();
            SeqRunI sr1 = m.SeqRun().fetch(1);
            PropCounter pc = new PropCounter();
            final DownloadBaseI downloader = m.Sequence().createDownloader(sr1, writer, true);
            downloader.addPropertyChangeListener(pc);
            boolean success = downloader.download();

            assertTrue(success);
            assertNotNull(pc.getLastEvent());
            assertEquals(TransferBaseI.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());
        } catch (SeqStoreException | MGXException ex) {
            fail(ex.getMessage());
        } finally {
            tmpFile.delete();
        }
    }
}
