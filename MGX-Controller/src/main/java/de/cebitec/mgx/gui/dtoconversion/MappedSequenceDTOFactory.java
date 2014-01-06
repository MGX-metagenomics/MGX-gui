package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.gui.datamodel.MappedSequence;

/**
 *
 * @author sjaenick
 */
public class MappedSequenceDTOFactory extends DTOConversionBase<MappedSequence, MappedSequenceDTO> {

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
    public MappedSequenceDTO toDTO(MappedSequence a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public MappedSequence toModel(MappedSequenceDTO dto) {
        return new MappedSequence(dto.getSeqId(), dto.getStart(), dto.getStop(), dto.getIdentity());
    }

}
