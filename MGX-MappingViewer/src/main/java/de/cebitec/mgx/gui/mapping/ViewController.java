/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author sjaenick
 */
public class ViewController {

    private final MappingCtx ctx;
    private final int curBounds[];
    private final PropertyChangeSupport pcs;

    public ViewController(MappingCtx ctx) {
        this.ctx = ctx;
        curBounds = new int[]{0, Math.min(10000, ctx.getReference().getLength() - 1)};
        pcs = new PropertyChangeSupport(this);
    }

    public Reference getReference() {
        return ctx.getReference();
    }

    public int[] getBounds() {
        return Arrays.copyOf(curBounds, 2);
    }

    public void setBounds(int i, int j) {
        assert i >= 0;
        assert i < ctx.getReference().getLength();
        assert j >= 0;
        assert j < ctx.getReference().getLength();
        assert i < j;

        curBounds[0] = Math.max(0, i);
        curBounds[1] = Math.min(ctx.getReference().getLength(), j);
        pcs.firePropertyChange("boundsChange", 0, 1);
    }

    public String getSequence(int from, int to) {
        return ctx.getSequence(from, to);
    }

    public Set<Region> getRegions(int from, int to) {
        return ctx.getRegions(from, to);
    }

    public List<MappedSequence> getMappings(int from, int to) {
        return ctx.getMappings(from, to);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
