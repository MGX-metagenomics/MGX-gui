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

    public final static String CONTIG_CHANGE = "contigChange";
    public final static String BOUNDS_CHANGE = "boundsChange";
    public final static String FEATURE_SELECTED = "featureSelected";
    //public final static String VIEWCONTROLLER_CLOSED = "viewControllerClosed";
    //
    private final ParallelPropertyChangeSupport pcs;
    private ContigI contig;
    private final List<GeneI> genes = new ArrayList<>();
    private int[] curBounds;
    private int[] newBounds;
    private int intervalLen;
    private String sequence;

    public ContigViewController() {
        pcs = new ParallelPropertyChangeSupport(this, true);
    }

    public void setContig(ContigI contig) {
        if (this.contig != null) {
            this.contig.removePropertyChangeListener(this);
            this.contig = null;
        }

        genes.clear();
        sequence = null;

        if (contig != null) {
            this.contig = contig;
            contig.addPropertyChangeListener(this);

            curBounds = new int[]{0, FastMath.min(15000, contig.getLength() - 1)};
            newBounds = new int[]{0, FastMath.min(15000, contig.getLength() - 1)};
            intervalLen = curBounds[1] - curBounds[0] + 1;
        } else {
            curBounds = new int[]{0, 0};
            newBounds = new int[]{0, 0};
        }
        pcs.firePropertyChange(CONTIG_CHANGE, null, contig);
        pcs.firePropertyChange(BOUNDS_CHANGE, 0, getBounds());
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
            contig = null;
            curBounds[0] = 0;
            curBounds[1] = 0;
            intervalLen = -1;
            pcs.firePropertyChange(CONTIG_CHANGE, evt.getSource(), null);
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, curBounds);
            //pcs.firePropertyChange(VIEWCONTROLLER_CLOSED, false, true);
        }
    }

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        listener.propertyChange(new PropertyChangeEvent(this, BOUNDS_CHANGE, 0, curBounds));
        pcs.addPropertyChangeListener(listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    synchronized Iterable<GeneI> getRegions() {
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

    public String getSequence() {
        if (sequence == null) {
            try {
                sequence = contig.getMaster().Contig().getDNASequence(contig).getSequence().toUpperCase();
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return sequence;
    }

    public void close(int i) {
        pcs.close();
    }

    public boolean isClosed() {
        return contig.isDeleted();
    }

    void selectGene(GeneI selectedGene) {
        pcs.firePropertyChange(FEATURE_SELECTED, null, selectedGene);
    }

}
