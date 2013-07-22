package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class ExcludeFilterTest {

    public ExcludeFilterTest() {
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
    public void testFilter() {
        return; // disable test
        System.out.println("filterSingleTree");
        MGXMaster m = TestMaster.get2();
        // ncbi_class
        Tree<Long> atb = m.Attribute().getHierarchy(5, 19);
        //Tree<Long> haw = m.Attribute().getHierarchy(5, 20);

        Collection<Tree<Long>> data = new HashSet<>();
        //data.add(haw);
        data.add(atb);
        Tree<Long> tree = TreeFactory.mergeTrees(data);
        assertNotNull(tree);

        AttributeType ncbiClass = null;
        Set<Attribute> filterEntries = new HashSet<>();
        for (Node<Long> node : tree.getNodes()) {
            if (node.getAttribute().getAttributeType().getName().equals("NCBI_CLASS")) {
                ncbiClass = node.getAttribute().getAttributeType();
            }
            if (node.getAttribute().getValue().equals("Bacteria")) {
                filterEntries.add(node.getAttribute());
            }
            if (node.getAttribute().getValue().equals("Eukaryota")) {
                filterEntries.add(node.getAttribute());
            }
        }

        assertNotNull(ncbiClass);
        assertEquals(2, filterEntries.size());


        Set<Attribute> blackList = new HashSet<>();


        for (Node<Long> node : tree.getNodes()) {
            if (ncbiClass.equals(node.getAttribute().getAttributeType())) {
                for (Node<Long> pathNode : node.getPath()) {
                    if (filterEntries.contains(pathNode.getAttribute())) {
                        blackList.add(node.getAttribute());
                        break;
                    }
                }
            }
        }

        assertEquals(78, blackList.size());


        Distribution atbDist = m.Attribute().getDistribution(5, 19);
        assertEquals(86, atbDist.size());
        ExcludeFilter filter = new ExcludeFilter(blackList);
        filter.filterDist(atbDist);
        assertEquals(8, atbDist.size());

//        Distribution hawDist = m.Attribute().getDistribution(5, 20);
//        assertEquals(93, hawDist.size());
//        filter.filterDist(hawDist);
//        assertEquals(79, hawDist.size());


        for (Node<Long> node : tree.getNodes()) {
            if (node.getAttribute().getAttributeType().getName().equals(ncbiClass.getName())) {
                if (!blackList.contains(node.getAttribute())) {
                    System.err.println("   " + node.getAttribute().getValue());
                }
            }
        }

        //TreeFactory.dumpNode(tree.getRoot(), 2);

    }
}