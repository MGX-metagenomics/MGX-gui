package de.cebitec.mgx.gui.keggviewer;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.visualization.ConflictResolver;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.Iterator;
import java.util.List;
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
    public void testRegressionInterrupted() throws MGXException {
        System.out.println("regressionInterrupted");

        VGroupManagerI mgr = VGroupManager.getTestInstance();
        VisualizationGroupI vg = mgr.createVisualizationGroup();
        MGXMasterI master = TestMaster.getRO();

        // add first run
        Iterator<SeqRunI> iter = master.SeqRun().fetchall();
        while (iter.hasNext()) {
            SeqRunI sr = iter.next();
            if (sr.getName().equals("dataset1")) {
                vg.add(sr);
            }
        }

        mgr.registerResolver(new ConflictResolver() {
            @Override
            public void resolve(String attrType, List<GroupI> data) {
                data.clear();
            }
        });

        // select ec_number attribute type
        boolean ok = mgr.selectAttributeType(AttributeRank.PRIMARY, "EC_number");
        assertTrue(ok);

        String attrType = mgr.getSelectedAttributeType();
        assertNotNull(attrType);
        assertEquals("EC_number", attrType);

        attrType = vg.getSelectedAttributeType();
        assertNotNull(attrType);
        assertEquals("EC_number", attrType);

        KeggViewer kv = new KeggViewer();
        kv.getCustomizer();
        
        // add second run
        iter = master.SeqRun().fetchall();
        while (iter.hasNext()) {
            SeqRunI sr = iter.next();
            if (sr.getName().equals("dataset2")) {
                vg.add(sr);
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
