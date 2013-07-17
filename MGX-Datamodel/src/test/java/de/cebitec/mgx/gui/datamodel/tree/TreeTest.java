package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

        AttributeType at = new AttributeType(1, "at1", 'x', 'y');
        AttributeType at2 = new AttributeType(2, "at2", 'x', 'y');

        Attribute a1 = new Attribute();
        a1.setAttributeType(at);
        a1.setId(7);
        a1.setValue("a1");
        a1.setParentID(-1);

        Attribute a2 = new Attribute();
        a2.setAttributeType(at2);
        a2.setId(8);
        a2.setValue("a2");
        a2.setParentID(7);

        Map<Attribute, Long> data = new HashMap<>();
        data.put(a1, Long.valueOf(1));
        data.put(a2, Long.valueOf(2));

        Tree<Long> tree = TreeFactory.createTree(data);
        assertEquals(2, tree.size());
        assertEquals(a1, tree.getRoot().getAttribute());
        assertEquals(1, tree.getRoot().getChildren().size());
        assertEquals(1, tree.getRoot().getContent().longValue());

        Attribute a3 = new Attribute();
        a3.setAttributeType(at);
        a3.setId(99);
        a3.setValue("a1");
        a3.setParentID(-1);

        Attribute a4 = new Attribute();
        a4.setAttributeType(at2);
        a4.setId(89);
        a4.setValue("a3");
        a4.setParentID(99);

        Map<Attribute, Long> data2 = new HashMap<>();
        data2.put(a3, Long.valueOf(3));
        data2.put(a4, Long.valueOf(4));

        Tree<Long> tree2 = TreeFactory.createTree(data2);
        assertEquals(3, tree2.getRoot().getContent().longValue());


        Collection<Tree<Long>> c = new ArrayList<>();
        c.add(tree);
        c.add(tree2);
        assertEquals(2, c.size());
        
        Tree<Long> merged = TreeFactory.mergeTrees(c);
        assertEquals("a1", merged.getRoot().getAttribute().getValue());
        assertEquals(3, merged.getNodes().size());
        
        assertEquals(4, merged.getRoot().getContent().longValue());
        assertEquals(0, merged.getRoot().getDepth());
        
        Node<Long> n = null;
        for (Node<Long> tmp : merged.getNodes()) {
            if (tmp.getAttribute().getValue().equals("a3")) {
                n = tmp;
            }
        }
        assertNotNull(n);
        Node<Long>[] path = n.getPath();
        assertEquals(2, path.length);
        for (Node<Long> x : path) {
            assertNotNull(x);
        }

    }
}