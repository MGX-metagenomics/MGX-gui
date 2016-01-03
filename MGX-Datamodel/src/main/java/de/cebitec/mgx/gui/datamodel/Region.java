package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.model.RegionI;

/**
 *
 * @author belmann
 */
public class Region extends RegionI {

    protected long reference_id;
    private String name;
    private String description;

    public Region(int start, int stop) {
        super(start, stop);
    }

    @Override
    public long getReferenceId() {
        return reference_id;
    }

    @Override
    public void setReference(long reference_id) {
        this.reference_id = reference_id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getLength() {
        return isFwdStrand() ? getStop() - getStart() + 1 : getStart() - getStop() + 1;
    }

    @Override
    public boolean isFwdStrand() {
        return getStart() < getStop();
    }

    /**
     * @return 1, 2, 3, -1, -2, -3 depending on the reading frame of the feature
     */
    @Override
    public int getFrame() {
        int frame;

        if (isFwdStrand()) { // forward strand
            frame = (getStart() - 1) % 3 + 1;
        } else {
            frame = (getStop() - 1) % 3 - 3;
        }
        return frame;
    }

//    @Override
//    public int compareTo(RegionI o) {
//        return name.compareTo(o.getName());
//    }
}
