/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public abstract class LocationBase<T extends LocationBase<T>> implements LocationI {

    private final int start;
    private final int stop;
    private final int min;
    private final int max;

    public LocationBase(int start, int stop) {
        //super(m, df);
        this.start = start;
        this.stop = stop;
        this.min = FastMath.min(start, stop);
        this.max = FastMath.max(start, stop);
    }

    @Override
    public final int getStart() {
        return start;
    }

    @Override
    public final int getStop() {
        return stop;
    }

    @Override
    public final int getMax() {
        return max;
    }

    @Override
    public final int getMin() {
        return min;
    }
}
