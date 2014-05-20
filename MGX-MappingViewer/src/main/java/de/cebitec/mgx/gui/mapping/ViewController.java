/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 * @author sjaenick
 */
public class ViewController {

    private final MappingCtx ctx;
    private final int[] curBounds;
    private int[] previewBounds;
    private final int[] newBounds;
    private final PropertyChangeSupport pcs;
    public static final String BOUNDS_CHANGE = "boundsChange";
    public static final String PREVIEWBOUNDS_CHANGE = "previewBoundsChange";

    public ViewController(MappingCtx ctx) {
        this.ctx = ctx;
        newBounds = new int[]{0, Math.min(15000, ctx.getReference().getLength() - 1)};
        curBounds = new int[]{0, Math.min(15000, ctx.getReference().getLength() - 1)};
        pcs = new ParallelPropertyChangeSupport(this);
    }

    public Reference getReference() {
        return ctx.getReference();
    }

    public int getMaxCoverage() {
        return 5; // ctx.getMaxCoverage();
    }

    public int[] getBounds() {
        return Arrays.copyOf(curBounds, 2);
    }

    public int[] getPreviewBounds() {
        return Arrays.copyOf(previewBounds, 2);
    }

    public void setBounds(int i, int j) {
        assert i >= 0;
        assert i < ctx.getReference().getLength();
        assert j >= 0;
        assert j < ctx.getReference().getLength();
        assert i < j;

        newBounds[0] = Math.max(0, i);
        newBounds[1] = Math.min(ctx.getReference().getLength(), j);

        if (curBounds[0] != newBounds[0] || curBounds[1] != newBounds[1]) {
            curBounds[0] = newBounds[0];
            curBounds[1] = newBounds[1];
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, 1);
        }
    }

    public void setPreviewBounds(int i, int j) {
        assert i >= 0;
        assert i < ctx.getReference().getLength();
        assert j >= 0;
        assert j < ctx.getReference().getLength();
        assert i < j;

        previewBounds[0] = i;
        previewBounds[1] = j;
        pcs.firePropertyChange(PREVIEWBOUNDS_CHANGE, 0, 1);
    }

    public String getSequence(int from, int to) {
        return ctx.getSequence(from, to);
    }

    public Set<Region> getRegions(int from, int to) {
        return ctx.getRegions(from, to);
    }

    public SortedSet<MappedSequence> getMappings(int from, int to) {
        return ctx.getMappings(from, to);
    }

    public int[] getCoverage(int from, int to) {
        return ctx.getCoverage(from, to);
    }
    
    public IntIterator getCoverageIterator() {
        return ctx.getCoverageIterator(0, ctx.getReference().getLength() -1);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
