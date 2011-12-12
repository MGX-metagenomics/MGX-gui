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
        b = b.setSampleId(d.getSample().getId());

        b = b.setMethod(d.getMethod());
        b = b.setProtocolName(d.getProtocol());

        // optional fields
        b = b.setFivePrimePrimer(d.getFivePrimer());
        b = b.setThreePrimePrimer(d.getThreePrimer());
        b = b.setTargetGene(d.getTargetGene());
        b = b.setTargetFragment(d.getTargetFragment());
        b = b.setDescription(d.getDescription());

        return b.build();
    }

    @Override
    public final DNAExtract toModel(DNAExtractDTO dto) {
        DNAExtract d = new DNAExtract();

        if (dto.hasId()) {
            d.setId(dto.getId());
        }

        d.setMethod(dto.getMethod());
        d.setProtocol(dto.getProtocolName());

        if (dto.hasFivePrimePrimer()) {
            d.setFivePrimer(dto.getFivePrimePrimer());
        }
        if (dto.hasThreePrimePrimer()) {
            d.setThreePrimer(dto.getThreePrimePrimer());
        }
        if (dto.hasTargetGene()) {
            d.setTargetGene(dto.getTargetGene());
        }
        if (dto.hasTargetFragment()) {
            d.setTargetFragment(dto.getTargetFragment());
        }
        if (dto.hasDescription()) {
            d.setDescription(dto.getDescription());
        }

        return d;
    }
}
