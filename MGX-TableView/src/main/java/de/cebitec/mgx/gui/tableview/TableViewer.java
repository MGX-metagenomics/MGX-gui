package de.cebitec.mgx.gui.tableview;

import de.cebitec.mgx.gui.attributevisualization.filter.SortOrder;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTable;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class TableViewer extends ViewerI<Distribution> {

    private JTable table;

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
        String[] columns = new String[dists.size() + 1];
        int i = 0;
        columns[i++] = getAttributeType().getName();
        for (Pair<VisualizationGroup, Distribution> p : dists) {
            columns[i++] = p.getFirst().getName();
        }
        
        SortOrder order = new SortOrder(getAttributeType(), SortOrder.DESCENDING);
        dists = order.filter(dists);
 
        String[][] rowData = new String[dists.size()+1][order.getOrder().size()];
        
        int row = 0;
        for (Attribute a : order.getOrder()) {
            int col = 0;
            rowData[row][col++] = a.getValue();
            for (Pair<VisualizationGroup, Distribution> p : dists) {
                Distribution d = p.getSecond();
                rowData[row][col++] = d.containsKey(a) ? d.get(a).toString() : "0";
            }
            row++;
        }

        table = new JTable(rowData, columns);
        table.setFillsViewportHeight(true);
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }
}
