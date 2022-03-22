package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.common.RegionType;

/**
 *
 * @author sj
 */
public abstract class ReferenceRegionI extends RegionI {

    private final String description;

    public ReferenceRegionI(MGXMasterI master, long id, long parent, int start, int stop, RegionType type, String desc) {
        super(master, id, parent, start, stop, type);
        this.description = desc;
    }

    public final String getDescription() {
        return description;
    }

}
