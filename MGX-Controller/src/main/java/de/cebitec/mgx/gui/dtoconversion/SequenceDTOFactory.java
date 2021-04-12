package de.cebitec.mgx.gui.dtoconversion;

import com.google.protobuf.ByteString;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.seqcompression.FourBitEncoder;

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
            byte[] enc = FourBitEncoder.encode(a.getSequence().getBytes());
            b.setSequence(ByteString.copyFrom(enc));
        }
        return b.build();
    }

    @Override
    public SequenceI toModel(MGXMasterI m, SequenceDTO dto) {
        SequenceI s = new Sequence();
        s.setName(dto.getName());
        if (dto.getId() != 0) {
            s.setId(dto.getId());
        }
        if (dto.getLength() != 0) {
            s.setLength(dto.getLength());
        }
        if (!dto.getSequence().isEmpty()) {
            byte[] dec = FourBitEncoder.decode(dto.getSequence().toByteArray());
            s.setSequence(new String(dec));
        }

        return s;
    }
}
