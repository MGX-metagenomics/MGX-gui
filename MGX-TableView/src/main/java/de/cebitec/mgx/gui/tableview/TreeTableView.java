package de.cebitec.mgx.gui.tableview;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.common.TreeFactory;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.common.visualization.ViewerI;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class TreeTableView extends ViewerI<TreeI<Long>> {

    private JXTable table;
    private TableViewCustomizer cust = null;

    @Override
    public JComponent getComponent() {
        return table;
    }

    @Override
    public String getName() {
        return "Krona Table View";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return valueType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL
                && VGroupManager.getInstance().getActiveVisualizationGroups().size() == 1;
    }

    @Override
    public Class getInputType() {
        return TreeI.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, TreeI<Long>>> dists) {

        final TreeI<Long> tree = dists.get(0).getSecond();

        // convert data to KRONA style
        TreeI<Long> kronaTree = TreeFactory.createKRONATree(tree);
        kronaTree = TreeFactory.filter(kronaTree, getCustomizer().getFilterEntries());

        /*
         *  handle unaligned trees, where equal depth does not mean equal
         *  rank. caused by constructs like "suborder", which is _sometimes_
         *  present between order and whatever comes below.
         * 
         *  instead, rely on attribute types as defined by deepest node and
         *  align everything else to it, filling gaps if needed.
         * 
         */
        // setup column names, based on unfiltered tree
        AttributeTypeI[] longestPath = TreeFactory.getLongestPath(tree);
        String[] columns = new String[1 + longestPath.length];
        int i = 0;
        columns[i++] = "Count"; // first column
        for (AttributeTypeI at : longestPath) {
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
        setupRowData(model, longestPath, kronaTree.getRoot());
        cust.setModel(model); // for tsv export

        table = new JXTable(model);
        table.setFillsViewportHeight(true);
        for (TableColumn tc : table.getColumns()) {
            if (0 != tc.getModelIndex()) {
                tc.setMinWidth(20);
                tc.setPreferredWidth(40);
                tc.setWidth(40);
            }
        }
        table.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});
        table.setSortable(false);
    }

    @Override
    public TableViewCustomizer getCustomizer() {
        if (cust == null) {
            cust = new TableViewCustomizer();
            cust.kronaMode();
        }
        cust.setAttributeType(getAttributeType());
        return cust;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null; // no image to export here
    }

    private void setupRowData(DefaultTableModel model, AttributeTypeI[] aTypes, NodeI<Long> node) {
        Object[] rowData = new Object[1 + aTypes.length];
        int pos = 0;

        // add the nodes content in first column
        rowData[pos++] = node.getContent();

        NodeI<Long>[] path = node.getPath();

        for (AttributeTypeI at : aTypes) {
            String value = "";
            for (NodeI<Long> tmp : path) {
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
            for (NodeI<Long> child : node.getChildren()) {
                setupRowData(model, aTypes, child);
            }
        }
    }
}
