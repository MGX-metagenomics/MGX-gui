package de.cebitec.mgx.gui.threedcharts;

import de.cebitec.mgx.gui.attributevisualization.viewer.CategoricalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JComponent;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.global.Settings;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class BarChart3DViewer extends CategoricalViewerI {

    private LabeledMouseSelector mouseSelection;
    private CustomMouseControl mouseCamera;
    private Chart chart;
    private List<VisualizationGroup> groups = new ArrayList<>();

    @Override
    public JComponent getComponent() {
        return (JComponent) chart.getCanvas();
    }

    @Override
    public String getName() {
        return "3D Bar Chart";
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
        Settings.getInstance().setHardwareAccelerated(true);
        chart = new Chart(Quality.Nicest, "swing") {

            @Override
            public void dispose() {
                mouseCamera.removeSlaveThreadController();
            }
        };
        setupAxes();
        setupMouseNavigation();
        setupKeyboardNavigation();
        setupKeyboardSave();
        setupMouseSelection();
        setupTitle();
        chart.getView().setMaximized(true);
        chart.getView().getCamera().setStretchToFill(true);
        BarChartBar.BAR_RADIUS = 5f;
        BarChartBar.BAR_FEAT_BUFFER_RADIUS = BarChartBar.BAR_RADIUS / 2f;
        Scene scene = chart.getScene();
        java.awt.Color c = java.awt.Color.BLUE;
        Color cc = new Color(c.getRed(), c.getGreen(), c.getBlue());

        Set<Attribute> attributes = new TreeSet<>();
        for (Pair<VisualizationGroup, Distribution> p : dists) {
            attributes.addAll(p.getSecond().keySet());
        }

        int distNum = 0;
        int entryNum = 0;
        for (Pair<VisualizationGroup, Distribution> p : dists) {
            groups.add(p.getFirst());
            for (Attribute attr : attributes) {
                double height = p.getSecond().containsKey(attr) ? p.getSecond().get(attr).doubleValue() : 0;
                scene.add(addBar(distNum++, p.getFirst().getName(), entryNum++, attr.getValue(), height, cc));

            }
        }
    }

    @Override
    public JComponent getCustomizer() {
        return null;
    }

    private void setupMouseSelection() {
        mouseSelection = new LabeledMouseSelector(chart);
        chart.getCanvas().addKeyListener(mouseSelection);
    }

    private void setupTitle() {
        Renderer2d messageRenderer = new Renderer2d() {

            @Override
            public void paint(Graphics g) {
                g.setColor(java.awt.Color.BLUE);
                g.setFont(g.getFont().deriveFont(Font.BOLD, 16));
                g.drawString(getTitle(),
                        (int) (15 + 0.05d * chart.getCanvas().getRendererWidth()),
                        (int) (15 + 0.05d * chart.getCanvas().getRendererHeight()));
            }
        };
        chart.addRenderer(messageRenderer);
    }

    private void setupMouseNavigation() {
        mouseCamera = new CustomMouseControl(chart);
        mouseCamera.install();
    }

    private int getFeatureIndex(float figYCenter) {
        return (int) ((figYCenter) / (2 * (BarChartBar.BAR_FEAT_BUFFER_RADIUS + BarChartBar.BAR_RADIUS)));
    }

    private void setupAxes() {
        chart.getAxeLayout().setXAxeLabel("Scattering");
        chart.getAxeLayout().setXTickRenderer(new DefaultDecimalTickRenderer(2));

        chart.getAxeLayout().setYAxeLabel("Group");
        chart.getAxeLayout().setYTickRenderer(new ITickRenderer() {

            @Override
            public String format(float value) {
                int idx = getFeatureIndex(value);
                if (value >= 0 && idx >= 0 && idx < groups.size()) {
                    return groups.get(idx).getName();
                } else {
                    return "";
                }
            }
        });
        chart.getAxeLayout().setYTickProvider(new DiscreteTickProvider());
        chart.getAxeLayout().setZAxeLabel("Tangling");
        chart.getAxeLayout().setZTickRenderer(new DefaultDecimalTickRenderer(2));
//        chart.getAxeLayout().setZTickRenderer( new ScientificNotationTickRenderer(2) );
//        float[] ticks = {0f, 0.5f, 1f};
//            chart.getAxeLayout().setZTickProvider(new StaticTickProvider(ticks));

        chart.getView().setViewPositionMode(ViewPositionMode.FREE);
//        chart.getView().setAxeSquared(false);
    }

    public AbstractDrawable addBar(int compUnit, String compUnitName, int feature, String featureName, double height, Color color) {
        // compUnit, feature numbered form 0!
        color.a = 1f;

        BarChartBar bar = new BarChartBar(chart, featureName, compUnitName);

        bar.setData(compUnit, feature, (float) height, color);
//        if (!a) {
//            bar.setColorMapper(new ColorMapper(new AffinityColorGen(), 0f, 2.0f));
//            bar.setLegend(new ColorbarLegend(bar, chart.getAxeLayout()));
//            bar.setLegendDisplayed(true);
//            a = true;
//        }

        return bar;
    }

    public Chart getChart() {
        return chart;
    }

    private void setupLegend() {
        chart.addRenderer(new CustomLegendRenderer(chart.getCanvas()));
    }

    private void setupKeyboardNavigation() {
        chart.getCanvas().addKeyListener(new CustomKeyboardControl(chart));
    }

    private void setupKeyboardSave() {
        chart.getCanvas().addKeyListener(new PNGKeyboardSaver(chart));
    }

//    @Override
//    public void featureSelectionChanged(SelectionManager tl) {
//    }
    @Override
    public void dispose() {
        if (chart != null) {
            chart.getCanvas().dispose();
            chart.dispose();
        }
        chart = null;
    }
//    @Override
//    public void compUnitSelectionChanged(SelectionManager tl) {
//        if (chart != null) {
//            chart.render();
//        }
//    }
}
