package de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.util.JobUtils;
import de.cebitec.mgx.gui.goldstandard.util.Vector;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectJobsWizardDescriptor;
import java.awt.Dialog;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = PipelineComparisonI.class)
public class PCDistanceViewer extends EvaluationViewerI implements PipelineComparisonI {

    private SeqRunI currentSeqrun;
    private AttributeTypeI usedAttributeType;
    private JXTable table;
    private List<JobI> jobs;
    private PCDistanceViewCustomizer cust = null;    
    private CategoryDataset dataset;

    public PCDistanceViewer() {
        //deactivate glossy effect
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultShadowsVisible(false);
    }

    @Override
    public JComponent getComponent() {
        if (usedAttributeType == null || jobs == null) {
            return null;
        }
        if (table == null) {
            evaluate();
        }

        return table;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null;
    }

    @Override
    public String getName() {
        return "Distance between jobs";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return true;
    }

    @Override
    public void evaluate() {
        Vector[] vectors;
        try {
            Map<AttributeI, long[]> attributes = null;
            int i = 0;
            for (JobI job : jobs) {
                DistributionI<Long> dist = job.getMaster().Attribute().getDistribution(usedAttributeType, job);
                if (attributes == null) {
                    attributes = new HashMap<>((int) (dist.size() * 1.3));
                }
                for (Entry<AttributeI, Long> attr : dist.entrySet()) {
                    if (attributes.containsKey(attr.getKey())) {
                        attributes.get(attr.getKey())[i] = attr.getValue();
                    } else {
                        long[] array = new long[jobs.size()];
                        Arrays.fill(array, 0);
                        array[i] = attr.getValue();
                        attributes.put(attr.getKey(), array);
                    }
                }
                i++;
            }

            vectors = new Vector[jobs.size()];
            for (i = 0; i < jobs.size(); i++) {
                vectors[i] = new Vector(attributes.size());
            }
            for (AttributeI key : attributes.keySet()) {
                long[] values = attributes.get(key);
                for (i = 0; i < values.length; i++) {
                    vectors[i].add(values[i]);
                }
            }

            if (((PCDistanceViewCustomizer) getCustomizer()).normalizeVectors()) {
                for (i = 0; i < vectors.length; i++) {
                    vectors[i] = vectors[i].normalize();
                }
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        String[] columns = new String[jobs.size() + 1];
        columns[0] = "";
        int i = 1;
        for (JobI job : jobs) {
            columns[i++] = JobUtils.jobToString(job);
        }
        DefaultTableModel model = new DefaultTableModel(columns, 0) {

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }

            @Override

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        double[][] distances = new double[jobs.size()][jobs.size()];

        for (i = 0; i < distances.length - 1; i++) {
            for (int j = i + 1; j < distances.length; j++) {
                distances[i][j] = vectors[i].euclideanDistance(vectors[j]);
                distances[j][i] = distances[i][j];
            }
        }

        String numberFormat;
        if (((PCDistanceViewCustomizer) getCustomizer()).normalizeVectors()) {
            numberFormat = "%.5f";
        } else {
            numberFormat = "%.2f";
        }

        for (i = 0; i < jobs.size(); i++) {
            Object[] rowData = new Object[columns.length];
            rowData[0] = columns[i + 1];
            for (int j = 0; j < columns.length - 1; j++) {
                if (i == j) {
                    rowData[j + 1] = "/";
                } else if (j < i) {
                    rowData[j + 1] = "";
                } else {
                    rowData[j + 1] = String.format(numberFormat, distances[i][j]);
                }
            }
            model.addRow(rowData);
        }

//        cust.setModel(model); // for tsv export
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
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new PCDistanceViewCustomizer();
        }
        return cust;
    }

    @Override
    public void selectJobs(SeqRunI seqrun) {
        try {
            SelectJobsWizardDescriptor jobWizard = new SelectJobsWizardDescriptor(seqrun, false);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                table = null;
                jobs = jobWizard.getJobs();
                usedAttributeType = jobWizard.getAttributeType();
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            table = null;
            jobs = null;
            usedAttributeType = null;
        }

    }
}
