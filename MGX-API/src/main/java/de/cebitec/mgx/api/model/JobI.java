/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.common.JobState;
import java.awt.datatransfer.DataFlavor;
import java.util.Date;
import java.util.List;

/**
 *
 * @author sj
 */
public abstract class JobI extends Identifiable<JobI> {

    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(JobI.class, "JobI");

    public JobI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract JobState getStatus();

    public abstract JobI setStatus(JobState status);

    public abstract ToolI getTool();

    public abstract JobI setTool(ToolI t);

    public abstract List<JobParameterI> getParameters();

    public abstract JobI setParameters(List<JobParameterI> parameters);

    public abstract Date getFinishDate();

    public abstract JobI setFinishDate(Date finishDate);

    public abstract Date getStartDate();

    public abstract JobI setStartDate(Date startDate);

    public abstract SeqRunI[] getSeqruns();

    public abstract JobI setSeqruns(SeqRunI[] run);

    public abstract AssemblyI getAssembly();
    
    public abstract JobI setAssembly(AssemblyI asm);

    public abstract String getCreator();

    public abstract JobI setCreator(String created_by);

    @Override
    public abstract boolean equals(Object object);

    @Override
    public abstract int compareTo(JobI o);

}
