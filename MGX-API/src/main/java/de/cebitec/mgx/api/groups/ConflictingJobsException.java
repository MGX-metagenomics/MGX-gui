package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public abstract class ConflictingJobsException extends Exception {

    private final GroupI group;

    public ConflictingJobsException(GroupI group) {
        this.group = group;
    }

    public final GroupI getGroup() {
        return group;
    }

    @Override
    public abstract String getMessage();
    
    public abstract Class getSourceClass();

}
