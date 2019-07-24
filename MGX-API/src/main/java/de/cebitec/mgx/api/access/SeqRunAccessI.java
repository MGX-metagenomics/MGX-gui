/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.TermI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.api.model.qc.QCResultI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sj
 */
public interface SeqRunAccessI extends AccessBaseI<SeqRunI> {

    public SeqRunI create(DNAExtractI extract, String name, TermI seqMethod, TermI seqTechnology, boolean submittedINSDC, boolean isPaired, String accession) throws MGXException;

    public Map<JobI, Set<AttributeTypeI>> getJobsAndAttributeTypes(SeqRunI run) throws MGXException;

    public Iterator<SeqRunI> ByExtract(DNAExtractI extract) throws MGXException;

    public List<QCResultI> getQC(SeqRunI run) throws MGXException;

    public Iterator<SeqRunI> ByJob(JobI job) throws MGXException;

    public Iterator<AssembledSeqRunI> ByAssembly(AssemblyI asm) throws MGXException;

    public boolean hasQuality(SeqRunI seqrun) throws MGXException;

}
