package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.gui.datamodel.MappedSequence;

/**
 *
 * @author sjaenick
 */
public class MappedSequenceDTOFactory extends DTOConversionBase<MappedSequenceI, MappedSequenceDTO> {

    static {
        instance = new MappedSequenceDTOFactory();
    }
    protected static MappedSequenceDTOFactory instance;

    private MappedSequenceDTOFactory() {
    }

    public static MappedSequenceDTOFactory getInstance() {
        return instance;
    }

    @Override
    public MappedSequenceDTO toDTO(MappedSequenceI a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public MappedSequenceI toModel(MGXMasterI master, MappedSequenceDTO dto) {
        return toModel(dto);
    }

    public MappedSequenceI toModel(MappedSequenceDTO dto) {
        return new MappedSequence(dto.getSeqId(), dto.getStart(), dto.getStop(), dto.getIdentity());
    }

}
