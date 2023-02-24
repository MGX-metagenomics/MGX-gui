package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.common.JobState;
import java.util.Date;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class Job extends JobI {

    protected SeqRunI[] seqruns = null;
    protected AssemblyI assembly = null;
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

    public Job(MGXMasterI m, String creator) {
        super(m);
        this.created_by = creator;
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
    public SeqRunI[] getSeqruns() {
        return seqruns;
    }

    @Override
    public JobI setSeqruns(SeqRunI[] runs) {
        if (assembly != null) {
            throw new RuntimeException("Cannot set seqruns for an assembly job.");
        }
        if (this.seqruns != null) {
            throw new RuntimeException("SeqRuns are already set for this job");
        }
        this.seqruns = runs;
        return this;
    }

    @Override
    public AssemblyI getAssembly() {
        return assembly;
    }

    @Override
    public JobI setAssembly(AssemblyI asm) {
        if (seqruns != null && seqruns.length > 0) {
            throw new RuntimeException("Cannot set assembly for a seqrun job.");
        }
        if (this.assembly != null) {
            throw new RuntimeException("Assembly is already set for this job.");

        }
        this.assembly = asm;
        return this;
    }

    @Override
    public String getCreator() {
        return created_by;
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
        return getStatus() == other.getStatus() && getMaster().getProject().equals(other.getMaster().getProject());
    }

    @Override
    public int compareTo(JobI o) {
        // avoid NPE here
        Date myDate = this.startDate != null ? this.startDate : new Date();
        Date otherDate = o.getStartDate() != null ? o.getStartDate() : new Date();
        return myDate.compareTo(otherDate);
    }
}
