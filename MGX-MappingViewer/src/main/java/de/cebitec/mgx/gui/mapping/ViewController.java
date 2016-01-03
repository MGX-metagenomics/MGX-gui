/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class ViewController implements PropertyChangeListener {

    private final MappingCtx ctx;
    private final int[] curBounds;
    private int intervalLen;
    private int[] previewBounds;
    private final int[] newBounds;
    private final PropertyChangeSupport pcs;
    private int minIdentity = 0;
    //
    public static final String BOUNDS_CHANGE = "boundsChange";
    public static final String MAX_COV_CHANGE = "maxCoverageChange";
    public static final String PREVIEWBOUNDS_CHANGE = "previewBoundsChange";
    public static final String MIN_IDENTITY_CHANGE = "minIdentityChange";

    public ViewController(MappingCtx ctx) {
        this.ctx = ctx;
        ctx.addPropertyChangeListener(this);
        newBounds = new int[]{0, FastMath.min(15000, ctx.getReference().getLength() - 1)};
        curBounds = new int[]{0, FastMath.min(15000, ctx.getReference().getLength() - 1)};
        intervalLen = curBounds[1] - curBounds[0] + 1;
        pcs = new ParallelPropertyChangeSupport(this, true);
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
    
    public final int getIntervalLength() {
        return intervalLen;
    }

    public int[] getPreviewBounds() {
        return Arrays.copyOf(previewBounds, 2);
    }

    public void setBounds(int i, int j) {
        newBounds[0] = FastMath.max(0, i);
        newBounds[1] = FastMath.min(ctx.getReference().getLength(), j);

        if (curBounds[0] != newBounds[0] || curBounds[1] != newBounds[1]) {
            curBounds[0] = newBounds[0];
            curBounds[1] = newBounds[1];
            intervalLen = curBounds[1] - curBounds[0] + 1;
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, curBounds);
        }
    }

    public void setMinIdentity(int ident) {
        minIdentity = ident;
        pcs.firePropertyChange(MIN_IDENTITY_CHANGE, -1, minIdentity);
    }

    public int getMinIdentity() {
        return minIdentity;
    }

    public void setPreviewBounds(int i, int j) {
        previewBounds[0] = i;
        previewBounds[1] = j;
        pcs.firePropertyChange(PREVIEWBOUNDS_CHANGE, 0, previewBounds);
    }

    public String getSequence(int from, int to) throws MGXException {
        return ctx.getSequence(from, to);
    }

    public Set<RegionI> getRegions() throws MGXException {
        return ctx.getRegions(curBounds[0], curBounds[1]);
    }

    public Set<RegionI> getRegions(int from, int to) throws MGXException {
        return ctx.getRegions(from, to);
    }

    public Iterator<MappedSequenceI> getMappings() throws MGXException {
        return ctx.getMappings(curBounds[0], curBounds[1], minIdentity);
    }

    public Iterator<MappedSequenceI> getMappings(int from, int to) throws MGXException {
        return ctx.getMappings(from, to, minIdentity);
    }

    public Iterator<MappedSequenceI> getMappings(int from, int to, int minIdent) throws MGXException {
        return ctx.getMappings(from, to, minIdent);
    }

    public void getCoverage(int from, int to, int[] dest) throws MGXException {
        ctx.getCoverage(from, to, dest);
    }

    public IntIterator getCoverageIterator() throws MGXException {
        return ctx.getCoverageIterator(0, ctx.getReference().getLength() - 1);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listener.propertyChange(new PropertyChangeEvent(this, BOUNDS_CHANGE, 0, curBounds));
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(MappingCtx.MAPPING_CLOSED)) {
            ctx.removePropertyChangeListener(this);
            curBounds[0] = 0;
            curBounds[1] = 0;
            intervalLen = -1;
            minIdentity = -1;
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, curBounds);
        }
    }

    public boolean isClosed() {
        return ctx.isClosed();
    }
}
