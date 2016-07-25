package de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectJobsWizardDescriptor;
import java.awt.Dialog;
import java.util.Collection;
import javax.swing.JComponent;
import org.jdesktop.swingx.JXTable;
import org.jfree.chart.JFreeChart;
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
public class PCPerformanceMetricsViewer extends EvaluationViewerI implements PipelineComparisonI {

    private SeqRunI currentSeqrun;
    private AttributeTypeI usedAttributeType;
    private JXTable table = null;
    private Collection<JobI> jobs;
    private PCPerformanceMetricsViewCustomizer cust = null;
    private JFreeChart chart = null;
    private CategoryDataset dataset;

    @Override
    public JComponent getComponent() {
        if (jobs == null || usedAttributeType == null) {
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
        return "Performance metrics";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return true;
    }

    @Override
    public void evaluate() {
        
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new PCPerformanceMetricsViewCustomizer();
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
            jobs = null;
            usedAttributeType = null;
            table = null;
        }

    }
}
