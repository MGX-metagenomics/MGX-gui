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
public interface TrackI {

    public final static int DEFAULT_PADDING = 25; // bp, should be px

    int getId();

    int getMax();

    public boolean tryAdd(MappedSequenceI ms);

    void add(MappedSequenceI ms);

    Iterator<MappedSequenceI> getSequences();

    Collection<MappedSequenceI> sequences();
}
