package de.cebitec.mgx.gui.dtoadapter;

import de.cebitec.mgx.dto.dto.DNAExtractDTO;
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

    private DNAExtractDTOFactory() {}

    public static DNAExtractDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final DNAExtractDTO toDTO(DNAExtract d) {
        return DNAExtractDTO.newBuilder()
                .setId(d.getId())
                .setSampleId(d.getSample().getId())
                .build();
    }

    @Override
    public final DNAExtract toModel(DNAExtractDTO dto) {
        DNAExtract d = new DNAExtract();

        if (dto.hasId())
            d.setId(dto.getId());

        return d;
    }
}
