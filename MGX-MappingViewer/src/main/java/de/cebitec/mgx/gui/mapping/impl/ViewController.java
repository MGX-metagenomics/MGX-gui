/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.impl;

import de.cebitec.mgx.gui.mapping.MappingCtx;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.cache.IntIterator;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.util.FastMath;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ViewController implements ViewControllerI {

    private MappingCtx ctx;
    private final int[] curBounds;
    private int intervalLen;
    private int[] previewBounds;
    private final int[] newBounds;
    private final ParallelPropertyChangeSupport pcs;
    private int minIdentity = 0;

    public ViewController(MappingCtx ctx) {
        this.ctx = ctx;
        ctx.addPropertyChangeListener(this);
        newBounds = new int[]{0, FastMath.min(15000, ctx.getReference().getLength() - 1)};
        curBounds = new int[]{0, FastMath.min(15000, ctx.getReference().getLength() - 1)};
        intervalLen = curBounds[1] - curBounds[0] + 1;
        pcs = new ParallelPropertyChangeSupport(this, true);
    }

    @Override
    public final MGXMasterI getMaster() {
        return ctx.getMaster();
    }

    @Override
    public MappingI getMapping() throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getMapping();
    }

    public MGXReferenceI getReference() throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getReference();
    }

    @Override
    public String getReferenceName() {
        return ctx.getReference().getName();
    }

    @Override
    public int getReferenceLength() {
        return ctx.getReference().getLength();
    }

    @Override
    public SeqRunI[] getSeqRuns() throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getRuns();
    }

    @Override
    public ToolI getTool() throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getTool();
    }

    @Override
    public long getMaxCoverage() throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getMaxCoverage();
    }

    // number of bp covered of the reference sequenc e
    @Override
    public long getGenomicCoverage() throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getGenomicCoverage();
    }

    @Override
    public int[] getBounds() {
        return Arrays.copyOf(curBounds, 2);
    }

    @Override
    public final int getIntervalLength() {
        return intervalLen;
    }

    public int[] getPreviewBounds() {
        return Arrays.copyOf(previewBounds, 2);
    }

    @Override
    public void setBounds(int i, int j) {
        newBounds[0] = FastMath.max(0, i);
        try {
            newBounds[1] = FastMath.min(getReference().getLength(), j);
        } catch (MGXLoggedoutException ex) {
            return;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (curBounds[0] != newBounds[0] || curBounds[1] != newBounds[1]) {
            curBounds[0] = newBounds[0];
            curBounds[1] = newBounds[1];
            intervalLen = curBounds[1] - curBounds[0] + 1;
//            System.err.println("NEW BOUNDS fire " + newBounds[0] + "-" + newBounds[1]);
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, getBounds());
//            System.err.println("NEW BOUNDS fire done ");
        }
    }

    @Override
    public void setMinIdentity(int ident) {
        minIdentity = ident;
        pcs.firePropertyChange(MIN_IDENTITY_CHANGE, -1, minIdentity);
    }

    @Override
    public int getMinIdentity() {
        return minIdentity;
    }

    public void setPreviewBounds(int i, int j) {
        previewBounds[0] = i;
        previewBounds[1] = j;
        pcs.firePropertyChange(PREVIEWBOUNDS_CHANGE, 0, previewBounds);
    }

    @Override
    public String getSequence(int from, int to) {
        try {
            return ctx.getSequence(from, to);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            return "";
        }
    }

    @Override
    public String getSequence() {
        return getSequence(0, getReferenceLength() - 1);
    }

    @Override
    public void selectRegion(ReferenceRegionI selectedRegion) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ReferenceRegionI getSelectedRegion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void navigateToRegion(long regionId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Set<ReferenceRegionI> getRegions() throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getRegions(curBounds[0], curBounds[1]);
    }

    public final Set<ReferenceRegionI> getRegions(int from, int to) throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getRegions(from, to);
    }

    @Override
    public final List<MappedSequenceI> getMappings() throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getMappings(curBounds[0], curBounds[1], minIdentity);
    }

    @Override
    public final List<MappedSequenceI> getMappings(int from, int to) throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getMappings(from, to, minIdentity);
    }

    @Override
    public final List<MappedSequenceI> getMappings(int from, int to, int minIdent) throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getMappings(from, to, minIdent);
    }

    @Override
    public void getCoverage(int from, int to, int[] dest) throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        ctx.getCoverage(from, to, dest);
    }

    @Override
    public final IntIterator getCoverageIterator() throws MGXException {
        if (isClosed()) {
            throw new MGXLoggedoutException("ViewController is closed");
        }
        return ctx.getCoverageIterator(0, ctx.getReference().getLength() - 1);
    }

    @Override
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        listener.propertyChange(new PropertyChangeEvent(this, BOUNDS_CHANGE, 0, curBounds));
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(MappingCtx.MAPPING_CLOSED)) {
            ctx.removePropertyChangeListener(this);
            ctx = null;
            curBounds[0] = 0;
            curBounds[1] = 0;
            intervalLen = -1;
            minIdentity = -1;
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, curBounds);
            pcs.firePropertyChange(VIEWCONTROLLER_CLOSED, false, true);
        }
    }

    @Override
    public final boolean isClosed() {
        return ctx == null || ctx.isClosed();
    }

    @Override
    public synchronized void close() {
        ctx.close();
        pcs.close();
    }

}
