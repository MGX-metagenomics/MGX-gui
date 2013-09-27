package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.gui.datamodel.Mapping;

/**
 *
 * @author sjaenick
 */
public class MappingDTOFactory extends DTOConversionBase<Mapping, MappingDTO> {

    static {
        instance = new MappingDTOFactory();
    }
    protected static MappingDTOFactory instance;

    private MappingDTOFactory() {
    }

    public static MappingDTOFactory getInstance() {
        return instance;
    }

    @Override
    public MappingDTO toDTO(Mapping a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mapping toModel(MappingDTO dto) {
        Mapping m = new Mapping();
        m.setId(dto.getId());
        m.setJobID(dto.getJobId());
        m.setReferenceID(dto.getReferenceId());
        m.setSeqrunID(dto.getRunId());
        return m;
    }
}
