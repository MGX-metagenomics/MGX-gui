package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import java.io.Serial;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public final class ConflictingJobsForAssembledSeqRunException extends ConflictingJobsException {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final Map<AssembledSeqRunI, Set<JobI>> conflicts;

    public ConflictingJobsForAssembledSeqRunException(GroupI<?> group, Map<AssembledSeqRunI, Set<JobI>> data) {
        super(group);
        this.conflicts = data;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Conflicts remain for group " + getGroup().getDisplayName()+ ": \n");
        for (Map.Entry<AssembledSeqRunI, Set<JobI>> e : conflicts.entrySet()) {
            sb.append(e.getKey().getName()).append(": ");
            for (JobI j : e.getValue()) {
                sb.append(j.getId()).append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public Class<?> getSourceClass() {
        return AssembledSeqRunI.class;
    }

}
