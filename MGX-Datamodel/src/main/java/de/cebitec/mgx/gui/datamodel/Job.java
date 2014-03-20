package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author sjaenick
 */
public class Job extends Identifiable<Job> {

    protected SeqRun seqrun;
    //
    protected Tool tool;
    protected String created_by;
    //
    protected Date startDate = null;
    //
    protected Date finishDate = null;
    //
    protected Collection<JobParameter> parameters = null;
    private int jobstate = JobState.CREATED.getValue();
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(Job.class, "Job");

    public Job() {
        super(DATA_FLAVOR);
    }

    public JobState getStatus() {
        return JobState.values()[jobstate];
    }

    public Job setStatus(JobState status) {
        jobstate = status.getValue();
        return this;
    }

    public Tool getTool() {
        return tool;
    }

    public Job setTool(Tool t) {
        assert t != null;
        this.tool = t;
        return this;
    }

    public Collection<JobParameter> getParameters() {
        return parameters;
    }

    public Job setParameters(Collection<JobParameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public Job setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Job setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public SeqRun getSeqrun() {
        return seqrun;
    }

    public Job setSeqrun(SeqRun run) {
        assert run != null;
        this.seqrun = run;
        return this;
    }

    public String getCreator() {
        return created_by;
    }

    public Job setCreator(String created_by) {
        this.created_by = created_by;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = (int) (31 * hash + this.id);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Job)) {
            return false;
        }
        Job other = (Job) object;
        if ((this.id == INVALID_IDENTIFIER && other.id != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.id)) {
            return false;
        }
        return getMaster().getProject().equals(other.getMaster().getProject());
    }

    @Override
    public int compareTo(Job o) {
        return this.startDate.compareTo(o.startDate);
    }
}
