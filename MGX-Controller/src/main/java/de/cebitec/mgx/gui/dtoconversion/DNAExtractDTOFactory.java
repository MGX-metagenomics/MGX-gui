package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.DNAExtractDTO.Builder;
import de.cebitec.mgx.gui.datamodel.DNAExtract;

/**
 *
 * @author sjaenick
 */
public class DNAExtractDTOFactory extends DTOConversionBase<DNAExtract, DNAExtractDTO> {

    static {
        instance = new DNAExtractDTOFactory();
    }
    protected static DNAExtractDTOFactory instance;

    private DNAExtractDTOFactory() {
    }

    public static DNAExtractDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final DNAExtractDTO toDTO(DNAExtract d) {
        Builder b = DNAExtractDTO.newBuilder();
        if (d.getId() != null) {
            b.setId(d.getId());
        }
        return b.setDescription(d.getDescription())
                .setSampleId(d.getSample().getId())
                .build();
    }

    @Override
    public final DNAExtract toModel(DNAExtractDTO dto) {
        DNAExtract d = new DNAExtract();

        if (dto.hasDescription()) {
            d.setDescription(dto.getDescription());
        }

        if (dto.hasId()) {
            d.setId(dto.getId());
        }

        return d;
    }
}
