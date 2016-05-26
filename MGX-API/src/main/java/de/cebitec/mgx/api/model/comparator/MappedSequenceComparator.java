/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.comparator;

import de.cebitec.mgx.api.model.MappedSequenceI;
import java.util.Comparator;

/**
 *
 * @author sjaenick
 */
public class MappedSequenceComparator implements Comparator<MappedSequenceI> {

    private final static MappedSequenceComparator instance = new MappedSequenceComparator();

    public static MappedSequenceComparator getInstance() {
        return instance;
    }

    private MappedSequenceComparator() {
    }

    @Override
    public int compare(MappedSequenceI o1, MappedSequenceI o2) {
        int ret = Integer.compare(o1.getMin(), o2.getMin());
        if (ret != 0) {
            return ret;
        }
        ret = Integer.compare(o1.getMax(), o2.getMax());
        if (ret != 0) {
            return ret;
        }
        ret = Float.compare(o1.getIdentity(), o2.getIdentity());
        return ret;
    }

}
