/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.common.RegionType;

/**
 *
 * @author sj
 */
public abstract class AssembledRegionI extends RegionI {

    //public static final DataFlavor DATA_FLAVOR = new DataFlavor(GeneI.class, "GeneI");
    public AssembledRegionI(MGXMasterI master, long id, long parentId, int start, int stop, RegionType type) {
        super(master, id, parentId, start, stop, type);
    }

    public abstract int getCoverage();

    public final int getAALength() {
        if (getType() == RegionType.CDS) {
            int nuclLen;
            if (getStart() < getStop()) {
                nuclLen = getStop() - getStart() + 1;
            } else {
                nuclLen = getStart() - getStop() + 1;

            }
            return nuclLen / 3;
        }
        return -1;
    }

}
