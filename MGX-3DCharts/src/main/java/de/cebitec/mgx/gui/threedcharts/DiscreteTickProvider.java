package de.cebitec.mgx.gui.threedcharts;

import org.jzy3d.plot3d.primitives.axes.layout.providers.ITickProvider;

/**
 *
 * @author sj
 */
class DiscreteTickProvider implements ITickProvider {

    @Override
    public float[] generateTicks(float min, float max) {
        return generateTicks(min, max, getSteps(min, max));
    }

    @Override
    public float[] generateTicks(float min, float max, int steps) {
        steps = Math.max(0, steps);
        float[] ticks = new float[steps];
        for (int i = 0; i < steps; i++) {
            ticks[i] = min + BarChartBar.BAR_RADIUS + i * 2 * (BarChartBar.BAR_RADIUS + BarChartBar.BAR_FEAT_BUFFER_RADIUS);
        }
        return ticks;
    }

    public int getSteps(float min, float max) {
        return (int) Math.ceil(
                //                        chart.getView().getBounds().getYRange().getRange()
                (max - min)
                / (2f * (BarChartBar.BAR_RADIUS + BarChartBar.BAR_FEAT_BUFFER_RADIUS)));
    }

    @Override
    public int getDefaultSteps() {
        return 42;
    }
}
