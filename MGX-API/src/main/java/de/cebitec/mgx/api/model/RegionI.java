/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.common.RegionType;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyChangeListener;

/**
 *
 * @author sj
 */
public abstract class RegionI extends Identifiable<RegionI> implements ModelBaseI<RegionI>, PropertyChangeListener, LocationI {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(RegionI.class, "RegionI");

    private final int start;
    private final int stop;
    private final int min;
    private final int max;
    //
    private final RegionType type;
    private final long parent;
    //
    private String name;
    //
    private ParallelPropertyChangeSupport pcs;
    //

    public RegionI(MGXMasterI master, long id, long parent, int start, int stop, RegionType type) {
        super(master, DATA_FLAVOR);
        super.setId(id);
        this.parent = parent;
        this.start = start;
        this.stop = stop;
        this.type = type;
        this.min = min(start, stop);
        this.max = max(start, stop);
    }

    public final long getParentId() {
        return parent;
    }

    public final String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final RegionType getType() {
        return type;
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

    @Override
    public final int getLength() {
        return start < stop ? getStop() - getStart() + 1 : getStart() - getStop() + 1;
    }

    @Override
    public final int getFrame() {
        int frame;

        if (start < stop) {
            frame = start % 3 + 1;
        } else {
            frame = -1 * (start % 3) - 1;
        }

        return frame;
    }

    private static int min(final int a, final int b) {
        return (a <= b) ? a : b;
    }

    private static int max(final int a, final int b) {
        return (a <= b) ? b : a;
    }

    @Override
    public int compareTo(RegionI o) {
        return name.compareTo(o.getName());
    }
}
