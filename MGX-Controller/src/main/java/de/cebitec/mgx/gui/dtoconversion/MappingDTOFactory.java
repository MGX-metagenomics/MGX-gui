package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.gui.datamodel.Mapping;

/**
 *
 * @author sjaenick
 */
public class MappingDTOFactory extends DTOConversionBase<MappingI, MappingDTO> {

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
    public MappingDTO toDTO(MappingI a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MappingI toModel(MGXMasterI master, MappingDTO dto) {
        MappingI m = new Mapping(master);
        m.setId(dto.getId());
        m.setJobID(dto.getJobId());
        m.setReferenceID(dto.getReferenceId());
        m.setSeqrunID(dto.getRunId());
        return m;
    }
}
