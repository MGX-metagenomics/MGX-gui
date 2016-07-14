package de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.TimeEvalJobWizardDescriptor;
import java.awt.Color;
import java.awt.Dialog;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.JComponent;
import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
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
public class PCTimeEvaluationViewer extends EvaluationViewerI<DistributionI<Long>> implements PipelineComparisonI{

    public enum StepSize {
        HOURS, MINUTES, SECONDS;
        
        @Override        
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private SeqRunI currentSeqrun;
    private ChartPanel cPanel = null;
    private List<JobI> jobs;
    private PCTimeEvaluationViewCustomizer cust = null;
    private JFreeChart chart = null;
    private CategoryDataset dataset;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return null;
    }

    @Override
    public String getName() {
        return "Time evaluation";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return true;
    }

    @Override
    public Class getInputType() {
        return JobI.class;
    }

    @Override
    public void show(List<DistributionI<Long>> dist) {
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
        for (JobI job : jobs) {
            double runtime = (job.getFinishDate().getTime() - job.getStartDate().getTime()) / (double)stepSize;
            data.addValue(runtime, "Time evaluation", job.getTool().getName());
        }
        dataset = data;
        
        String xAxisLabel = "";

        chart = ChartFactory.createBarChart(getTitle(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.HORIZONTAL, true, true, false);

        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setAntiAlias(true);
        cPanel = new ChartPanel(chart);
        CategoryPlot plot = chart.getCategoryPlot();

//        plot.setFixedLegendItems(JFreeChartUtil.createLegend(data));
        plot.setBackgroundPaint(Color.WHITE);

        BarRenderer br = (BarRenderer) plot.getRenderer();
//        br.setItemMargin(customizer.getItemMargin());
        br.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("<html>Group: {0} <br> Attribute: {1} <br> " + yAxisLabel + ": {2}</html>", NumberFormat.getInstance()));
        br.setMaximumBarWidth(.1); // set maximum width to 10% of chart

        // x axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        //      domainAxis.setCategoryMargin(customizer.getCategoryMargin());
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(FastMath.PI / 6));

        // y axis
        final NumberAxis rangeAxis;
        final TickUnitSource tus;
//        if (getCustomizer().logY()) {
//            rangeAxis = new LogarithmicAxis("log(" + yAxisLabel + ")");
//            ((LogarithmicAxis) rangeAxis).setStrictValuesFlag(false);
//            tus = LogAxis.createLogTickUnits(Locale.getDefault());
//
//        } else {
        rangeAxis = (NumberAxis) plot.getRangeAxis();
//            if (getCustomizer().useFractions()) {
//                tus = NumberAxis.createStandardTickUnits();
//            } else {
        tus = NumberAxis.createIntegerTickUnits();
//            }
//        }
        rangeAxis.setStandardTickUnits(tus);
//        if (dataset instanceof SlidingCategoryDataset) {
//            SlidingCategoryDataset scd = (SlidingCategoryDataset) dataset;
//            rangeAxis.setAutoRange(false);
//            rangeAxis.setRange(0, scd.getMaxY());
//        }
        plot.setRangeAxis(rangeAxis);

        // colors
//        int i = 0;
        plot.getRenderer().setSeriesPaint(0, Color.decode("#1d72aa"));
//        for (Pair<VisualizationGroupI, DistributionI<Double>> groupDistribution : data) {
//            renderer.setSeriesPaint(i++, groupDistribution.getFirst().getColor());
//        }
    }

    @Override
    public JComponent getCustomizer() {
        if (cust == null) {
            cust = new PCTimeEvaluationViewCustomizer();            
        }
        return cust;
    }

    @Override
    public void start(SeqRunI seqrun) {
        try {
            TimeEvalJobWizardDescriptor jobWizard = new TimeEvalJobWizardDescriptor(seqrun);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(jobWizard);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = jobWizard.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                jobs = jobWizard.getJobs();
                show(null);
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

    }
}
