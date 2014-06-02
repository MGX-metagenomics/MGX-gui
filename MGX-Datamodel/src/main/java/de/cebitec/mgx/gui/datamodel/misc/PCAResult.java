package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.api.misc.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class PCAResult implements PCAResultI {

    private final List<Point> datapoints = new LinkedList<>();
    private final List<Point> loadings = new LinkedList<>();
    private final double[] variances;

    public PCAResult(double[] variances) {
        this.variances = variances;
    }

    @Override
    public double[] getVariances() {
        return variances;
    }

    @Override
    public void addPoint(Point p) {
        assert p.getName() != null;
        datapoints.add(p);
    }

    @Override
    public List<Point> getDatapoints() {
        return datapoints;
    }

    @Override
    public void addLoading(Point p) {
        assert p.getName() != null;
        loadings.add(p);
    }

    @Override
    public List<Point> getLoadings() {
        Collections.sort(loadings);
        return loadings;
    }
}
