/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SampleI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface DNAExtractAccessI extends AccessBaseI<DNAExtractI> {
    
    public DNAExtractI create(SampleI sample, String name, String method, 
            String protocol, String primer5, String primer3, 
            String targetGene, String targetFragment, String description) throws MGXException;

    public Iterator<DNAExtractI> BySample(SampleI sample) throws MGXException;

}
