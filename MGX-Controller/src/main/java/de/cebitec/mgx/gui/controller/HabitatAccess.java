package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.dtoconversion.HabitatDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class HabitatAccess extends AccessBase<Habitat> {

    @Override
    public long create(Habitat obj) {
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Habitat().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        return id;
    }

    @Override
    public Habitat fetch(long id) {
        HabitatDTO h = null;
        try {
            h = getDTOmaster().Habitat().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        Habitat ret = HabitatDTOFactory.getInstance().toModel(h);
        ret.setMaster(this.getMaster());
        return ret;
    }

    @Override
    public Iterator<Habitat> fetchall() {
        try {
            Iterator<HabitatDTO> fetchall = getDTOmaster().Habitat().fetchall();
            return new BaseIterator<HabitatDTO, Habitat>(fetchall) {
                @Override
                public Habitat next() {
                    Habitat h = HabitatDTOFactory.getInstance().toModel(iter.next());
                    h.setMaster(getMaster());
                    return h;
                }
            };

        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    @Override
    public void update(Habitat obj) {
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Habitat().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_MODIFIED, null, obj);
    }

    @Override
    public boolean delete(Habitat obj) {
        boolean ret;
        try {
            ret = getDTOmaster().Habitat().delete(obj.getId());
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
        obj.firePropertyChange(ModelBase.OBJECT_DELETED, obj, null);
        return ret;
    }
}
