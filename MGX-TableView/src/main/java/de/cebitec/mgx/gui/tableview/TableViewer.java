package de.cebitec.mgx.gui.tableview;

import de.cebitec.mgx.gui.attributevisualization.filter.SortOrder;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.HashSet;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class TableViewer extends ViewerI<Distribution> {

    private JXTable table;
    private TableViewCustomizer cust = new TableViewCustomizer();

    @Override
    public JComponent getComponent() {
        return table;
    }

    @Override
    public String getName() {
        return "Table View";
    }

    @Override
    public boolean canHandle(AttributeType valueType) {
        return true;
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
        HashSet<Attribute> allAttrs = new HashSet<>();
        int numColumns = dists.size() + 1;
        String[] columns = new String[numColumns];
        int i = 0;
        columns[i++] = getAttributeType().getName();
        for (Pair<VisualizationGroup, Distribution> p : dists) {
            columns[i++] = p.getFirst().getName();
            allAttrs.addAll(p.getSecond().keySet());
        }

        SortOrder order = new SortOrder(getAttributeType(), SortOrder.DESCENDING);
        dists = order.filter(dists);

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    default:
                        return Long.class;
                }
            }
        };
        cust.setModel(model);
        
        for (Attribute a : allAttrs) {
            Object[] rowData = new Object[numColumns];
            rowData[0] = a.getValue();
            int col = 1;
            for (Pair<VisualizationGroup, Distribution> p : dists) {
                Distribution d = p.getSecond();
                rowData[col++] = d.containsKey(a) ? d.get(a).longValue() : 0;
            }
            model.addRow(rowData);
        }

        table = new JXTable(model);
        table.setFillsViewportHeight(true);
    }

    @Override
    public JComponent getCustomizer() {
        return cust;
    }
}
