/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.api.model.assembly.GeneCoverageI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface GeneCoverageAccessI {

    public Iterator<GeneCoverageI> ByGene(RegionI a) throws MGXException;

}
