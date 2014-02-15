package de.cebitec.mgx.gui.mapping.sequences;

/**
 * A persistant feature. Containing background information about a feature, such
 * as id, ec number, locus, product, start and stop positions, strand and type.
 *
 * @author ddoppmeier, rhilker
 */
public class RegionHolder implements ISequenceHolder, Comparable<RegionHolder> {

    private long id;
    private String name = "";
    private int start;
    private int stop;
    private boolean isFwdStrand;
    private int frame;

    /**
     * @param id id of the feature in db
     * @param parentIds The string containing all ids of the parents of this
     * feature separated by ";", if it has at least one. If not this string is
     * empty.
     * @param start start position
     * @param stop stop position
     * @param isFwdStrand SequenceUtils.STRAND_FWD for featues on forward and
     * SequenceUtils.STRAND_REV on reverse strand
     * @param locus locus information
     * @param regionName name of the feature, if it exists (e.g. "dnaA")
     */
    public RegionHolder(long id,
            int start, int stop, boolean isFwdStrand, String regionName) {
        this.id = id;
        this.start = start;
        this.stop = stop;
        this.isFwdStrand = isFwdStrand;
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

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getStop() {
        return stop;
    }

    /**
     * Returns if the feature is located on the fwd or rev strand.
     *
     * @return true for featues on forward and false on reverse strand
     */
    public boolean isFwdStrand() {
        return isFwdStrand;
    }

    /**
     * @return SequenceUtils.STRAND_FWD_STRING ("Fwd") or
     * SequenceUtils.STRAND_REV_STRING ("Rev")
     */
    public String isFwdStrandString() {
        return isFwdStrand ? SequenceUtils.STRAND_FWD_STRING : SequenceUtils.STRAND_REV_STRING;
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
            returnString = "Feature with start: " + this.start + ", stop: " + this.stop;
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

    /**
     * Compares two PersistantFeature based on their start position. '0' is
     * returned for equal start positions, 1, if the start position of the other
     * is larger and -1, if the start position of this mapping is larger.
     *
     * @param feature mapping to compare to this mapping
     * @return '0' for equal start positions, 1, if the start position of the
     * other is larger and -1, if the start position of this mapping is larger.
     */
    @Override
    public int compareTo(RegionHolder feature) {
        int ret = 0;
        if (this.start < feature.getStart()) {
            ret = -1;
        } else if (this.start > feature.getStart()) {
            ret = 1;
        }
        return ret;
    }

}
