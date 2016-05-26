/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;
import java.util.Collection;
import java.util.Date;

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

    public abstract Collection<JobParameterI> getParameters();

    public abstract JobI setParameters(Collection<JobParameterI> parameters);

    public abstract Date getFinishDate();

    public abstract JobI setFinishDate(Date finishDate);

    public abstract Date getStartDate();

    public abstract JobI setStartDate(Date startDate);

    public abstract SeqRunI getSeqrun();

    public abstract JobI setSeqrun(SeqRunI run);

    public abstract String getCreator();

    public abstract JobI setCreator(String created_by);

    @Override
    public abstract boolean equals(Object object);

    @Override
    public abstract int compareTo(JobI o);
    
}
