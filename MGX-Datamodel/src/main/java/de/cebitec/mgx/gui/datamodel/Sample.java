package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class Sample extends ModelBase<SampleDTO> {
    
    long id;

    public Sample(MGXMaster master, SampleDTO dto) {
        super(master, dto);
        id = dto.getId();
    }

    @Override
    public void delete() {
        try {
            getMaster().Sample().delete(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(Sample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(Sample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
