package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        master = TestMaster.get();
        Distribution dist = master.Attribute().getDistribution(6, 3);
        assertNotNull(dist);
        assertEquals(5, dist.size());
        assertEquals(24, dist.getTotalClassifiedElements());
    }

    @Test
    public void testFetch() {
        System.out.println("fetch");
        master = TestMaster.get();
        Attribute attr = master.Attribute().fetch(1);
        assertNotNull(attr);
        assertNotNull(attr.getAttributeType());
        assertEquals("50.8", attr.getValue());
        assertEquals("GC", attr.getAttributeType().getName());
    }

    @Test
    public void testGetHierarchy() {
        System.out.println("getHierarchy");
        master = TestMaster.get();
        Tree<Long> tree = master.Attribute().getHierarchy(6, 3);
        assertNotNull(tree);
        assertEquals(30, tree.getNodes().size());
        assertNotNull(tree.getRoot());

        Node<Long> n = null;
        for (Node x : tree.getNodes()) {
            if (x.getAttribute().getValue().equals("Bacteroidetes")) {
                n = x;
                break;
            }
        }
        assertNotNull(n);
        assertEquals(2, n.getDepth());

        // check the path
        Node<Long>[] path = n.getPath();
        assertEquals(3, path.length);
        assertEquals(path[0], tree.getRoot());
        assertNotNull(path[1]);
        assertEquals(path[2], n);


    }

    @Test
    public void verifyTreeStructure() {
        System.out.println("verifyTreeStructure");
        master = TestMaster.get();
        Tree<Long> tree = master.Attribute().getHierarchy(6, 3);
        assertNotNull(tree);

        for (Node<Long> node : tree.getNodes()) {
            if (!node.isRoot()) {
                assertNotNull(node.getParent());
            }

            if (node.isLeaf()) {
                assertFalse(node.hasChildren());
            }
        }

        // nodes and node parents attributes types have to differ
        for (Node<Long> node : tree.getNodes()) {
            if (!node.isRoot()) {
                assertNotSame(node.getAttribute().getAttributeType(), node.getParent().getAttribute().getAttributeType());
                assertFalse(node.getAttribute().getAttributeType().getName().equals(node.getParent().getAttribute().getAttributeType().getName()));
            }
        }

        for (Node<Long> node : tree.getNodes()) {
            assertEquals(node.getDepth() + 1, node.getPath().length);
        }
    }
}