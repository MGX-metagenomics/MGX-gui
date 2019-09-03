/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.api.model.assembly.GeneObservationI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface GeneObservationAccessI {

    public Iterator<GeneObservationI> ByGene(GeneI gene) throws MGXException;

}
