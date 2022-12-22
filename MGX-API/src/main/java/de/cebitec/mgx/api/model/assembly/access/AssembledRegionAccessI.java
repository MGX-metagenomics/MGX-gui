/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly.access;

import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.BinSearchResultI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author sj
 */
public interface AssembledRegionAccessI {

    public Iterator<AssembledRegionI> ByContig(ContigI a) throws MGXException;

    public SequenceI getDNASequence(AssembledRegionI gene) throws MGXException;

    public DownloadBaseI createDownloaderByAttributes(Set<AttributeI> value, SeqWriterI<DNASequenceI> writer, boolean closeWriter, Set<String> seenGeneNames) throws MGXException;
    
    public Iterator<BinSearchResultI> search(BinI bin, String term) throws MGXException;

}
