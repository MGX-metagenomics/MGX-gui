package de.cebitec.mgx.gui.mapping.sequences;

/**
 * A persistant feature. Containing background information about a feature, such
 * as id, ec number, locus, product, start and stop positions, strand and type.
 *
 * @author ddoppmeier, rhilker
 */
public class RegionHolder extends ISequenceHolder {

    private final long id;
    private String name = "";
    private int frame;

    /**
     * @param id id of the feature in db
     * @param start start position
     * @param stop stop position
     * @param regionName name of the feature, if it exists (e.g. "dnaA")
     */
    public RegionHolder(long id, int start, int stop, String regionName) {
        super(start, stop);
        this.id = id;
        if (regionName != null) {
            this.name = regionName;
        }
    }

    /**
     * @return The unique id of this feature
     */
    public long getId() {
        return id;
    }

    /**
     * @return The locus of the feature, if it is set.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns if the feature is located on the fwd or rev strand.
     *
     * @return true for featues on forward and false on reverse strand
     */
    public boolean isFwdStrand() {
        return getStart() < getStop();
    }

    /**
     * Retrieves the best possible string representation of the feature. First
     * it checks the feature name, then the locus information, then the EC
     * number and if all are not given it returns "Feature with start: x, stop:
     * y".
     *
     * @return the best possible name for the feature.
     */
    @Override
    public String toString() {
        String returnString;
        if (this.name != null && !this.name.isEmpty()) {
            returnString = this.name;
        } else if (this.name != null && !this.name.isEmpty()) {
            returnString = this.name;
        } else {
            returnString = "Feature with start: " + getStart() + ", stop: " + getStop();
        }
        return returnString;
    }

    /**
     * @param frame The <tt>frame</tt> in which this feature should be displayed
     */
    public void setFrame(int frame) {
        this.frame = frame;
    }

    /**
     * @return The frame in which this feature should be displayed
     */
    public int getFrame() {
        return frame;
    }
}
