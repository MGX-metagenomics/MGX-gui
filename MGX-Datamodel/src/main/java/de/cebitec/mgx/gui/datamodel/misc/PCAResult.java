package de.cebitec.mgx.gui.datamodel.misc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class PCAResult {

    private final List<Point> datapoints = new LinkedList<>();
    private final List<Point> loadings = new LinkedList<>();
    private final double[] variances;

    public PCAResult(double[] variances) {
        this.variances = variances;
    }

    public double[] getVariances() {
        return variances;
    }

    public void addPoint(Point p) {
        assert p.getName() != null;
        datapoints.add(p);
    }

    public List<Point> getDatapoints() {
        return datapoints;
    }

    public void addLoading(Point p) {
        assert p.getName() != null;
        loadings.add(p);
    }

    public List<Point> getLoadings() {
        Collections.sort(loadings);
        return loadings;
    }
}
