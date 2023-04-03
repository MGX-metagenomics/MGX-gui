package de.cebitec.mgx.api.groups;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public abstract class ConflictingJobsException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final GroupI<?> group;

    public ConflictingJobsException(GroupI<?> group) {
        this.group = group;
    }

    public final GroupI<?> getGroup() {
        return group;
    }

    @Override
    public abstract String getMessage();
    
    public abstract Class<?> getSourceClass();

}
