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
//public abstract class LocationBase<T extends LocationBase<T>> extends Identifiable<T> implements LocationI {
public abstract class LocationBase<T extends LocationBase<T>> implements LocationI {

    private final int start;
    private final int stop;
    private int min = -1;
    private int max = -1;

    public LocationBase(int start, int stop) {
        //super(m, df);
        this.start = start;
        this.stop = stop;
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
        if (max == -1) {
            max = FastMath.max(start, stop);
        }
        return max;
    }

    @Override
    public final int getMin() {
        if (min == -1) {
            min = FastMath.min(start, stop);
        }
        return min;
    }
}
