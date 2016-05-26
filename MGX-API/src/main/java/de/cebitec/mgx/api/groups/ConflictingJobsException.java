package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class ConflictingJobsException extends Exception {

    private final VisualizationGroupI group;
    private final Map<SeqRunI, Set<JobI>> conflicts;

    public ConflictingJobsException(VisualizationGroupI group, Map<SeqRunI, Set<JobI>> data) {
        this.group = group;
        this.conflicts = data;
    }

    public VisualizationGroupI getGroup() {
        return group;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Conflicts remain for group " + group.getDisplayName()+ ": \n");
        for (Map.Entry<SeqRunI, Set<JobI>> e : conflicts.entrySet()) {
            sb.append(e.getKey().getName()).append(": ");
            for (JobI j : e.getValue()) {
                sb.append(j.getId()).append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

}
