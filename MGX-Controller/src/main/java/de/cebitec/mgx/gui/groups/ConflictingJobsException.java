package de.cebitec.mgx.gui.groups;

/**
 *
 * @author sjaenick
 */
public class ConflictingJobsException extends Exception {

    private final VisualizationGroup group;

    public ConflictingJobsException(VisualizationGroup group) {
        this.group = group;
    }

    public VisualizationGroup getGroup() {
        return group;
    }

    @Override
    public String getMessage() {
        return "Conflicts remain for group " + group.getName();
    }

}
