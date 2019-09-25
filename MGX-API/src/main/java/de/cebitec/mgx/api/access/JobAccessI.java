/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sj
 */
public interface JobAccessI {

    public JobI create(ToolI tool, List<JobParameterI> params, SeqRunI... seqruns) throws MGXException;

    public JobI create(ToolI tool, List<JobParameterI> params, AssemblyI assembly) throws MGXException;

    public JobI fetch(long id) throws MGXException;

    public Iterator<JobI> fetchall() throws MGXException;

    public void update(JobI obj) throws MGXException;

    public TaskI<JobI> delete(JobI obj) throws MGXException;

    public List<JobI> BySeqRun(SeqRunI run) throws MGXException;

    public List<JobI> ByAssembly(AssemblyI ass) throws MGXException;

    public List<JobI> ByAttributeTypeAndSeqRun(AttributeTypeI attrType, SeqRunI run) throws MGXException;

    public String getErrorMessage(JobI job) throws MGXException;

    public TaskI<JobI> restart(JobI job) throws MGXException;

    public boolean cancel(JobI job) throws MGXException;

    public boolean verify(JobI job) throws MGXException;

    public boolean execute(JobI job) throws MGXException;

    public void runDefaultTools(SeqRunI seqrun) throws MGXException;

}
