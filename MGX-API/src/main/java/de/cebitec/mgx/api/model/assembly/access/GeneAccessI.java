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
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author sj
 */
public interface GeneAccessI {

    public Iterator<GeneI> ByContig(ContigI a) throws MGXException;

    public SequenceI getDNASequence(GeneI gene) throws MGXException;

    public DownloadBaseI createDownloaderByAttributes(Set<AttributeI> value, SeqWriterI<DNASequenceI> writer, boolean closeWriter, Set<String> seenGeneNames) throws MGXException;

}
