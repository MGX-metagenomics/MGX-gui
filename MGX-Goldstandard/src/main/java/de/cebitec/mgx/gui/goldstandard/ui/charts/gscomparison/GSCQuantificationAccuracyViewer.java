package de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison.PCDistanceViewer;
import de.cebitec.mgx.gui.goldstandard.util.JobUtils;
import de.cebitec.mgx.gui.goldstandard.util.Vector;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWithGSWizardDescriptor;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author patrick
 */
@ServiceProvider(service = GSComparisonI.class)
public class GSCQuantificationAccuracyViewer extends EvaluationViewerI implements GSComparisonI {

    private SeqRunI currentSeqrun;
    private List<JobI> currentJobs;

    private JFreeChart chart = null;
    private ChartPanel cPanel = null;

    private GSCQuantificationAccuracyViewCustomizer cust = null;

    private JobI gsJob;
    private AttributeTypeI attrType;

    @Override
    public JComponent getComponent() {
        if (gsJob == null || attrType == null || currentSeqrun == null || currentJobs == null) {
            return null;
        }
        if (cPanel == null) {
            evaluate();
        }

        return cPanel;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null;
    }

    @Override
    public String getName() {
        return "Quantification Accuracy";
    }

    @Override
    public void evaluate() {
        double[][] values = new double[2][];
        double correlation = 0;

        try {
            List<JobI> jobs = new ArrayList<>();
            jobs.add(gsJob);
            jobs.add(currentJobs.get(0));

            Vector[] vectors = PCDistanceViewer.calcAttributeVectors(jobs, attrType, false);
            values[0] = vectors[0].asArray();
            values[1] = vectors[1].asArray();

            correlation = new PearsonsCorrelation().correlation(vectors[1].asArray(), vectors[0].asArray());
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            tidyUp();
            return;
        }

        XYPlot plot = new XYPlot();

        XYSeries series1 = new XYSeries("Points");
        for (int i = 0; i < values[0].length; i++) {
            series1.add(values[1][i], values[0][i]);
        }
        XYDataset collection1 = new XYSeriesCollection(series1);
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(false, true);   // Shapes only
        renderer1.setSeriesShape(0, new Rectangle(2, 2));
        ValueAxis domain1 = new NumberAxis(JobUtils.jobToString(currentJobs.get(0)));
        ValueAxis range1 = new NumberAxis(JobUtils.jobToString(gsJob));

        plot.setDataset(0, collection1);
        plot.setRenderer(0, renderer1);
        plot.setDomainAxis(0, domain1);
        plot.setRangeAxis(0, range1);

        plot.mapDatasetToDomainAxis(0, 0);
        plot.mapDatasetToRangeAxis(0, 0);

        XYSeries series2 = new XYSeries("Line");
        series2.add(0, 0);
        double val = Math.max(series1.getMaxX(), series1.getMaxY());
        series2.add(val, val);
        XYDataset collection2 = new XYSeriesCollection(series2);
        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false);   // Lines only

        plot.setDataset(1, collection2);
        plot.setRenderer(1, renderer2);

        plot.mapDatasetToDomainAxis(1, 0);
        plot.mapDatasetToRangeAxis(1, 0);

        final XYTextAnnotation r = new XYTextAnnotation(String.format("R: %1$.5f", correlation), series1.getMaxX() * 0.10, series1.getMaxY() - 20);
        r.setFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.addAnnotation(r);

        chart = new JFreeChart(plot);
        chart.removeLegend();
        cPanel = new ChartPanel(chart);

    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new GSCQuantificationAccuracyViewCustomizer();
        }

        return cust;
    }

    @Override
    public void selectJobs(final SeqRunI seqrun) {
        tidyUp();
        currentSeqrun = seqrun;
        try {
            final SelectSingleJobWithGSWizardDescriptor jobWizard = new SelectSingleJobWithGSWizardDescriptor(seqrun, false, 1);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                currentJobs = jobWizard.getJobs();
                gsJob = jobWizard.getGoldstandard();
                attrType = jobWizard.getAttributeType();
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            tidyUp();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        cust.dispose();
        cust = null;
        tidyUp();
    }

    private void tidyUp() {
        currentJobs = null;
        gsJob = null;
        attrType = null;
        cPanel = null;
        chart = null;
    }

}
