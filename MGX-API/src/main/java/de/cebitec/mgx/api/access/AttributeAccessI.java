/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.tree.TreeI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface AttributeAccessI {

    public AttributeI create(JobI job, AttributeTypeI attrType, String attrValue, AttributeI parent) throws MGXException;

    public TaskI<AttributeI> delete(AttributeI attr) throws MGXException;

    public AttributeI fetch(long id) throws MGXException;

    public Iterator<AttributeI> ByJob(JobI job) throws MGXException;

    public Iterator<AttributeI> BySeqRun(final SeqRunI seqrun) throws MGXException;

    public DistributionI<Long> getDistribution(AttributeTypeI attrType, JobI job) throws MGXException;

    public TreeI<Long> getHierarchy(AttributeTypeI attrType, JobI job) throws MGXException;
    
    // as getDistribution(), but only for sequences annotated with filterAttribute
    public DistributionI<Long> getFilteredDistribution(AttributeI filterAttribute, AttributeTypeI first, JobI second) throws MGXException;

    //public Iterator<SequenceI> search(String term, boolean exact, SeqRunI[] targets) throws MGXException;
    public Iterator<String> find(String term, SeqRunI run) throws MGXException;

    public Iterator<SequenceI> search(String term, boolean exact, SeqRunI run) throws MGXException;
}
