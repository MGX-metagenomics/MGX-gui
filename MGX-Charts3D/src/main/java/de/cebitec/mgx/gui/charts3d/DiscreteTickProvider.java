package de.cebitec.mgx.gui.charts3d;

import org.apache.commons.math3.util.FastMath;
import org.jzy3d.plot3d.primitives.axes.layout.providers.ITickProvider;

/**
 *
 * @author sj
 */
class DiscreteTickProvider implements ITickProvider {

    @Override
    public double[] generateTicks(double min, double max) {
        return generateTicks(min, max, getSteps(min, max));
    }

    @Override
    public double[] generateTicks(double min, double max, int steps) {
        steps = FastMath.max(0, steps);
        double[] ticks = new double[steps];
        for (int i = 0; i < steps; i++) {
            ticks[i] = min + BarChartBar.BAR_RADIUS + i * 2 * (BarChartBar.BAR_RADIUS + BarChartBar.BAR_FEAT_BUFFER_RADIUS);
        }
        return ticks;
    }

    public int getSteps(double min, double max) {
        return (int) FastMath.ceil(
                //                        chart.getView().getBounds().getYRange().getRange()
                (max - min)
                / (2f * (BarChartBar.BAR_RADIUS + BarChartBar.BAR_FEAT_BUFFER_RADIUS)));
    }

    @Override
    public int getDefaultSteps() {
        return 10;
    }
}
