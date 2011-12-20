package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.gui.datamodel.Habitat;
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
    public Long create(Habitat obj) {
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        Long id = null;
        try {
            id = getDTOmaster().Habitat().create(dto);
        } catch (MGXServerException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        obj.setId(id);
        return id;
    }

    @Override
    public Habitat fetch(Long id) {
        HabitatDTO h = null;
        try {
            h = getDTOmaster().Habitat().fetch(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return HabitatDTOFactory.getInstance().toModel(h);
    }

    @Override
    public List<Habitat> fetchall() {
        List<Habitat> all = new ArrayList<Habitat>();
        try {
            for (HabitatDTO dto : getDTOmaster().Habitat().fetchall()) {
                all.add(HabitatDTOFactory.getInstance().toModel(dto));
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    @Override
    public void update(Habitat obj) {
        HabitatDTO dto = HabitatDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Habitat().update(dto);
        } catch (MGXServerException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            getDTOmaster().Habitat().delete(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(HabitatAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
