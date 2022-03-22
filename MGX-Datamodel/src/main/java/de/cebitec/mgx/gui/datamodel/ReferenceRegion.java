package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.common.RegionType;

/**
 *
 * @author belmann
 */
public class ReferenceRegion extends ReferenceRegionI {

    public ReferenceRegion(MGXMasterI master, long id, long parentId, int start, int stop, RegionType type, String desc) {
        super(master, id, parentId, start, stop, type, desc);
    }

}
