package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ReferenceRegionI;

/**
 *
 * @author belmann
 */
public class ReferenceRegion extends ReferenceRegionI {

    public ReferenceRegion(MGXMasterI master, long id, long parentId, int start, int stop, String desc) {
        super(master, id, parentId, start, stop, desc);
    }

}
