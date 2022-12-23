package de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import de.cebitec.mgx.gui.charts.basic.util.SVGChartPanel;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.TimeEvalJobWizardDescriptor;
import java.awt.Color;
import java.awt.Dialog;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComponent;
import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = PipelineComparisonI.class)
public class PCTimeEvaluationViewer extends EvaluationViewerI implements PipelineComparisonI {

    public enum StepSize {
        HOURS, MINUTES, SECONDS;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private SVGChartPanel cPanel = null;
    private List<JobI> jobs;
    private PCTimeEvaluationViewCustomizer cust = null;
    private JFreeChart chart = null;
    private CategoryDataset dataset;

    public PCTimeEvaluationViewer() {
        //deactivate glossy effect
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultShadowsVisible(false);
        XYBarRenderer.setDefaultShadowsVisible(false);
    }

    @Override
    public JComponent getComponent() {
        if (jobs == null) {
            return null;
        }
        if (cPanel == null) {
            evaluate();
        }
        return cPanel;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }

    @Override
    public String getName() {
        return "Runtime comparison";
    }

    @Override
    public void evaluate() {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        int stepSize = 1;
        String yAxisLabel = "";
        switch (cust.getCurrentStepSize()) {
            case HOURS:
                stepSize = 3_600_000;
                yAxisLabel = "hours";
                break;
            case MINUTES:
                stepSize = 60_000;
                yAxisLabel = "minutes";
                break;
            case SECONDS:
                stepSize = 1_000;
                yAxisLabel = "seconds";
        }

        //
        // sort by runtime, descending
        //
        Collections.sort(jobs, new Comparator<JobI>() {
            @Override
            public int compare(JobI o1, JobI o2) {
                double r1 = o1.getFinishDate().getTime() - o1.getStartDate().getTime();
                double r2 = o2.getFinishDate().getTime() - o2.getStartDate().getTime();
                int ret = Double.compare(r2, r1);
                return ret != 0 ? ret : o1.getTool().getName().compareTo(o2.getTool().getName());
            }
        });

        for (JobI job : jobs) {
            double runtime = (job.getFinishDate().getTime() - job.getStartDate().getTime()) / (double) stepSize;
            data.addValue(runtime, "Time evaluation", job.getTool().getName());
        }
        dataset = data;

        String xAxisLabel = "";

        chart = ChartFactory.createBarChart(getName(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.HORIZONTAL, true, true, false);

        chart.removeLegend();
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setAntiAlias(true);
        cPanel = new SVGChartPanel(chart);
        CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);

        BarRenderer br = (BarRenderer) plot.getRenderer();
        br.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("<html>Group: {0} <br> Attribute: {1} <br> " + yAxisLabel + ": {2}</html>", NumberFormat.getInstance()));
        br.setMaximumBarWidth(.1); // set maximum width to 10% of chart

        // x axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(FastMath.PI / 6));

        // y axis
        final NumberAxis rangeAxis;
        final TickUnitSource tus;
        rangeAxis = (NumberAxis) plot.getRangeAxis();
        tus = NumberAxis.createIntegerTickUnits();
        rangeAxis.setStandardTickUnits(tus);
        plot.setRangeAxis(rangeAxis);

        // colors
        plot.getRenderer().setSeriesPaint(0, Color.decode("#1d72aa"));
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new PCTimeEvaluationViewCustomizer();
        }
        return cust;
    }

    @Override
    public void selectJobs(SeqRunI seqrun) {
        try {
            TimeEvalJobWizardDescriptor jobWizard = new TimeEvalJobWizardDescriptor(seqrun);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                cPanel = null;
                jobs = jobWizard.getJobs();
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            cPanel = null;
            jobs = null;
        }

    }
}
