package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.dto.dto.HabitatDTO;

/**
 *
 * @author sj
 */
public class Habitat extends ModelBase<HabitatDTO> {

    public Habitat(MGXMaster master, HabitatDTO dto) {
        super(master, dto);
    }
}