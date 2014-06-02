package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.gui.datamodel.Sequence;

/**
 *
 * @author sjaenick
 */
public class SequenceDTOFactory extends DTOConversionBase<SequenceI, SequenceDTO> {

    static {
        instance = new SequenceDTOFactory();
    }
    protected static SequenceDTOFactory instance;

    private SequenceDTOFactory() {
    }

    public static SequenceDTOFactory getInstance() {
        return instance;
    }

    @Override
    public SequenceDTO toDTO(SequenceI a) {
        SequenceDTO.Builder b = SequenceDTO.newBuilder()
                .setName(a.getName());
        if (a.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(a.getId());
        }
        if (a.getLength() != -1) {
            b.setLength(a.getLength());
        }
        if (a.getSequence() != null) {
            b.setSequence(a.getSequence());
        }
        return b.build();
    }

    @Override
    public SequenceI toModel(MGXMasterI m, SequenceDTO dto) {
        SequenceI s = new Sequence(m);
        s.setName(dto.getName());
        if (dto.hasId()) {
            s.setId(dto.getId());
        }
        if (dto.hasLength()) {
            s.setLength(dto.getLength());
        }
        if (dto.hasSequence()) {
            s.setSequence(dto.getSequence());
        }

        return s;
    }
}
