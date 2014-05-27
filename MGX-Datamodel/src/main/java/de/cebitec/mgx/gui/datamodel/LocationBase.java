/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public abstract class LocationBase<T> extends Identifiable<T> {

    private final int start;
    private final int stop;

    public LocationBase(int start, int stop, DataFlavor df) {
        super(df);
        this.start = start;
        this.stop = stop;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    public int getMax() {
        return Math.max(getStart(), getStop());
    }

    public int getMin() {
        return Math.min(getStart(), getStop());
    }
}
