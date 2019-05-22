/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.common.JobState;
import java.awt.datatransfer.DataFlavor;
import java.util.Date;

/**
 *
 * @author sj
 */
public abstract class AssemblyJobI extends Identifiable<AssemblyJobI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(AssemblyJobI.class, "AssemblyJobI");

    public AssemblyJobI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract SeqRunI[] getSeqRuns();

    public abstract void setSeqRuns(SeqRunI[] runs);

    public abstract String getCreator();

    public abstract void setCreator(String created_by);

    public abstract JobState getStatus();

    public abstract void setStatus(JobState status);

    public abstract Date getFinishDate();

    public abstract void setFinishDate(Date finishDate);

    public abstract Date getStartDate();

    public abstract void setStartDate(Date startDate);
}
