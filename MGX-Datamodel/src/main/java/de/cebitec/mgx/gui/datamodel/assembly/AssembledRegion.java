/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.common.RegionType;

/**
 *
 * @author sj
 */
public class AssembledRegion extends AssembledRegionI {

    private final int coverage;

    public AssembledRegion(MGXMasterI m, long id, long ctgId, int start, int stop, RegionType type, int coverage) {
        super(m, id, ctgId, start, stop, type);
        this.coverage = coverage;
    }

    @Override
    public int getCoverage() {
        return coverage;
    }

}
