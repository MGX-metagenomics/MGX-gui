package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO.Builder;
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
                .setExtractId(s.getExtract().getId())
                .setSequencingTechnology(s.getSequencingTechnology())
                .setSequencingMethod(s.getSequencingMethod())
                .setSubmittedToInsdc(s.getSubmittedToINSDC());

        // optional fields
        if (s.getId() != null) {
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
                .setSequencingTechnology(dto.getSequencingTechnology())
                .setSequencingMethod(dto.getSequencingMethod())
                .setSubmittedToINSDC(dto.getSubmittedToInsdc());

        if (dto.getSubmittedToInsdc()) {
            s.setAccession(dto.getAccession());
        }

        if (dto.hasId()) {
            s.setId(dto.getId());
        }

        return s;
        // cannot set extract here
    }
}
