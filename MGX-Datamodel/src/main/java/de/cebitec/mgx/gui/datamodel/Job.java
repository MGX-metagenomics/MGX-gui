package de.cebitec.mgx.gui.datamodel;

import java.util.Date;

/**
 *
 * @author sjaenick
 */
public class Job extends Identifiable {

    protected SeqRun seqrun;
    //
    protected Tool tool;
    protected String created_by;
    //
    protected Date startDate = null;
    //
    protected Date finishDate = null;
    //
    protected String parameters;
    private int jobstate;

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

    public Job setTool(Tool tool) {
        this.tool = tool;
        return this;
    }

    public String getParameters() {
        return parameters;
    }

    public Job setParameters(String parameters) {
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

    public Job setSeqrun(SeqRun seqrun) {
        this.seqrun = seqrun;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Job)) {
            return false;
        }
        Job other = (Job) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
