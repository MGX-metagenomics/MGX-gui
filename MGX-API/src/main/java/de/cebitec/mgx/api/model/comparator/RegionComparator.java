/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.comparator;

import de.cebitec.mgx.api.model.RegionI;
import java.util.Comparator;

/**
 *
 * @author sjaenick
 */
public class RegionComparator implements Comparator<RegionI> {

    private final static RegionComparator instance = new RegionComparator();

    public static RegionComparator getInstance() {
        return instance;
    }

    private RegionComparator() {
    }

    @Override
    public int compare(RegionI o1, RegionI o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
