package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;

/**
 *
 * @author sj
 */
public abstract class ReferenceRegionI extends RegionI {

    private final String description;

    public ReferenceRegionI(MGXMasterI master, long id, long parent, int start, int stop, String desc) {
        super(master, id, parent, start, stop);
        this.description = desc;
    }

    public final String getDescription() {
        return description;
    }

}
