package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.gui.datamodel.Observation;

/**
 *
 * @author sj
 */
class ObservationDTOFactory extends DTOConversionBase<Observation, ObservationDTO> {

    static {
        instance = new ObservationDTOFactory();
    }
    protected static ObservationDTOFactory instance;

    private ObservationDTOFactory() {
    }

    public static ObservationDTOFactory getInstance() {
        return instance;
    }

    @Override
    public ObservationDTO toDTO(Observation a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Observation toModel(ObservationDTO dto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
