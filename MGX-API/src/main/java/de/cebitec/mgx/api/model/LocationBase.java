/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

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
        this.min = min(start, stop);
        this.max = max(start, stop);
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

    private static int min(final int a, final int b) {
        return (a <= b) ? a : b;
    }

    private static int max(final int a, final int b) {
        return (a <= b) ? b : a;
    }
}
