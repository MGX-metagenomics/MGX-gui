package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.dto.dto.SeqRunDTO;

/**
 *
 * @author sj
 */
public class SeqRun extends ModelBase<SeqRunDTO> {
    
    public SeqRun(MGXMaster master, SeqRunDTO dto) {
        super(master, dto);
    }
}
