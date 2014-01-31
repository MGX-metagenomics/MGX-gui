package de.cebitec.mgx.gui.groups;

import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class ConflictingJobsException extends Exception {

    private final VisualizationGroup group;
    private final Map<SeqRun, List<Job>> conflicts;

    public ConflictingJobsException(VisualizationGroup group, Map<SeqRun, List<Job>> data) {
        this.group = group;
        this.conflicts = data;
    }

    public VisualizationGroup getGroup() {
        return group;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Conflicts remain for group " + group.getName()+ ": \n");
        for (Map.Entry<SeqRun, List<Job>> e : conflicts.entrySet()) {
            sb.append(e.getKey().getName()).append(": ");
            for (Job j : e.getValue()) {
                sb.append(j.getId()).append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

}
