/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.BulkObservation;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SequenceI;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sj
 */
public interface ObservationAccessI {

    public Iterator<ObservationI> ByRead(SequenceI seq) throws MGXException;

    public ObservationI create(SequenceI seq, AttributeI attr, int start, int stop) throws MGXException;

    public void createBulk(List<BulkObservation> obsList) throws MGXException;

    public void delete(SequenceI seq, AttributeI attr, int start, int stop) throws MGXException;

}
