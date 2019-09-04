package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.DNAExtractDTO.Builder;
import de.cebitec.mgx.gui.datamodel.DNAExtract;

/**
 *
 * @author sjaenick
 */
public class DNAExtractDTOFactory extends DTOConversionBase<DNAExtractI, DNAExtractDTO> {

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
    public final DNAExtractDTO toDTO(DNAExtractI d) {
        Builder b = DNAExtractDTO.newBuilder();
        if (d.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(d.getId());
        }
        b = b.setName(d.getName());
        b = b.setSampleId(d.getSampleId());

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
    public final DNAExtractI toModel(MGXMasterI m, DNAExtractDTO dto) {
        DNAExtractI d = new DNAExtract(m);

        d.setName(dto.getName());

        d.setSampleId(dto.getSampleId())
                .setMethod(dto.getMethod())
                .setProtocol(dto.getProtocolName());

        if (!dto.getFivePrimePrimer().isEmpty()) {
            d.setFivePrimer(dto.getFivePrimePrimer());
        }
        if (!dto.getThreePrimePrimer().isEmpty()) {
            d.setThreePrimer(dto.getThreePrimePrimer());
        }
        if (!dto.getTargetGene().isEmpty()) {
            d.setTargetGene(dto.getTargetGene());
        }
        if (!dto.getTargetFragment().isEmpty()) {
            d.setTargetFragment(dto.getTargetFragment());
        }
        if (!dto.getDescription().isEmpty()) {
            d.setDescription(dto.getDescription());
        }

        d.setId(dto.getId());
        return d;
    }
}
