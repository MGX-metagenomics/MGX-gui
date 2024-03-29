/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface ContigAccessI {
    
    public ContigI fetch(long id) throws MGXException;
    
    public Iterator<ContigI> fetchall() throws MGXException;

    public Iterator<ContigI> ByBin(BinI bin) throws MGXException;
    
    public SequenceI getDNASequence(ContigI gene) throws MGXException;

}
