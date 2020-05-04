package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.gui.datamodel.Observation;

/**
 *
 * @author sj
 */
public class ObservationDTOFactory extends DTOConversionBase<ObservationI, ObservationDTO> {

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
    public ObservationDTO toDTO(ObservationI obs) {
        return ObservationDTO.newBuilder()
                .setAttributeName(obs.getAttributeName())
                .setAttributeTypeValue(obs.getAttributeTypeName())
                .setStart(obs.getStart())
                .setStop(obs.getStop())
                .build();
    }

    @Override
    public ObservationI toModel(MGXMasterI m, ObservationDTO dto) {
        ObservationI o = new Observation();
        o.setStart(dto.getStart());
        o.setStop(dto.getStop());
        o.setAttributeName(dto.getAttributeName());
        o.setAttributeTypeName(dto.getAttributeTypeValue());
        return o;
    }
}
