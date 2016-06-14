package de.cebitec.mgx.gui.charts.basic;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.common.visualization.CategoricalViewerI;
import de.cebitec.mgx.common.visualization.NumericalViewerI;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.customizer.XYPlotCustomizer;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.openide.util.lookup.ServiceProvider;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.analysis.function.Log10;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.Exceptions;

/**
 *
 * @author pblumenk
 */
@ServiceProvider(service = ViewerI.class)
public class VolcanoPlot extends CategoricalViewerI<Long> {

    private ChartPanel cPanel = null;
    private XYPlotCustomizer customizer = null;
    private JFreeChart chart = null;

    private final MannWhitneyUTest mwuTest = new MannWhitneyUTest();
    private final Log10 logBase10 = new Log10();
    private final double logBase10Exp2 = logBase10.value(2);

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return JFreeChartUtil.getImageExporter(chart);
    }

    @Override
    public String getName() {
        return "Volcano Plot";
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroupI, DistributionI<Long>>> dists) {
        Collection<ReplicateGroupI> repGroup = VGroupManager.getInstance().getReplicateGroups();
        Iterator<ReplicateGroupI> it = repGroup.iterator();
        ReplicateGroupI groupA = it.next();
        ReplicateGroupI groupB = it.next();
        int groupARepCount = groupA.getReplicates().size();
        int groupBRepCount = groupB.getReplicates().size();
        DistributionI<Double> groupAMean = groupA.getMeanDistribution();
        DistributionI<Double> groupBMean = groupB.getMeanDistribution();
        Set<AttributeI> unusedAttributes = new HashSet<>();
        unusedAttributes.addAll(groupBMean.keySet());

        double foldChangeThreshold = 1;

        XYSeries lowFoldChange = new XYSeries("low fold change");
        XYSeries highFoldChange = new XYSeries("high fold change");
        try {
            for (AttributeI attr : groupAMean.keySet()) {
                double[] groupASet = new double[groupARepCount];
                double[] groupBSet = new double[groupBRepCount];
                int i = 0;
                for (ReplicateI rep : groupA.getReplicates()) {
                    Long count = rep.getDistribution().get(attr);
                    groupASet[i] = (count != null) ? count : 0;
                    i++;
                }
                i = 0;
                for (ReplicateI rep : groupB.getReplicates()) {
                    Long count = rep.getDistribution().get(attr);
                    groupBSet[i] = (count != null) ? count : 0;
                    i++;
                }
                double pValue = -logBase10.value(mwuTest.mannWhitneyUTest(groupASet, groupBSet));
                Double meanA = groupAMean.get(attr);
                if (meanA == null) {
                    meanA = 1.;
                }
                Double meanB = groupBMean.get(attr);
                if (meanB == null) {
                    meanB = 1.;
                }
                double foldChangeLog2 = logBase10.value(meanA / meanB) / logBase10Exp2;
                if (foldChangeLog2 < foldChangeThreshold && foldChangeLog2 > -foldChangeThreshold) {
                    lowFoldChange.add(foldChangeLog2, pValue);
                } else {
                    highFoldChange.add(foldChangeLog2, pValue);
                }
                unusedAttributes.remove(attr);
            }

            for (AttributeI attr : unusedAttributes) {
                double[] groupASet = new double[groupARepCount];
                double[] groupBSet = new double[groupBRepCount];
                Arrays.fill(groupASet, 0);
                int i = 0;
                for (ReplicateI rep : groupB.getReplicates()) {
                    Long count = rep.getDistribution().get(attr);
                    groupBSet[i] = (count != null) ? count : 0;
                    i++;
                }
                double pValue = -logBase10.value(mwuTest.mannWhitneyUTest(groupASet, groupBSet));
                Double meanA = groupAMean.get(attr);
                if (meanA == null) {
                    meanA = 1.;
                }
                Double meanB = groupBMean.get(attr);
                if (meanB == null) {
                    meanB = 1.;
                }
                double foldChangeLog2 = logBase10.value(meanA / meanB) / logBase10Exp2;
                if (foldChangeLog2 < foldChangeThreshold && foldChangeLog2 > -foldChangeThreshold) {
                    lowFoldChange.add(foldChangeLog2, pValue);
                } else {
                    highFoldChange.add(foldChangeLog2, pValue);
                }
            }
        } catch (ConflictingJobsException ex) {
            Exceptions.printStackTrace(ex);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(lowFoldChange);
        dataset.addSeries(highFoldChange);
        String xAxis = "log2 fold change";
        String yAxis = "-log10 p-value";
        chart = ChartFactory.createScatterPlot("Volcano Plot", xAxis, yAxis, dataset);
        chart.setBorderPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        cPanel = new ChartPanel(chart);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 2);

        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabel(xAxis);
        AttributedString domainAxisString = new AttributedString(xAxis);
        domainAxis.setAttributedLabel(domainAxisString);
        Font font = new Font(Font.SANS_SERIF, Font.ITALIC, 14);
        domainAxisString.addAttributes(font.getAttributes(), 0, xAxis.length());
        domainAxisString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 5);
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        long replicatesCount = 0;
        for (ReplicateGroupI rg : VGroupManager.getInstance().getReplicateGroups()) {
            replicatesCount += rg.getReplicates().size();
        }

        return super.canHandle(valueType)
                && VGroupManager.getInstance().getReplicateGroups().size() == 2
                && VGroupManager.getInstance().getAllVisualizationGroups().size() == replicatesCount;
    }
}
