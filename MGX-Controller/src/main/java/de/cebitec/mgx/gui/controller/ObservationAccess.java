package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.ObservationAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.ObservationDTOFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class ObservationAccess extends AccessBase<ObservationI> implements ObservationAccessI {

    public ObservationAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        super(master, dtomaster);
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
    public ObservationI create(ObservationI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public ObservationI fetch(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<ObservationI> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(ObservationI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task<ObservationI> delete(ObservationI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
