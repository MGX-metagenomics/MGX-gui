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
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.charts.basic.customizer.VolcanoPlotCustomizer;
import de.cebitec.mgx.gui.charts.basic.customizer.XYPlotCustomizer;
import de.cebitec.mgx.gui.charts.basic.util.JFreeChartUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.jfree.chart.labels.CustomXYToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
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
    private VolcanoPlotCustomizer customizer = null;
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

        double foldChangeThreshold = customizer.getFoldChangeThreshold();
        double pValueThreshold = -logBase10.value(customizer.getPValue());
        double maxFoldChange = 0;
        double maxPValue = 0;

        XYSeries lowFoldChange = new XYSeries("low fold change", false);
        XYSeries highFoldChange = new XYSeries("high fold change", false);
        List<List<String>> attributes = new ArrayList<>(2);
        attributes.add(new ArrayList<String>(100));
        attributes.add(new ArrayList<String>(100));
        try {
            //all attributes from first replicate group
            for (AttributeI attr : groupAMean.keySet()) {
                double[] groupASet = new double[groupARepCount];        //all values for an attribute in group A
                double[] groupBSet = new double[groupBRepCount];        //all values for an attribute in group B
                int i = 0;
                for (ReplicateI rep : groupA.getReplicates()) {
                    groupASet[i++] = rep.getDistribution().getOrDefault(attr, 0L);
                }
                i = 0;
                for (ReplicateI rep : groupB.getReplicates()) {
                    groupBSet[i++] = rep.getDistribution().getOrDefault(attr, 0L);
                }
                double pValue = mwuTest.mannWhitneyUTest(groupASet, groupBSet);
                double logPValue = -logBase10.value(pValue);
                Double meanA = groupAMean.getOrDefault(attr, 1.);   //Zero values will be replaced by 1 for calculating fold change
                Double meanB = groupBMean.getOrDefault(attr, 1.);
                double foldChange = meanA / meanB;
                double foldChangeLog2 = logBase10.value(foldChange) / logBase10Exp2;
                if (foldChangeLog2 < foldChangeThreshold && foldChangeLog2 > -foldChangeThreshold) {
                    lowFoldChange.add(foldChangeLog2, -logBase10.value(pValue));
                    attributes.get(0).add(attr.getValue());
                } else {
                    highFoldChange.add(foldChangeLog2, -logBase10.value(pValue));
                    attributes.get(1).add(attr.getValue());
                }

                maxPValue = (maxPValue < logPValue) ? logPValue : maxPValue;
                maxFoldChange = (maxFoldChange < Math.abs(foldChangeLog2)) ? Math.abs(foldChangeLog2) : maxFoldChange;

                unusedAttributes.remove(attr);
            }

            //remaining attributes from group B
            for (AttributeI attr : unusedAttributes) {
                double[] groupASet = new double[groupARepCount];
                double[] groupBSet = new double[groupBRepCount];
                Arrays.fill(groupASet, 0);
                int i = 0;
                for (ReplicateI rep : groupB.getReplicates()) {
                    groupBSet[i++] = rep.getDistribution().getOrDefault(attr, 0L);
                }
                double pValue = mwuTest.mannWhitneyUTest(groupASet, groupBSet);
                double logPValue = logBase10.value(pValue);
                Double meanA = groupAMean.getOrDefault(attr, 1.);   //Zero values will be replaced by 1 for calculating fold change
                Double meanB = groupBMean.getOrDefault(attr, 1.);
                double foldChange = meanA / meanB;
                double foldChangeLog2 = logBase10.value(foldChange) / logBase10Exp2;
                if (foldChangeLog2 < foldChangeThreshold && foldChangeLog2 > -foldChangeThreshold) {
                    lowFoldChange.add(foldChangeLog2, -logBase10.value(pValue));
                    attributes.get(0).add(attr.getValue());
                } else {
                    highFoldChange.add(foldChangeLog2, -logBase10.value(pValue));
                    attributes.get(1).add(attr.getValue());
                }

                maxPValue = (maxPValue < logPValue) ? logPValue : maxPValue;
                maxFoldChange = (maxFoldChange < Math.abs(foldChangeLog2)) ? Math.abs(foldChangeLog2) : maxFoldChange;
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
        chart.removeLegend();
        cPanel = new ChartPanel(chart);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        plot.addRangeMarker(new ValueMarker(pValueThreshold, Color.BLACK, new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{10f, 3f}, 0f)));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, (maxPValue < pValueThreshold) ? pValueThreshold * 1.15 : maxPValue * 1.15); 
        AttributedString rangeAxisString = new AttributedString(yAxis);
        rangeAxis.setAttributedLabel(rangeAxisString);
        rangeAxisString.addAttribute(TextAttribute.SIZE, 14, 0, yAxis.length());
        rangeAxisString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 4, 6);
        rangeAxis.setAttributedLabel(rangeAxisString);

        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setRange(-Math.abs(maxFoldChange) * 1.15, Math.abs(maxFoldChange) * 1.15); 
        AttributedString domainAxisString = new AttributedString(xAxis);
        domainAxis.setAttributedLabel(domainAxisString);
        domainAxisString.addAttribute(TextAttribute.SIZE, 14, 0, xAxis.length());
        domainAxisString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 3, 5);
        domainAxis.setAttributedLabel(domainAxisString);
        domainAxisString.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 5);

        AttributeToolTipGenerator tooltipGen = new AttributeToolTipGenerator(attributes);
        plot.getRenderer().setBaseToolTipGenerator(tooltipGen);
    }

    @Override
    public JComponent getCustomizer() {
        if (customizer == null) {
            customizer = new VolcanoPlotCustomizer();
        }

        return customizer;
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

class AttributeToolTipGenerator extends CustomXYToolTipGenerator{
    private final List<List<String>> attributes;
    
    public AttributeToolTipGenerator(List<List<String>> attributes){
        super();
        this.attributes = attributes;
    }

    @Override
    public String generateToolTip(XYDataset data, int series, int item) {
        StringBuilder tooltip = new StringBuilder();
        tooltip.append(String.format("<html><p style='color:#0000ff;'>Attribute: '%s'</p>", attributes.get(series).get(item)));
        tooltip.append(String.format("fold change: '%f'<br/>", data.getX(series, item)));
        tooltip.append(String.format("p value: '%f'<br/>", data.getYValue(series, item)));
        tooltip.append("</html>");
        return tooltip.toString();
    }
    
    
}