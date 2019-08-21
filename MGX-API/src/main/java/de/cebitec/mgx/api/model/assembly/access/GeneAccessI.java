/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.GeneI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface GeneAccessI {

    public Iterator<GeneI> ByContig(ContigI a) throws MGXException;

    // return dna and aa sequence
    public String[] getSequence(GeneI selectedFeature) throws MGXException;

}
