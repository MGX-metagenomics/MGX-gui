package de.cebitec.mgx.gui.charts3d;

import java.awt.Graphics;
import java.awt.Image;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.plot2d.primitive.ColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.axes.layout.providers.AbstractTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

/**
 *
 * @author sj
 */
class CustomLegendRenderer implements Renderer2d {

    private final ICanvas c;

    public CustomLegendRenderer(ICanvas c) {
        this.c = c;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(toImage(100, 100), c.getRendererWidth() - 100, 0, null);
    }

    public Image toImage(int width, int height) {
        ColorbarImageGenerator bar = new ColorbarImageGenerator(
                new ColorMapper(new AffinityColorGen(), 0f, 2f),
                new AbstractTickProvider() {

                    @Override
                    public double[] generateTicks(double min, double max, int steps) {
                        return new double[]{0f, 1f, 2f};
                    }

                    @Override
                    public int getDefaultSteps() {
                        return 3;
                    }
                },
                new ITickRenderer() {

                    @Override
                    public String format(double value) {
                        switch ((int) value) {
                            case 0:
                                return "Single-feat.";
                            case 1:
                                return "Group-feat.";
                            case 2:
                                return "Infrastructural";
                            default:
                                return "";
                        }
                    }
                });

        bar.setForegroundColor(Color.BLACK);
        bar.setHasBackground(false);

        // render @ given dimensions
        return bar.toImage(Math.max(width - 25, 1), Math.max(height - 25, 1));
    }
}
