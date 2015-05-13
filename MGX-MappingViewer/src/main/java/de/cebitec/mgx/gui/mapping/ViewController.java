/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class ViewController implements PropertyChangeListener {

    private final MappingCtx ctx;
    private final int[] curBounds;
    private int[] previewBounds;
    private final int[] newBounds;
    private final PropertyChangeSupport pcs;
    //
    public static final String BOUNDS_CHANGE = "boundsChange";
    public static final String MAX_COV_CHANGE = "maxCoverageChange";
    public static final String PREVIEWBOUNDS_CHANGE = "previewBoundsChange";

    public ViewController(MappingCtx ctx) {
        this.ctx = ctx;
        ctx.addPropertyChangeListener(this);
        newBounds = new int[]{0, FastMath.min(15000, ctx.getReference().getLength() - 1)};
        curBounds = new int[]{0, FastMath.min(15000, ctx.getReference().getLength() - 1)};
        pcs = new ParallelPropertyChangeSupport(this);
    }

    public MGXReferenceI getReference() {
        return ctx.getReference();
    }

    public SeqRunI getSeqRun() {
        return ctx.getRun();
    }
    
    public ToolI getTool() {
        return ctx.getTool();
    }

    public long getMaxCoverage() throws MGXException {
        return ctx.getMaxCoverage();
    }

    public int[] getBounds() {
        return Arrays.copyOf(curBounds, 2);
    }

    public int[] getPreviewBounds() {
        return Arrays.copyOf(previewBounds, 2);
    }

    public void setBounds(int i, int j) {
//        assert i >= 0;
//        assert i < ctx.getReference().getLength();
//        assert j >= 0;
//        assert j < ctx.getReference().getLength();
//        assert i < j;

        newBounds[0] = FastMath.max(0, i);
        newBounds[1] = FastMath.min(ctx.getReference().getLength(), j);

        if (curBounds[0] != newBounds[0] || curBounds[1] != newBounds[1]) {
            curBounds[0] = newBounds[0];
            curBounds[1] = newBounds[1];
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, 1);
        }
    }

    public void setPreviewBounds(int i, int j) {
//        assert i >= 0;
//        assert i < ctx.getReference().getLength();
//        assert j >= 0;
//        assert j < ctx.getReference().getLength();
//        assert i < j;

        previewBounds[0] = i;
        previewBounds[1] = j;
        pcs.firePropertyChange(PREVIEWBOUNDS_CHANGE, 0, 1);
    }

    public String getSequence(int from, int to) throws MGXException {
        return ctx.getSequence(from, to);
    }

    public Set<RegionI> getRegions(int from, int to) throws MGXException {
        return ctx.getRegions(from, to);
    }

    public SortedSet<MappedSequenceI> getMappings(int from, int to) throws MGXException {
        return ctx.getMappings(from, to);
    }

    public void getCoverage(int from, int to, int[] dest) throws MGXException {
        ctx.getCoverage(from, to, dest);
    }

    public IntIterator getCoverageIterator() throws MGXException {
        return ctx.getCoverageIterator(0, ctx.getReference().getLength() - 1);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listener.propertyChange(new PropertyChangeEvent(this, BOUNDS_CHANGE, 0, 1));
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ModelBase.OBJECT_DELETED)) {
            curBounds[0] = 0;
            curBounds[1] = 0;
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, 1);
        }
    }
}
