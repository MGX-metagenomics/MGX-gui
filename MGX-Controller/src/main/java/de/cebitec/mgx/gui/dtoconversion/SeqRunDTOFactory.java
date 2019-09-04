package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO.Builder;
import de.cebitec.mgx.gui.datamodel.SeqRun;

/**
 *
 * @author sjaenick
 */
public class SeqRunDTOFactory extends DTOConversionBase<SeqRunI, SeqRunDTO> {

    static {
        instance = new SeqRunDTOFactory();
    }
    protected static SeqRunDTOFactory instance;

    private SeqRunDTOFactory() {
    }

    public static SeqRunDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final SeqRunDTO toDTO(SeqRunI s) {
        Builder b = SeqRunDTO.newBuilder()
                .setExtractId(s.getExtractId())
                .setName(s.getName())
                .setIsPaired(s.isPaired())
                .setSequencingTechnology(TermDTOFactory.getInstance().toDTO(s.getSequencingTechnology()))
                .setSequencingMethod(TermDTOFactory.getInstance().toDTO(s.getSequencingMethod()))
                .setSubmittedToInsdc(s.getSubmittedToINSDC());

        // optional fields
        if (s.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(s.getId());
        }

        if (s.getSubmittedToINSDC()) {
            b.setAccession(s.getAccession());
        }

        return b.build();
    }

    @Override
    public final SeqRunI toModel(MGXMasterI m, SeqRunDTO dto) {
        SeqRunI s = new SeqRun(m)
                .setDNAExtractId(dto.getExtractId())
                .setName(dto.getName())
                .setIsPaired(dto.getIsPaired())
                .setSequencingTechnology(TermDTOFactory.getInstance().toModel(m, dto.getSequencingTechnology()))
                .setSequencingMethod(TermDTOFactory.getInstance().toModel(m, dto.getSequencingMethod()))
                .setSubmittedToINSDC(dto.getSubmittedToInsdc());

        if (dto.getSubmittedToInsdc()) {
            s.setAccession(dto.getAccession());
        }

        if (dto.getNumSequences() != 0) {
            s.setNumSequences(dto.getNumSequences());
        }

        s.setId(dto.getId());
        return s;
    }
}
