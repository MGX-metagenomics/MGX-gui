package de.cebitec.mgx.gui.keggviewer;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.kegg.pathways.KEGGException;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class KeggViewerTest {

    public KeggViewerTest() {
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
    public void testRegressionInterrupted() {
        System.out.println("regressionInterrupted");

        VisualizationGroupI vg = VGroupManager.getInstance().createGroup();
        MGXMasterI master = TestMaster.getRO();

        // add first run
        Iterator<SeqRunI> iter = master.SeqRun().fetchall();
        while (iter.hasNext()) {
            SeqRunI sr = iter.next();
            if (sr.getName().equals("dataset1")) {
                vg.addSeqRun(sr);
            }
        }

        // select ec_number attribute type
        try {
            vg.selectAttributeType(AttributeRank.PRIMARY, "EC_number");
        } catch (ConflictingJobsException ex) {
            fail(ex.getMessage());
        }

        KeggViewer kv = new KeggViewer();
        kv.getCustomizer();

        // add second run
        iter = master.SeqRun().fetchall();
        while (iter.hasNext()) {
            SeqRunI sr = iter.next();
            if (sr.getName().equals("dataset2")) {
                vg.addSeqRun(sr);
            }
        }

        long start = System.currentTimeMillis();

        try {
            kv.selectPathways();
        } catch (ConflictingJobsException | KEGGException ex) {
            Exceptions.printStackTrace(ex);
        }
        long duration = System.currentTimeMillis() - start;
        System.err.println("duration was " + duration + "ms");

    }

}
