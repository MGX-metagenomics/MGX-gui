package de.cebitec.mgx.gui.tableview;

import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class TreeTableView extends ViewerI<Tree<Long>> {

    private JXTable table;
    private TableViewCustomizer cust = null;
    private Set<Attribute> excludeList = null;

    @Override
    public JComponent getComponent() {
        return table;
    }

    @Override
    public String getName() {
        return "Krona Table View";
    }

    @Override
    public boolean canHandle(AttributeType valueType) {
        return valueType.getStructure() == AttributeType.STRUCTURE_HIERARCHICAL
                && VGroupManager.getInstance().getActiveGroups().size() == 1;
    }

    @Override
    public Class getInputType() {
        return Tree.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Tree<Long>>> dists) {

        Tree<Long> tree = dists.get(0).getSecond();

        // convert data to KRONA style
        tree = convertTree(tree);

        excludeList = getCustomizer().createBlackList(tree, getCustomizer().getFilterEntries());

        /*
         *  handle unaligned trees, where equal depth does not mean equal
         *  rank. caused by constructs like "suborder", which is _sometimes_
         *  present between order and whatever comes below.
         * 
         *  instead, rely on attribute types as defined by deepest node and
         *  align everything else to it, filling gaps if needed.
         * 
         */

        // setup column names
        AttributeType[] longestPath = getLongestPath(tree);
        String[] columns = new String[1 + longestPath.length];
        int i = 0;
        columns[i++] = "Count"; // first column
        for (AttributeType at : longestPath) {
            columns[i++] = at.getName();
        }

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Long.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cust.setModel(model); // for tsv export

        setupRowData(model, longestPath, tree.getRoot());

        table = new JXTable(model);
        table.setFillsViewportHeight(true);
        for (TableColumn tc : table.getColumns()) {
            if (0 != tc.getModelIndex()) {
                tc.setMinWidth(20);
                tc.setPreferredWidth(40);
                tc.setWidth(40);
            }
        }
    }

    @Override
    public TableViewCustomizer getCustomizer() {
        if (cust == null) {
            cust = new TableViewCustomizer();
        }
        cust.setAttributeType(getAttributeType());
        return cust;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null; // no image to export here
    }

    private AttributeType[] getLongestPath(Tree<Long> tree) {
        /*
         * actually, this is still wrong. we don't need the longest path, but
         * the most complete one. the current code will fail when several paths
         * have equal length, but different content, e.g.
         * 
         *  phylum -- subphylum -- order -- foo
         *  phylum -- order -- suborder -- foo,
         * 
         *  yielding equal path lengths but different order. a correct solution
         *  would need to merge the paths, giving
         * 
         *  phylum -- subphylum -- order -- suborder -- foo.
         */
        int curDepth = -1;
        Node<Long> deepestNode = null;
        for (Node<Long> node : tree.getNodes()) {
            if (!excludeList.contains(node.getAttribute())) {
                if (node.getDepth() > curDepth) {
                    deepestNode = node;
                    curDepth = node.getDepth();
                }
            }
        }

        AttributeType[] ret = new AttributeType[curDepth + 1];
        int i = 0;
        for (Node<Long> n : deepestNode.getPath()) {
            ret[i++] = n.getAttribute().getAttributeType();
        }
        return ret;
    }

    private void setupRowData(DefaultTableModel model, AttributeType[] aTypes, Node<Long> node) {
        if (excludeList.contains(node.getAttribute())) {
            return;
        }
        Object[] rowData = new Object[1 + aTypes.length];
        int pos = 0;

        // add the nodes content in first column
        rowData[pos++] = node.getContent();

        Node<Long>[] path = node.getPath();

        for (AttributeType at : aTypes) {
            String value = "";
            for (Node<Long> tmp : path) {
                if (tmp.getAttribute().getAttributeType().getName().equals(at.getName())) {
                    value = tmp.getAttribute().getValue();
                    break;
                }
            }
            rowData[pos++] = value;
        }

        model.addRow(rowData);

        // recurse for child nodes
        if (!node.isLeaf()) {
            for (Node<Long> child : node.getChildren()) {
                setupRowData(model, aTypes, child);
            }
        }
    }

    private Tree<Long> convertTree(Tree<Long> tree) {
        
        tree = TreeFactory.clone(tree);
        
        // for KRONA plots, we need each nodes count to be the number
        // of reads most specifically assigned to this node only, excluding
        // reads assigned to a more specific entry.
        // Thus, we iterate over all nodes and subtract the sum of reads assigned
        // to the immediate child nodes.
        Map<Attribute, Long> newContent = new HashMap<>(tree.getNodes().size());
        for (Node<Long> node : tree.getNodes()) {
            Long numPathsEndingHere = node.getContent();
            if (!node.isLeaf()) {
                numPathsEndingHere = numPathsEndingHere - nodeSum(node.getChildren());
            }
            newContent.put(node.getAttribute(), numPathsEndingHere);
        }
        
        // ..and update
        for (Node<Long> node : tree.getNodes()) {
            node.setContent(newContent.get(node.getAttribute()));
        }
        return tree;
    }

    private static long nodeSum(Set<Node<Long>> nodes) {
        int sum = 0;
        for (Node<Long> n : nodes) {
            sum += n.getContent();
        }
        return sum;
    }
}
