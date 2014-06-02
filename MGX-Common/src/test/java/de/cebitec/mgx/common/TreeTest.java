package de.cebitec.mgx.common;

import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class TreeTest {

    public TreeTest() {
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
    public void testMergeTrees() {
        System.out.println("mergeTrees");

        AttributeTypeI at = new AttributeType(null, 1, "at1", 'x', 'y');
        AttributeTypeI at2 = new AttributeType(null, 2, "at2", 'x', 'y');

        AttributeI a1 = new Attribute(null);
        a1.setAttributeType(at);
        a1.setId(7);
        a1.setValue("a1");
        a1.setParentID(-1);

        AttributeI a2 = new Attribute(null);
        a2.setAttributeType(at2);
        a2.setId(8);
        a2.setValue("a2");
        a2.setParentID(7);

        Map<AttributeI, Long> data = new HashMap<>();
        data.put(a1, Long.valueOf(1));
        data.put(a2, Long.valueOf(2));

        TreeI<Long> tree = TreeFactory.createTree(data);
        assertEquals(2, tree.size());
        assertEquals(a1, tree.getRoot().getAttribute());
        assertEquals(1, tree.getRoot().getChildren().size());
        assertEquals(1, tree.getRoot().getContent().longValue());

        AttributeI a3 = new Attribute(null);
        a3.setAttributeType(at);
        a3.setId(99);
        a3.setValue("a1");
        a3.setParentID(-1);

        AttributeI a4 = new Attribute(null);
        a4.setAttributeType(at2);
        a4.setId(89);
        a4.setValue("a3");
        a4.setParentID(99);

        Map<AttributeI, Long> data2 = new HashMap<>();
        data2.put(a3, Long.valueOf(3));
        data2.put(a4, Long.valueOf(4));

        TreeI<Long> tree2 = TreeFactory.createTree(data2);
        assertEquals(3, tree2.getRoot().getContent().longValue());


        Collection<TreeI<Long>> c = new ArrayList<>();
        c.add(tree);
        c.add(tree2);
        assertEquals(2, c.size());
        
        TreeI<Long> merged = TreeFactory.mergeTrees(c);
        assertEquals("a1", merged.getRoot().getAttribute().getValue());
        assertEquals(3, merged.getNodes().size());
        
        assertEquals(4, merged.getRoot().getContent().longValue());
        assertEquals(0, merged.getRoot().getDepth());
        
        NodeI<Long> n = null;
        for (NodeI<Long> tmp : merged.getNodes()) {
            if (tmp.getAttribute().getValue().equals("a3")) {
                n = tmp;
                break;
            }
        }
        assertNotNull(n);
        
        
        NodeI<Long>[] path = n.getPath();
        assertEquals(2, path.length);
        for (NodeI<Long> x : path) {
            assertNotNull(x);
        }

    }
}