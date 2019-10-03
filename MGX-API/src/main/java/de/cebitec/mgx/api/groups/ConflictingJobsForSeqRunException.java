package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public final class ConflictingJobsForSeqRunException extends ConflictingJobsException {

    private final Map<SeqRunI, Set<JobI>> conflicts;

    public ConflictingJobsForSeqRunException(GroupI group, Map<SeqRunI, Set<JobI>> data) {
        super(group);
        this.conflicts = data;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Conflicts remain for group " + getGroup().getDisplayName()+ ": \n");
        for (Map.Entry<SeqRunI, Set<JobI>> e : conflicts.entrySet()) {
            sb.append(e.getKey().getName()).append(": ");
            for (JobI j : e.getValue()) {
                sb.append(j.getId()).append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public Class getSourceClass() {
        return SeqRunI.class;
    }

}
