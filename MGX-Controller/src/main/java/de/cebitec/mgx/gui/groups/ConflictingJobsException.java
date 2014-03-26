package de.cebitec.mgx.gui.groups;

import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class ConflictingJobsException extends Exception {

    private final VisualizationGroup group;
    private final Map<SeqRun, Set<Job>> conflicts;

    public ConflictingJobsException(VisualizationGroup group, Map<SeqRun, Set<Job>> data) {
        this.group = group;
        this.conflicts = data;
    }

    public VisualizationGroup getGroup() {
        return group;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Conflicts remain for group " + group.getName()+ ": \n");
        for (Map.Entry<SeqRun, Set<Job>> e : conflicts.entrySet()) {
            sb.append(e.getKey().getName()).append(": ");
            for (Job j : e.getValue()) {
                sb.append(j.getId()).append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

}
