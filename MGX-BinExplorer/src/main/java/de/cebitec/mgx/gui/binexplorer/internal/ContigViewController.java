/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.internal;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class ContigViewController implements PropertyChangeListener {

    public final static String BOUNDS_CHANGE = "boundsChange";
    public final static String FEATURE_SELECTED = "featureSelected";
    public final static String VIEWCONTROLLER_CLOSED = "viewControllerClosed";
    //
    private final ParallelPropertyChangeSupport pcs;
    private final ContigI contig;
    private final List<GeneI> genes = new ArrayList<>();
    private final int[] curBounds;
    private final int[] newBounds;
    private int intervalLen;

    public ContigViewController(ContigI contig) {
        this.contig = contig;
        contig.addPropertyChangeListener(this);

        curBounds = new int[]{0, FastMath.min(15000, contig.getLength() - 1)};
        newBounds = new int[]{0, FastMath.min(15000, contig.getLength() - 1)};
        intervalLen = curBounds[1] - curBounds[0] + 1;
        pcs = new ParallelPropertyChangeSupport(this, true);
    }

    public int[] getBounds() {
        return curBounds; //Arrays.copyOf(curBounds, 2);
    }

    public int getIntervalLength() {
        return intervalLen;
    }

    public int getReferenceLength() {
        return contig.getLength();
    }

    void setBounds(int i, int j) {
        newBounds[0] = FastMath.max(0, i);
        newBounds[1] = FastMath.min(contig.getLength(), j);

        if (curBounds[0] != newBounds[0] || curBounds[1] != newBounds[1]) {
            curBounds[0] = newBounds[0];
            curBounds[1] = newBounds[1];
            intervalLen = curBounds[1] - curBounds[0] + 1;
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, getBounds());
        }
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(contig) && evt.getPropertyName().equals(ContigI.OBJECT_DELETED)) {
            contig.removePropertyChangeListener(this);
            curBounds[0] = 0;
            curBounds[1] = 0;
            intervalLen = -1;
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, curBounds);
            pcs.firePropertyChange(VIEWCONTROLLER_CLOSED, false, true);
        }
    }

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        listener.propertyChange(new PropertyChangeEvent(this, BOUNDS_CHANGE, 0, curBounds));
        pcs.addPropertyChangeListener(listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    Iterable<GeneI> getRegions() {
        if (genes.isEmpty()) {
            Iterator<GeneI> iter;
            try {
                iter = contig.getMaster().Gene().ByContig(contig);
                while (iter != null && iter.hasNext()) {
                    genes.add(iter.next());
                }
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
                genes.clear();
            }
        }

        return genes;

    }

    public String getReferenceName() {
        return contig.getName();
    }

    public void close() {
        contig.removePropertyChangeListener(this);
        pcs.firePropertyChange(VIEWCONTROLLER_CLOSED, false, true);
    }

    public boolean isClosed() {
        return contig.isDeleted();
    }

    void selectGene(GeneI selectedGene) {
         pcs.firePropertyChange(FEATURE_SELECTED, null, selectedGene);
    }

}
