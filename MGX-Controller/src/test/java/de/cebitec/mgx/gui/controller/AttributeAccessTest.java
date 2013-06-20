package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.HashSet;
import java.util.Iterator;
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
public class AttributeAccessTest {

    private MGXMaster master;

    public AttributeAccessTest() {
    }

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
    public void testGetDistribution() {
        System.out.println("getDistribution");
        Distribution dist = master.Attribute().getDistribution(6, 3);
        assertNotNull(dist);
        assertEquals(5, dist.size());
        assertEquals(24, dist.getTotalClassifiedElements());
    }

    @Test
    public void testFetch() {
        System.out.println("fetch");
        Attribute attr = master.Attribute().fetch(1);
        assertNotNull(attr);
        assertNotNull(attr.getAttributeType());
        assertEquals("50.8", attr.getValue());
        assertEquals("GC", attr.getAttributeType().getName());
    }

    @Test
    public void testGetHierarchy() {
        System.out.println("getHierarchy");
        Tree<Long> tree = master.Attribute().getHierarchy(6, 3);
        assertNotNull(tree);
        assertEquals(30, tree.getNodes().size());
    }
}