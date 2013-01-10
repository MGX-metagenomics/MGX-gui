package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Identifiable;

/**
 *
 * @author sjaenick
 */
public class AttributeDTOFactory extends DTOConversionBase<Attribute, AttributeDTO> {

    protected final static AttributeDTOFactory instance = new AttributeDTOFactory();

    private AttributeDTOFactory() {
    }

    public static AttributeDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final AttributeDTO toDTO(Attribute a) {
        Builder b = AttributeDTO.newBuilder();
        b.setAttributeTypeId(a.getAttributeType().getId())
                .setValue(a.getValue())
                .setJobid(a.getJobId());
        
        if (a.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(a.getId());
        }
        
        if (a.getParentID() != Identifiable.INVALID_IDENTIFIER) {
            b.setParentId(a.getParentID());
        }
        return b.build();
    }

    @Override
    public final Attribute toModel(AttributeDTO dto) {
        Attribute a = new Attribute()
            .setJobId(dto.getJobid())
            .setValue(dto.getValue());
        if (dto.hasParentId()) {
            a.setParentID(dto.getParentId());
        }
        a.setId(dto.getId());
        return a;
    }
}
