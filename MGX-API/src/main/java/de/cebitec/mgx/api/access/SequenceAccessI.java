/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author sj
 */
public interface SequenceAccessI { //extends AccessBaseI<SequenceI> {
    
    public SequenceI fetch(long id) throws MGXException;

    public Iterator<SequenceI> fetchByIds(long[] id) throws MGXException;

    public void fetchSeqData(Iterable<SequenceI> sequences) throws MGXException;

    public SequenceI fetch(SeqRunI seqrun, String seqName) throws MGXException;

    public void sendSequences(SeqRunI seqrun, SeqReaderI<? extends DNASequenceI> reader) throws MGXException;

    public Iterator<Long> fetchSequenceIDs(AttributeI attr) throws MGXException;

    public void downloadSequencesForAttributes(Set<AttributeI> attrs, SeqWriterI<DNASequenceI> writer, boolean closeWriter) throws MGXException;

    public DownloadBaseI createDownloaderByAttributes(Set<AttributeI> value, SeqWriterI<DNASequenceI> writer, boolean closeWriter) throws MGXException;

    public UploadBaseI createUploader(SeqRunI seqrun, SeqReaderI<? extends DNASequenceI> reader) throws MGXException;

    public DownloadBaseI createDownloader(SeqRunI seqrun, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) throws MGXException;
}
