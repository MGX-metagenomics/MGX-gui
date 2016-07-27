package de.cebitec.mgx.gui.tableview;

import de.cebitec.mgx.gui.swingutils.DecimalFormatRenderer;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import de.cebitec.mgx.gui.vizfilter.SortOrder;
import de.cebitec.mgx.gui.vizfilter.ToFractionFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class TableViewer extends ViewerI<DistributionI<Long>> {

    private JXTable table;
    private TableViewCustomizer cust = null;

    @Override
    public JComponent getComponent() {
        return table;
    }

    @Override
    public String getName() {
        return "Table View";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return true;
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public void show(final List<Pair<VisualizationGroupI, DistributionI<Long>>> in) {

        //
        // check whether assigned reads should be displayed as counts or fractions
        //
        List<Pair<VisualizationGroupI, DistributionI<Double>>> data = null;
        if (getCustomizer().useFractions()) {
            VisFilterI<DistributionI<Long>, DistributionI<Double>> fracFilter = new ToFractionFilter();
            data = fracFilter.filter(in);
        } else {
            data = new LongToDouble().filter(in);
        }

        //
        // exclude filter must be applied _AFTER_ converting to fractions
        //
        data = getCustomizer().filter(data);

        Set<AttributeI> allAttrs = new HashSet<>();
        int numColumns = data.size() + 1;
        String[] columns = new String[numColumns];
        int i = 0;
        columns[i++] = getAttributeType().getName(); // first column
        for (Pair<VisualizationGroupI, DistributionI<Double>> p : data) {
            columns[i++] = p.getFirst().getDisplayName();
            allAttrs.addAll(p.getSecond().keySet());
        }

        SortOrder<Double> order = new SortOrder<>(getAttributeType(), SortOrder.DESCENDING);
        data = order.filter(data);

        final boolean useFractions = getCustomizer().useFractions();

        DefaultTableModel model = new DefaultTableModel(columns, 0) {

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    default:
                        return useFractions ? Double.class : Long.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (AttributeI a : allAttrs) {
            Object[] rowData = new Object[numColumns];
            rowData[0] = a.getValue();
            int col = 1;
            for (Pair<VisualizationGroupI, DistributionI<Double>> p : data) {
                DistributionI<Double> d = p.getSecond();
                rowData[col++] = d.containsKey(a)
                        ? getCustomizer().useFractions() ? d.get(a) : d.get(a).longValue()
                        : 0;
            }
            model.addRow(rowData);
        }

        cust.setModel(model); // for tsv export

        table = new JXTable(model);
        table.setDefaultRenderer(Double.class, new DecimalFormatRenderer());
        table.setFillsViewportHeight(true);
        for (TableColumn tc : table.getColumns()) {
            if (0 != tc.getModelIndex()) {
                tc.setMinWidth(20);
                tc.setPreferredWidth(40);
                tc.setWidth(40);
            }
        }
        table.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});
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
}
