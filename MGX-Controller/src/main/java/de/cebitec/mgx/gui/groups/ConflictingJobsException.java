package de.cebitec.mgx.gui.groups;

/**
 *
 * @author sjaenick
 */
public class ConflictingJobsException extends Exception {

    private VisualizationGroup group;

    public ConflictingJobsException(VisualizationGroup group) {
        this.group = group;
    }

    public VisualizationGroup getGroup() {
        return group;
    }
}
