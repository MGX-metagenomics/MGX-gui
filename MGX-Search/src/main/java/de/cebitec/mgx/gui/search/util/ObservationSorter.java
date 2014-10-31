/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.api.model.ObservationI;
import java.util.Comparator;

/**
 *
 * @author sjaenick
 */
public class ObservationSorter implements Comparator<ObservationI> {

    @Override
    public int compare(ObservationI o1, ObservationI o2) {
        // compare start positions, first one comes first
        int min1 = o1.getStart() < o1.getStop() ? o1.getStart() : o1.getStop();
        int min2 = o2.getStart() < o2.getStop() ? o2.getStart() : o2.getStop();
        int ret = Integer.compare(min2, min1);
        // if equal, compare length - longer one first
        return ret == 0 ? compareLength(o1, o2) : ret;
    }

    private int compareLength(ObservationI o1, ObservationI o2) {
        int l1 = o1.getStart() < o1.getStop()
                ? o1.getStop() - o1.getStart() + 1
                : o1.getStart() - o1.getStop() + 1;
        int l2 = o2.getStart() < o2.getStop()
                ? o2.getStop() - o2.getStart() + 1
                : o2.getStart() - o2.getStop() + 1;
        return Integer.compare(l1, l2);
    }
}
