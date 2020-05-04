package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.GeneObservationI;
import de.cebitec.mgx.dto.dto.GeneObservationDTO;
import de.cebitec.mgx.gui.datamodel.assembly.GeneObservation;

/**
 *
 * @author sj
 */
public class GeneObservationDTOFactory extends DTOConversionBase<GeneObservationI, GeneObservationDTO> {

    static {
        instance = new GeneObservationDTOFactory();
    }
    protected static GeneObservationDTOFactory instance;

    private GeneObservationDTOFactory() {
    }

    public static GeneObservationDTOFactory getInstance() {
        return instance;
    }

    @Override
    public GeneObservationDTO toDTO(GeneObservationI obs) {
        return GeneObservationDTO.newBuilder()
                .setAttributeName(obs.getAttributeName())
                .setAttributeTypeValue(obs.getAttributeTypeName())
                .setStart(obs.getStart())
                .setStop(obs.getStop())
                .build();
    }

    @Override
    public GeneObservationI toModel(MGXMasterI m, GeneObservationDTO dto) {
        GeneObservationI o = new GeneObservation();
        o.setStart(dto.getStart());
        o.setStop(dto.getStop());
        o.setAttributeName(dto.getAttributeName());
        o.setAttributeTypeName(dto.getAttributeTypeValue());
        return o;
    }
}
