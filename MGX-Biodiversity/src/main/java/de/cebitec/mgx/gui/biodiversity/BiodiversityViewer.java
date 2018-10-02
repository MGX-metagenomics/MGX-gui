/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.biodiversity;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.common.visualization.CategoricalViewerI;
import de.cebitec.mgx.common.visualization.CustomizableI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.biodiversity.statistic.Statistic;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.ACE;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Chao1;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Shannon;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Simpson;
import de.cebitec.mgx.gui.swingutils.DecimalFormatRenderer;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class BiodiversityViewer extends CategoricalViewerI<Long> implements CustomizableI {

    private JXTable table;
    private DefaultTableModel model;
    private final Statistic[] stats;
    private BiodiversityCustomizer cust = null;

    //
    private final static String NOT_AVAILABLE = "N/A";

    public BiodiversityViewer() {
        this.stats = new Statistic[]{new ACE(), new Chao1(), new Shannon(), new Simpson()};
    }

    @Override
    public String getName() {
        return "Biodiversity Indices";
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> dists) {

        String[] headers = new String[dists.size() + 1];
        int i = 0;
        headers[i++] = "Index";
        for (Pair<VisualizationGroupI, DistributionI<Long>> p : dists) {
            headers[i++] = p.getFirst().getDisplayName();
        }

        model = new DefaultTableModel(headers, stats.length) {

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    default:
                        return Double.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // first column with index names
        for (i = 0; i < stats.length; i++) {
            model.setValueAt(stats[i].getName(), i, 0);
        }

        int col = 1;
        for (Pair<VisualizationGroupI, DistributionI<Long>> p : dists) {
            for (i = 0; i < stats.length; i++) {
                model.setValueAt(stats[i].measure(p.getSecond()), i, col);
            }
            col++;
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

        // sorter
        TableRowSorter<TableModel> sorter = new TableRowSorter<>();
        table.setRowSorter(sorter);
        sorter.setModel(model);
    }

    @Override
    public JComponent getComponent() {
        return table;
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new BiodiversityCustomizer();
        }
        return cust;
    }

}
