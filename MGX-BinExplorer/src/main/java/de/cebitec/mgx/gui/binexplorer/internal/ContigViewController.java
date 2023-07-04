/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.internal;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.SequenceViewControllerI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
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
public class ContigViewController implements PropertyChangeListener, SequenceViewControllerI<AssembledRegionI> {

    public final static String BIN_SELECTED = "binSelected";
    //
    private final ParallelPropertyChangeSupport pcs;
    //
    private volatile BinI bin = null;
    private volatile ContigI contig = null;
    private volatile AssembledRegionI selectedRegion = null;
    //
    private final List<AssembledRegionI> regions = new ArrayList<>();
    private int[] curBounds;
    private int[] newBounds;
    private int intervalLen;
    private String contigDNASequence = null;

    public ContigViewController() {
        pcs = new ParallelPropertyChangeSupport(this, true);
    }

    public void selectBin(BinI b) {
        BinI prev = bin;
        this.bin = b;

        if (prev != bin) {
            if (prev != null) {
                prev.removePropertyChangeListener(this);
            }
            bin.addPropertyChangeListener(this);
            pcs.firePropertyChange(BIN_SELECTED, prev, bin);
        }

        setContig(null);
    }

    public BinI getSelectedBin() {
        return bin;
    }

    public void setContig(ContigI contig) {
        
        ContigI prev = this.contig;

        if (contig != null && contig.equals(this.contig)) {
            // no change
            return;
        }

        if (this.contig != null) {
            this.contig = null;
        }

        regions.clear();
        contigDNASequence = null;

        if (contig != null) {
            this.contig = contig;

            curBounds = new int[]{0, FastMath.min(15000, contig.getLength() - 1)};
            newBounds = new int[]{0, FastMath.min(15000, contig.getLength() - 1)};
            intervalLen = curBounds[1] - curBounds[0] + 1;

            if (prev != this.contig) {
                pcs.firePropertyChange(CONTIG_CHANGE, prev, this.contig);
            }
        } else {
            this.contig = null;
            curBounds = new int[]{0, 0};
            newBounds = new int[]{0, 0};
            if (prev != null) {
                pcs.firePropertyChange(CONTIG_CHANGE, prev, null);
            }
        }
        pcs.firePropertyChange(BOUNDS_CHANGE, 0, getBounds());
        selectRegion(null);
    }

    public ContigI getContig() {
        return contig;
    }

    @Override
    public int[] getBounds() {
        return curBounds; //Arrays.copyOf(curBounds, 2);
    }

    @Override
    public int getIntervalLength() {
        return intervalLen;
    }

    @Override
    public int getReferenceLength() {
        return contig != null ? contig.getLength() : 0;
    }

    @Override
    public void setBounds(int i, int j) {
        if (i > j) {
            throw new RuntimeException("Invalid bounds: " + i + "-" + j);
        }
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
        // contigs don't support propertychanges, therefore we subscribe
        // to the bin; if the bin gets deleted, the contig can no longer
        // be valid, too
        if (evt.getSource().equals(bin) && evt.getPropertyName().equals(BinI.OBJECT_DELETED)) {
            bin.removePropertyChangeListener(this);
            bin = null;
            contig = null;
            curBounds[0] = 0;
            curBounds[1] = 0;
            intervalLen = -1;
            pcs.firePropertyChange(CONTIG_CHANGE, evt.getSource(), null);
            pcs.firePropertyChange(BOUNDS_CHANGE, 0, curBounds);
            //pcs.firePropertyChange(VIEWCONTROLLER_CLOSED, false, true);
        }
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
    public synchronized Iterable<AssembledRegionI> getRegions() {
        if (regions.isEmpty()) {
            Iterator<AssembledRegionI> iter;
            try {
                iter = contig.getMaster().AssembledRegion().ByContig(contig);
                while (iter != null && iter.hasNext()) {
                    regions.add(iter.next());
                }
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
                regions.clear();
            }
        }

        return regions;

    }

    @Override
    public String getReferenceName() {
        return contig != null ? contig.getName() : null;
    }

    @Override
    public String getSequence() {
        if (contig == null) {
            return null;
        }

        if (contigDNASequence == null) {
            try {
                contigDNASequence = contig.getMaster().Contig().getDNASequence(contig).getSequence().toUpperCase();
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return contigDNASequence;
    }

    public void close() {
        selectBin(null);
        pcs.close();
    }

    @Override
    public boolean isClosed() {
        return contig == null;
    }

    @Override
    public void selectRegion(AssembledRegionI selectedGene) {
        AssembledRegionI prev = selectedRegion;
        selectedRegion = selectedGene;
        if (prev != selectedRegion) {
            pcs.firePropertyChange(FEATURE_SELECTED, prev, selectedRegion);
        }
    }

    @Override
    public AssembledRegionI getSelectedRegion() {
        return selectedRegion;
    }

    @Override
    public void navigateToRegion(long regionId) {
        AssembledRegionI target = null;
        for (AssembledRegionI r : getRegions()) {
            if (r.getId() == regionId) {
                target = r;
                break;
            }
        }

        if (target != null) {
            if (target == selectedRegion) {
                return;
            }
            selectRegion(target);
            pcs.firePropertyChange(NAVIGATE_TO_REGION, null, target);
        } else {
            System.err.println("Region not found!");
            regions.clear();
            selectRegion(null);
            contigDNASequence = null;
            curBounds[0] = 0;
            curBounds[1] = 0;
        }
    }

    @Override
    public String toString() {
        return "ContigViewController{" + "contig=" + contig + '}';
    }

    @Override
    public String getSequence(int from, int to) {
        String fullSeq = getSequence();
        return fullSeq.substring(from, to - from + 1);
    }

}
