package de.cebitec.mgx.gui.biodiversity;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.ACE;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Chao1;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Shannon;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.ShannonEvenness;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Simpson;
import de.cebitec.mgx.gui.swingutils.DecimalFormatRenderer;
import de.cebitec.mgx.gui.viewer.api.CategoricalViewerI;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
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
import de.cebitec.mgx.gui.biodiversity.statistic.StatisticI;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Margalef;
import de.cebitec.mgx.gui.biodiversity.statistic.impl.Menhinick;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class BiodiversityViewer extends CategoricalViewerI<Long> implements CustomizableI {

    private JXTable table;
    private DefaultTableModel model;
    private final StatisticI[] stats;
    private BiodiversityCustomizer cust = null;

    public BiodiversityViewer() {
        this.stats = new StatisticI[]{new ACE(), new Chao1(),
            new Margalef(), new Menhinick(),
            new Shannon(), new ShannonEvenness(), new Simpson()};
    }

    @Override
    public String getName() {
        return "Biodiversity Indices";
    }

    @Override
    public Class<?> getInputType() {
        return DistributionI.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void show(List<Pair<GroupI, DistributionI<Long>>> dists) {

        String[] headers = new String[dists.size() + 1];
        int i = 0;
        headers[i++] = "Index";
        for (Pair<GroupI, DistributionI<Long>> p : dists) {
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
        for (Pair<GroupI, DistributionI<Long>> p : dists) {
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
