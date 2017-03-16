/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import de.cebitec.mgx.api.model.MappedSequenceI;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class DummyTrack implements TrackI {
    
    int max;

    @Override
    public int getId() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMax() {
        return max;
    }

    public void setMax(int i) {
        max = i;
    }

    @Override
    public void add(MappedSequenceI ms) {
        throw new UnsupportedOperationException("add() to dummy not supported.");
    }

    @Override
    public boolean tryAdd(MappedSequenceI ms) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public final int hashCode() {
        return max;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public Collection<MappedSequenceI> sequences() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<MappedSequenceI> getSequences() {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
