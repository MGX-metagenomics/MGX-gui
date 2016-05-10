package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.ObservationAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.dtoconversion.ObservationDTOFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class ObservationAccess implements ObservationAccessI {

    private final MGXDTOMaster dtomaster;
    private final MGXMasterI master;

    public ObservationAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        this.dtomaster = dtomaster;
        this.master = master;
        if (master.isDeleted()) {
            throw new MGXLoggedoutException("You are disconnected.");
        }
    }

    @Override
    public Iterator<ObservationI> ByRead(SequenceI s) throws MGXException {
        Collection<ObservationI> ret = new ArrayList<>();
        try {
            Iterator<ObservationDTO> iter = getDTOmaster().Observation().ByRead(s.getId());
            while (iter.hasNext()) {
                ObservationI obs = ObservationDTOFactory.getInstance().toModel(getMaster(), iter.next());
                ret.add(obs);
            }
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }

        return ret.iterator();
    }

    @Override
    public ObservationI create(SequenceI seq, AttributeI attr, int start, int stop) throws MGXException {

        if (start < 0 || stop < 0 || start >= seq.getLength() || stop >= seq.getLength()) {
            throw new MGXException("Coordinates cannot point outside of sequence.");
        }
        if (seq == null || seq.getId() == Identifiable.INVALID_IDENTIFIER) {
            throw new MGXException("Invalid sequence");
        }
        if (attr == null || attr.getId() == Identifiable.INVALID_IDENTIFIER) {
            throw new MGXException("Invalid attribute");
        }
        
        ObservationI obj = new Observation(getMaster());
        obj.setAttributeName(attr.getValue());
        obj.setAttributeTypeName(attr.getAttributeType().getName());
        obj.setStart(start);
        obj.setStop(stop);

        ObservationDTO dto = ObservationDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Observation().create(seq.getId(), attr.getId(), dto);
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
        return obj;
    }

    private MGXDTOMaster getDTOmaster() {
        return dtomaster;
    }

    private MGXMasterI getMaster() {
        return master;
    }
}
