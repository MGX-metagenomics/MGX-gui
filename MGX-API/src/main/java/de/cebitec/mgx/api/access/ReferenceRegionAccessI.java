/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface ReferenceRegionAccessI {

    public Iterator<ReferenceRegionI> byReferenceInterval(MGXReferenceI ref, int from, int to) throws MGXException;

}
