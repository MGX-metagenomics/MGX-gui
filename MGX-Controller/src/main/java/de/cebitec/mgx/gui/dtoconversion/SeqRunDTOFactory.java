package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.SeqRun;

/**
 *
 * @author sjaenick
 */
public class SeqRunDTOFactory extends DTOConversionBase<SeqRun, SeqRunDTO> {

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
    public final SeqRunDTO toDTO(SeqRun s) {
        Builder b = SeqRunDTO.newBuilder()
                .setExtractId(s.getExtractId())
                .setName(s.getName())
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
    public final SeqRun toModel(SeqRunDTO dto) {
        SeqRun s = new SeqRun()
                .setDNAExtractId(dto.getExtractId())
                .setName(dto.getName())
                .setSequencingTechnology(TermDTOFactory.getInstance().toModel(dto.getSequencingTechnology()))
                .setSequencingMethod(TermDTOFactory.getInstance().toModel(dto.getSequencingMethod()))
                .setSubmittedToINSDC(dto.getSubmittedToInsdc());

        if (dto.getSubmittedToInsdc()) {
            s.setAccession(dto.getAccession());
        }

        if (dto.hasNumSequences()) {
            s.setNumSequences(dto.getNumSequences());
        }

        s.setId(dto.getId());
        return s;
    }
}
