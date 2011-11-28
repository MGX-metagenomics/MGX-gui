package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.SeqRunDTO;
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

    private SeqRunDTOFactory() {}

    public static SeqRunDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final SeqRunDTO toDTO(SeqRun s) {
        return SeqRunDTO.newBuilder()
                .setId(s.getId())
                .setExtractId(s.getExtract().getId())
                .setAccession(s.getAccession())
                .setSubmittedToInsdc(s.getSubmittedToINSDC())
                .setSequencingMethod(s.getSequencingMethod())
                .setSequencingTechnology(s.getSequencingTechnology())
                .build();
    }

    @Override
    public final SeqRun toModel(SeqRunDTO dto) {
        SeqRun s = new SeqRun()
                .setAccession(dto.getAccession())
                .setSubmittedToINSDC(dto.getSubmittedToInsdc())
                .setSequencingMethod(dto.getSequencingMethod())
                .setSequencingTechnology(dto.getSequencingTechnology());

        if (dto.hasId())
            s.setId(dto.getId());

        return s;
        // cannot set sample here
    }
}
