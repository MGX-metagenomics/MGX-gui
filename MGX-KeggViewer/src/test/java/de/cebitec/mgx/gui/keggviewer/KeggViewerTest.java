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
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class KeggViewerTest {

    public KeggViewerTest() {
    }

    @Test
    public void testRegressionInterrupted() throws MGXException {
        System.out.println("regressionInterrupted");

        VGroupManagerI mgr = VGroupManager.getTestInstance();
        VisualizationGroupI vg = mgr.createVisualizationGroup();
        MGXMasterI master = TestMaster.getRO();

        // add first run
        SeqRunI run1 = master.SeqRun().fetch(49);
        assertEquals(run1.getName(), "dataset1");
        vg.add(run1);

        mgr.registerResolver(new ConflictResolver() {
            @Override
            public void resolve(String attrType, List<GroupI> data) {
                data.clear();
            }
        });

        // select ec_number attribute type via manager which forwards the
        // selection to all groups
        boolean ok = mgr.selectAttributeType(AttributeRank.PRIMARY, "EC_number");
        assertTrue(ok);

        String attrType = vg.getSelectedAttributeType();
        assertNotNull(attrType);
        assertEquals("EC_number", attrType);

        attrType = mgr.getSelectedAttributeType();
        assertNotNull(attrType);
        assertEquals("EC_number", attrType);

        // add second run
        SeqRunI run2 = master.SeqRun().fetch(50);
        assertEquals(run2.getName(), "dataset2");
        vg.add(run2);

        attrType = vg.getSelectedAttributeType();
        assertNotNull(attrType);
        assertEquals("EC_number", attrType);

        attrType = mgr.getSelectedAttributeType();
        assertNotNull(attrType);
        assertEquals("EC_number", attrType);

        long start = System.currentTimeMillis();

        KeggViewer kv = new KeggViewer();
        
        // manually set vmgr to test instance
        kv.setVGroupManager(mgr);
        
        kv.getCustomizer();

        try {
            kv.selectPathways();
        } catch (ConflictingJobsException | KEGGException ex) {
            fail(ex.getMessage());
        }
        long duration = System.currentTimeMillis() - start;
        System.err.println("duration was " + duration + "ms");

    }

}
