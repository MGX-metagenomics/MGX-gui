package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class DNAExtract extends ModelBase<DNAExtractDTO> {
    
    long id;
    
    public DNAExtract(MGXMaster master, DNAExtractDTO dto) {
        super(master, dto);
        dto.getId();
    }
    
    @Override
    public void delete() {
        try {
            getMaster().DNAExtract().delete(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(DNAExtract.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(DNAExtract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
