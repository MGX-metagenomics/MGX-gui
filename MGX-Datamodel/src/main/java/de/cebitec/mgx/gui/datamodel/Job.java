package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import java.util.Date;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class Job extends JobI {

    protected SeqRunI seqrun;
    //
    protected ToolI tool;
    protected String created_by;
    //
    protected Date startDate = null;
    //
    protected Date finishDate = null;
    //
    protected List<JobParameterI> parameters = null;
    private int jobstate = JobState.CREATED.getValue();

    public Job(MGXMasterI m) {
        super(m);
    }

    @Override
    public JobState getStatus() {
        return JobState.values()[jobstate];
    }

    @Override
    public Job setStatus(JobState status) {
        jobstate = status.getValue();
        return this;
    }

    @Override
    public ToolI getTool() {
        return tool;
    }

    @Override
    public Job setTool(ToolI t) {
        assert t != null;
        assert this.tool == null;
        this.tool = t;
        return this;
    }

    @Override
    public List<JobParameterI> getParameters() {
        return parameters;
    }

    @Override
    public Job setParameters(List<JobParameterI> parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    public Date getFinishDate() {
        return finishDate;
    }

    @Override
    public Job setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
        return this;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Job setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    @Override
    public SeqRunI getSeqrun() {
        return seqrun;
    }

    @Override
    public JobI setSeqrun(SeqRunI run) {
        assert run != null;
        assert this.seqrun == null;
        this.seqrun = run;
        return this;
    }

    @Override
    public String getCreator() {
        return created_by;
    }

    @Override
    public Job setCreator(String created_by) {
        this.created_by = created_by;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JobI)) {
            return false;
        }
        JobI other = (JobI) object;
        if ((this.id == INVALID_IDENTIFIER && other.getId() != INVALID_IDENTIFIER) || (this.id != INVALID_IDENTIFIER && this.id != other.getId())) {
            return false;
        }
        return getStatus() == other.getStatus() && getMaster().getProject().equals(other.getMaster().getProject()) ;
    }

    @Override
    public int compareTo(JobI o) {
        return this.startDate.compareTo(o.getStartDate());
    }
}
