/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public interface MappingAccessI extends AccessBaseI<MappingI> {

    public Iterator<MappedSequenceI> byReferenceInterval(UUID uuid, int from, int to) throws MGXException;

    public UUID openMapping(long id) throws MGXException;

    public void closeMapping(UUID uuid) throws MGXException;

    public Iterator<MappingI> ByReference(MGXReferenceI reference) throws MGXException;

    public Iterator<MappingI> BySeqRun(SeqRunI run) throws MGXException;

    public Iterator<MappingI> ByJob(JobI job) throws MGXException;

    public long getMaxCoverage(UUID sessionUUID) throws MGXException;

    public DownloadBaseI createDownloader(MappingI mapping, OutputStream writer) throws MGXException;

    public long getGenomicCoverage(UUID sessionUUID) throws MGXException;

}
