package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class SeqRun extends ModelBase<SeqRunDTO> {
    
    long id;
    
    public SeqRun(MGXMaster master, SeqRunDTO dto) {
        super(master, dto);
        id = dto.getId();
    }

    @Override
    public void delete() {
        try {
            getMaster().SeqRun().delete(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(SeqRun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SeqRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
