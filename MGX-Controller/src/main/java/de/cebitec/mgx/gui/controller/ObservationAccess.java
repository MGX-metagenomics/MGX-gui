package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.gui.dtoconversion.ObservationDTOFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.Exceptions;


/**
 *
 * @author sj
 */
public class ObservationAccess extends AccessBase<Observation> {

    public Collection<Observation> ByRead(Sequence s) {
        Collection<Observation> ret = new ArrayList<>();
        try {
            for (ObservationDTO dto : getDTOmaster().Observation().ByRead(s.getId())) {
                ret.add(ObservationDTOFactory.getInstance().toModel(dto));
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return ret;
    }
    @Override
    public long create(Observation obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Observation fetch(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<Observation> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(Observation obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void delete(Observation obj) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
