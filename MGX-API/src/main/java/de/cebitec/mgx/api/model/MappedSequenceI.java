/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.comparator.MappedSequenceComparator;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class MappedSequenceI extends LocationBase<MappedSequenceI> implements Comparable<MappedSequenceI> {

    //

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MappedSequenceI.class, "MappedSequenceI");

    public MappedSequenceI(int start, int stop) {
        super(start, stop);
    }

    public abstract long getSeqId();

    public abstract float getIdentity();

    @Override
    public final int compareTo(MappedSequenceI o) {
        int ret = MappedSequenceComparator.getInstance().compare(this, o);
        if (ret != 0) {
            return ret;
        }
        return Long.compare(getSeqId(), o.getSeqId());
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

}
