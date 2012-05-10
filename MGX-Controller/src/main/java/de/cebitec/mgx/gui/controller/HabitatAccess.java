package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.dtoconversion.HabitatDTOFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class HabitatAccess extends AccessBase<Habitat> {

    @Override
    public long create(Habitat obj) {
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        long id = ModelBase.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Habitat().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        Habitat ret = HabitatDTOFactory.getInstance().toModel(h);
        ret.setMaster(this.getMaster());
        return ret;
    }

    @Override
    public List<Habitat> fetchall() {
        List<Habitat> all = new ArrayList<>();
        try {
            for (HabitatDTO dto : getDTOmaster().Habitat().fetchall()) {
                Habitat h = HabitatDTOFactory.getInstance().toModel(dto);
                h.setMaster(this.getMaster());
                all.add(h);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    @Override
    public void update(Habitat obj) {
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Habitat().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(long id) {
        try {
            getDTOmaster().Habitat().delete(id);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
