/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public abstract class MappingAccessI implements AccessBaseI<MappingI> {

    public abstract Iterator<MappedSequenceI> byReferenceInterval(UUID uuid, int from, int to) throws MGXException;

    public abstract UUID openMapping(long id) throws MGXException;

    public abstract void closeMapping(UUID uuid) throws MGXException;

    public abstract Iterator<MappingI> ByReference(MGXReferenceI reference) throws MGXException;

    public abstract Iterator<MappingI> BySeqRun(SeqRunI run) throws MGXException;

    public abstract long getMaxCoverage(UUID sessionUUID) throws MGXException;
}
