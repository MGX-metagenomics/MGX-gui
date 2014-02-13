//package de.cebitec.mgx.gui.charts3d;
//
//import de.cebitec.mgx.gui.attributevisualization.filter.SortOrder;
//import de.cebitec.mgx.gui.attributevisualization.viewer.CategoricalViewerI;
//import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
//import de.cebitec.mgx.gui.datamodel.Attribute;
//import de.cebitec.mgx.gui.datamodel.AttributeType;
//import de.cebitec.mgx.gui.datamodel.misc.Distribution;
//import de.cebitec.mgx.gui.datamodel.misc.Pair;
//import de.cebitec.mgx.gui.groups.ImageExporterI;
//import de.cebitec.mgx.gui.groups.VisualizationGroup;
//import java.awt.Font;
//import java.awt.Graphics;
//import java.util.ArrayList;
//import java.util.List;
//import javax.swing.JComponent;
//import jogamp.newt.driver.awt.AWTCanvas;
//import org.jzy3d.chart.AWTChart;
//import org.jzy3d.chart.Chart;
//import org.jzy3d.chart.Settings;
//import org.jzy3d.colors.Color;
//import org.jzy3d.plot3d.primitives.AbstractDrawable;
//import org.jzy3d.plot3d.primitives.axes.layout.renderers.DefaultDecimalTickRenderer;
//import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
//import org.jzy3d.plot3d.rendering.canvas.ICanvas;
//import org.jzy3d.plot3d.rendering.canvas.Quality;
//import org.jzy3d.plot3d.rendering.scene.Scene;
//import org.jzy3d.plot3d.rendering.view.Renderer2d;
//import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
//import org.openide.util.lookup.ServiceProvider;
//
///**
// *
// * @author sj
// *
// * based on
// * http://code.google.com/p/jzy3d/source/browse/branches/0.8.1/src/demos/org/jzy3d/demos/histogram/barchart/BarChartDemo.java?r=438
// */
//@ServiceProvider(service = ViewerI.class)
//public class BarChart3DViewer extends CategoricalViewerI {
//
//    @Override
//    public boolean canHandle(AttributeType valueType) {
//        // currently broken, disable viewer
//        return false;
//    }
//
//    private LabeledMouseSelector mouseSelection;
//    private CustomMouseControl mouseCamera;
//    private AWTChart chart;
//    private final List<VisualizationGroup> groups = new ArrayList<>();
//    private final List<Attribute> attributes = new ArrayList<>();
//
//    @Override
//    public JComponent getComponent() {
//        return (JComponent) chart.getCanvas();
//    }
//
//    @Override
//    public String getName() {
//        return "3D Bar Chart";
//    }
//
//    @Override
//    public Class getInputType() {
//        return Distribution.class;
//    }
//
//    @Override
//    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
//
//        SortOrder sorter = new SortOrder(getAttributeType(), SortOrder.DESCENDING);
//        dists = sorter.filter(dists);
//
//        Settings.getInstance().setHardwareAccelerated(true);
//        chart = new AWTChart(Quality.Nicest, "swing"); // {
////
////            @Override
////            public void dispose() {
////                mouseCamera.removeSlaveThreadController();
////            }
////        };
//        setupAxes();
//        setupMouseNavigation();
//        setupKeyboardNavigation();
//        setupKeyboardSave();
//        setupMouseSelection();
//        setupTitle();
//        chart.getView().setMaximized(true);
//        chart.getView().getCamera().setStretchToFill(true);
//
//        BarChartBar.BAR_RADIUS = 5f;
//        BarChartBar.BAR_FEAT_BUFFER_RADIUS = BarChartBar.BAR_RADIUS / 2f;
//
//        attributes.clear();
//        groups.clear();
//
//        // collect all present attributes
//        for (Pair<VisualizationGroup, Distribution> p : dists) {
//            for (Attribute a : p.getSecond().keySet()) {
//                if (!attributes.contains(a)) {
//                    attributes.add(a);
//                }
//            }
//        }
//
//        int distNum = 0;
//        int entryNum = 0;
//        Scene scene = chart.getScene();
//        for (Pair<VisualizationGroup, Distribution> p : dists) {
//            VisualizationGroup curGroup = p.getFirst();
//            Distribution curDist = p.getSecond();
//            groups.add(curGroup);
//
//            Color color = new Color(curGroup.getColor().getRed(), curGroup.getColor().getGreen(), curGroup.getColor().getBlue());
//            for (Attribute attr : attributes) {
//                float height = curDist.containsKey(attr) ? curDist.get(attr).floatValue() : 0;
//                scene.add(addBar(distNum, curGroup.getName(), entryNum++, attr.getValue(), height, color));
//            }
//            distNum++;
//        }
//    }
//
//    @Override
//    public JComponent getCustomizer() {
//        return null;
//    }
//
//    private void setupMouseSelection() {
//        mouseSelection = new LabeledMouseSelector(chart);
//        chart.getCanvas().addMouseController(mouseSelection);
//        //chart.getCanvas().addKeyListener(mouseSelection);
//    }
//
//    private void setupTitle() {
//        Renderer2d messageRenderer = new Renderer2d() {
//            @Override
//            public void paint(Graphics g) {
//                g.setColor(java.awt.Color.BLACK);
//                g.setFont(g.getFont().deriveFont(Font.BOLD, 16));
//                g.drawString(getTitle(),
//                        (int) (15 + 0.05d * chart.getCanvas().getRendererWidth()),
//                        (int) (15 + 0.05d * chart.getCanvas().getRendererHeight()));
//            }
//        };
//        chart.addRenderer(messageRenderer);
//    }
//
//    private void setupMouseNavigation() {
//        mouseCamera = new CustomMouseControl(chart);
//        mouseCamera.install();
//    }
//
//    private int getFeatureIndex(float figYCenter) {
//        return (int) ((figYCenter) / (2 * (BarChartBar.BAR_FEAT_BUFFER_RADIUS + BarChartBar.BAR_RADIUS)));
//    }
//
//    private void setupAxes() {
//
//        // x axis
//        chart.getAxeLayout().setXAxeLabel("Groups");
//        chart.getAxeLayout().setXTickRenderer(new ITickRenderer() {
//            @Override
//            public String format(double value) {
//                int idx = getFeatureIndex(value);
//                //int idx = Math.round(value); 
//                if (value >= 0 && idx >= 0 && idx < groups.size()) {
//                    return groups.get(idx).getName();
//                } else {
//                    return "";
//                }
//            }
//        });
//
//        // y axis
//        chart.getAxeLayout().setYAxeLabel(getAttributeType().getName());
//        chart.getAxeLayout().setYTickProvider(new DiscreteTickProvider());
//        chart.getAxeLayout().setYTickRenderer(new ITickRenderer() {
//            @Override
//            public String format(double value) {
//                int idx = getFeatureIndex(value);
//                if (value >= 0 && idx >= 0 && idx < attributes.size()) {
//                    return attributes.get(idx).getValue();
//                } else {
//                    return "";
//                }
//            }
//        });
//
//        // z axis
//        chart.getAxeLayout().setZAxeLabel("Count");
//        chart.getAxeLayout().setZTickRenderer(new DefaultDecimalTickRenderer(1));
//        chart.getAxeLayout().setZTickProvider(new DiscreteTickProvider());
////        chart.getAxeLayout().setZTickRenderer( new ScientificNotationTickRenderer(2) );
////        float[] ticks = {0f, 0.5f, 1f};
////            chart.getAxeLayout().setZTickProvider(new StaticTickProvider(ticks));
//
//        chart.getView().setViewPositionMode(ViewPositionMode.FREE);
////        chart.getView().setAxeSquared(false);
//    }
//
//    public AbstractDrawable addBar(int compUnit, String compUnitName, int feature, String featureName, float height, Color color) {
//        color.a = 1f;
//
//        BarChartBar bar = new BarChartBar(chart, featureName, compUnitName);
//
//        bar.setData(compUnit, feature, height, color);
////        if (!a) {
////            bar.setColorMapper(new ColorMapper(new AffinityColorGen(), 0f, 2.0f));
////            bar.setLegend(new ColorbarLegend(bar, chart.getAxeLayout()));
////            bar.setLegendDisplayed(true);
////            a = true;
////        }
//
//        return bar;
//    }
//
//    public Chart getChart() {
//        return chart;
//    }
//
//    private void setupLegend() {
//        chart.addRenderer(new CustomLegendRenderer(chart.getCanvas()));
//    }
//
//    private void setupKeyboardNavigation() {
//        AWTCanvas canvas = (AWTCanvas) chart.getCanvas();
//        canvas.addKeyListener(new CustomKeyboardControl(chart));
//    }
//
//    private void setupKeyboardSave() {
//        AWTCanvas canvas = (AWTCanvas) chart.getCanvas();
//        canvas.addKeyListener(new PNGKeyboardSaver(chart));
//    }
////    @Override
////    public void featureSelectionChanged(SelectionManager tl) {
////    }
////    @Override
////    public void dispose() {
////        if (chart != null) {
//////            ICanvas canvas = chart.getCanvas(); 
//////            canvas.dispose();
////            chart.dispose();
////        }
////        chart = null;
////    }
////    @Override
////    public void compUnitSelectionChanged(SelectionManager tl) {
////        if (chart != null) {
////            chart.render();
////        }
////    }
//
//    @Override
//    public ImageExporterI getImageExporter() {
//        return null;
//    }
//}
