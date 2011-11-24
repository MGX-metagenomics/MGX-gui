package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class Habitat extends ModelBase<HabitatDTO> {
    
    long id;
    private String name = null;

    public Habitat(MGXMaster master, HabitatDTO dto) {
        super(master, dto);
    }

    @Override
    public void delete() {
        try {
            getMaster().Habitat().delete(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(Habitat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(Habitat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return name;
    }
    
    
}