package de.cebitec.mgx.gui.util;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.restgpms.GPMS;
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
public class TreeTest {

    public TreeTest() {
    }
    private MGXMaster master;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        MGXDTOMaster dtoMaster = null;
        GPMSClientI gpms = new GPMS("MyServer", "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/");
        if (!gpms.login("mgx_unittest", "gut-isM5iNt")) {
            assert false;
        }
        for (MembershipI m : gpms.getMemberships()) {
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Rsolani".equals(m.getProject().getName()))) {
                dtoMaster = new MGXDTOMaster(gpms, m);
                break;
            }
        }

        assert dtoMaster != null;
        assert dtoMaster.getProject().getName().equals("MGX_Rsolani");
        master = new MGXMaster(dtoMaster);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetDistribution() {
        System.out.println("getDistribution");
        Distribution dist = master.Attribute().getDistribution(8, 2);
        assertNotNull(dist);
        int cryptoCnt = 0;
        for (Attribute a : dist.keySet()) {
            if (a.getValue().equals("Cryptococcus")) {
                cryptoCnt++;
            }
        }
        assertEquals(2, cryptoCnt);
        assertEquals(367, dist.size());
    }

    @Test
    public void testGetHierarchy() {
        System.out.println("getHierarchy");
        Tree<Long> tree = master.Attribute().getHierarchy(5, 2);
        assertNotNull(tree);
        int cryptoCnt = 0;
        for (Node<Long> n : tree.getNodes()) {
            if (n.getAttribute().getValue().equals("Cryptococcus")) {
                cryptoCnt++;
            }
        }
        assertEquals(2, cryptoCnt);
    }
}