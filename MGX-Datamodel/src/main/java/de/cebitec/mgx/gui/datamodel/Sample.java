package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.dto.dto.SampleDTO;

/**
 *
 * @author sj
 */
public class Sample extends ModelBase<SampleDTO> {

    public Sample(MGXMaster master, SampleDTO dto) {
        super(master, dto);
    }
}
