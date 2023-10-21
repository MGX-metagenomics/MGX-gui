package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.TermAccessI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.TermI;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderFactory;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import de.cebitec.mgx.testutils.PropCounter;
import de.cebitec.mgx.testutils.TestInput;
import de.cebitec.mgx.testutils.TestMaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccessTest {

    @Test
    public void testFetch() throws MGXException {
        System.out.println("fetch");
        MGXMasterI m = TestMaster.getRO();
        SeqRunI sr1 = m.SeqRun().fetch(49);
        assertNotNull(sr1);
        assertNotNull(sr1.getMaster());
    }

    @Test
    public void testFetchall() throws MGXException {
        System.out.println("fetchall");
        MGXMasterI m = TestMaster.getRO();
        Iterator<SeqRunI> iter = m.SeqRun().fetchall();
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            SeqRunI next = iter.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(6, cnt);
    }

    @Test
    public void testByJob() {
        System.out.println("testByJob");
        MGXMasterI m = TestMaster.getRO();
        Iterator<SeqRunI> iter = null;
        JobI job = null;
        try {
            job = m.Job().fetch(7);
            iter = m.SeqRun().ByJob(job);
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(job);
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        SeqRunI run = iter.next();
        assertEquals(49, run.getId());
        assertEquals("dataset1", run.getName());
        assertNotNull(job.getSeqruns());
        assertEquals(1, job.getSeqruns().length);
    }

    @Test
    public void testgetJobsAndAttributeTypes() throws MGXException {
        System.out.println("getJobsAndAttributeTypes");
        MGXMasterI m = TestMaster.getRO();
        SeqRunI sr1 = m.SeqRun().fetch(49);
        Map<JobI, Set<AttributeTypeI>> data = m.SeqRun().getJobsAndAttributeTypes(sr1);
        assertNotNull(data);
        assertEquals(3, data.size());

        int paramCnt = 0;

        for (JobI j : data.keySet()) {
            assertNotNull(j.getSeqruns());
            assertNotEquals(0, j.getSeqruns().length);
            paramCnt += j.getParameters().size();
            for (JobParameterI jp : j.getParameters()) {
                assertNotNull(jp.getType());
            }
        }
        assertEquals(6, paramCnt);
    }

    @Test
    public void testEquality() throws MGXException {
        System.out.println("equals");
        MGXMasterI m = TestMaster.getRO();
        SeqRunI sr1 = m.SeqRun().fetch(49);
        SeqRunI sr2 = m.SeqRun().fetch(49);
        assertNotNull(sr1);
        assertNotNull(sr2);
        //assertNotSame(sr1, sr2);
        assertEquals(sr1, sr2);
    }

    @Test
    public void testRegressionEquality() throws MGXException {
        System.out.println("testRegressionEquality");
        MGXMasterI m1 = TestMaster.getPrivate("MGX_deNBI1");
        MGXMasterI m2 = TestMaster.getPrivate("MGX_deNBI4");
        assumeTrue(m1 != null);
        assumeTrue(m2 != null);
        SeqRunI run1 = m1.SeqRun().fetch(2);
        SeqRunI run2 = m2.SeqRun().fetch(2);
        assertNotNull(run1);
        assertNotNull(run2);
        assertEquals(run1.getName(), run2.getName());
        assertNotEquals(run1.getMaster(), run2.getMaster());
        assertNotEquals(run1, run2, "runs from different projects should not be equal");
    }

    @Test
    public void testDownload() throws IOException {
        System.out.println("testDownload");
        File tmpFile = File.createTempFile("down", "xx");

        try {
            final SeqWriterI<DNASequenceI> writer = new FastaWriter(tmpFile.getAbsolutePath());

            MGXMasterI m = TestMaster.getRO();
            SeqRunI sr1 = m.SeqRun().fetch(49);
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

    @Test
    public void testSeqRunUpload() {
        System.out.println("testSeqRunUpload");

        MGXMasterI m = TestMaster.getRW();
        SeqRunI newRun = null;

        try {
            List<TermI> methods = m.Term().byCategory(TermAccessI.SEQ_METHODS);
            List<TermI> platforms = m.Term().byCategory(TermAccessI.SEQ_PLATFORMS);
            DNAExtractI ex = m.DNAExtract().fetch(48);
            newRun = m.SeqRun().create(ex, "sample data", methods.get(0), platforms.get(0), false, false, "");

        } catch (MGXException ex) {
            fail(ex.getMessage());
        }

        assertNotNull(newRun);
        assertEquals(-1, newRun.getNumSequences());

        SeqReaderI<? extends DNASequenceI> reader = null;
        try {
            File testFasta = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/gui/controller/sample.fas");
            reader = SeqReaderFactory.getReader(testFasta.getAbsolutePath());
        } catch (SeqStoreException | IOException ex) {
            fail(ex.getMessage());
        }

        assertNotNull(reader);
        boolean success = false;
        try {
            UploadBaseI uploader = m.Sequence().createUploader(newRun, reader);
            assertNotNull(uploader);
            success = uploader.upload();
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }
        assertTrue(success, "Upload did not succeed as expected");

        // check number of sequences uploaded for local SeqRunI instance
        long numSeqs = newRun.getNumSequences();
        long numSeqs2 = -1;

        try {
            SeqRunI newRunCopy = m.SeqRun().fetch(newRun.getId());
            assertNotNull(newRunCopy);
            numSeqs2 = newRunCopy.getNumSequences();
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }

        // cleanup
        try {
            TaskI<SeqRunI> delTask = m.SeqRun().delete(newRun);
            while (!delTask.done()) {
                m.<SeqRunI>Task().refresh(delTask);
            }
        } catch (MGXException ex) {
            fail(ex.getMessage());
        }

        assertEquals(2, numSeqs);
        assertEquals(2, numSeqs2);
    }
}
