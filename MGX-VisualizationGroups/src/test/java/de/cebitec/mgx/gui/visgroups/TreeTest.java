package de.cebitec.mgx.gui.visgroups;

import de.cebitec.mgx.gui.datafactories.TreeFactory;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class TreeTest {

    public TreeTest() {
    }

    @Test
    public void testNorm() {
        System.out.println("testNorm");

        AttributeTypeI aTypeRoot = new AttributeType(null, 0, "rank_root", AttributeTypeI.VALUE_DISCRETE, AttributeTypeI.STRUCTURE_HIERARCHICAL);
        AttributeI attr_root = new Attribute();
        attr_root.setAttributeType(aTypeRoot);

        AttributeTypeI aType = new AttributeType(null, 0, "rank1", AttributeTypeI.VALUE_DISCRETE, AttributeTypeI.STRUCTURE_HIERARCHICAL);
        AttributeI attr1 = new Attribute();
        attr1.setAttributeType(aType);
        AttributeI attr2 = new Attribute();
        attr2.setAttributeType(aType);
        AttributeI attr3 = new Attribute();
        attr3.setAttributeType(aType);

        TreeI<Long> t = new Tree<>();
        NodeI<Long> root = t.createRootNode(attr_root, 5l);
        root.addChild(attr1, 10l);
        root.addChild(attr2, 10l);
        root.addChild(attr3, 10l);
        TreeI<Double> normalized = TreeFactory.normalize(t);
        assertNotNull(normalized);
        assertEquals(1d, normalized.getRoot().getContent(), 0.0000001d);
        assertEquals(3, normalized.getLeaves().size());
        for (NodeI<Double> n : normalized.getLeaves()) {
            assertEquals(0.33333333d, n.getContent(), 0.00000001d);
        }
    }

    @Test
    public void testMergeTrees() {
        System.out.println("mergeTrees");

        AttributeTypeI at = new AttributeType(null, 1, "at1", 'x', 'y');
        AttributeTypeI at2 = new AttributeType(null, 2, "at2", 'x', 'y');

        AttributeI a1 = new Attribute();
        a1.setAttributeType(at);
        a1.setId(7);
        a1.setValue("a1");
        a1.setParentID(-1);

        AttributeI a2 = new Attribute();
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
        assertEquals(Long.valueOf(1), tree.getRoot().getContent());

        AttributeI a3 = new Attribute();
        a3.setAttributeType(at);
        a3.setId(99);
        a3.setValue("a1");
        a3.setParentID(-1);

        AttributeI a4 = new Attribute();
        a4.setAttributeType(at2);
        a4.setId(89);
        a4.setValue("a3");
        a4.setParentID(99);

        Map<AttributeI, Long> data2 = new HashMap<>();
        data2.put(a3, Long.valueOf(3));
        data2.put(a4, Long.valueOf(4));

        TreeI<Long> tree2 = TreeFactory.createTree(data2);
        assertEquals(Long.valueOf(3), tree2.getRoot().getContent());

        Collection<Future<TreeI<Long>>> c = new ArrayList<>();
        c.add(new NoFuture<>(tree));
        c.add(new NoFuture<>(tree2));
        assertEquals(2, c.size());

        TreeI<Long> merged = null;
        try {
            merged = TreeFactory.mergeTrees(c);
        } catch (InterruptedException | ExecutionException ex) {
            fail(ex.getMessage());
        }

        assertNotNull(merged);
        assertEquals("a1", merged.getRoot().getAttribute().getValue());
        assertEquals(3, merged.getNodes().size());

        assertEquals(Long.valueOf(4), merged.getRoot().getContent());
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
