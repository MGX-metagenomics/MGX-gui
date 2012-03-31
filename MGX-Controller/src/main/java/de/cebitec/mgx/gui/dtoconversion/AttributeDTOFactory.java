package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.gui.datamodel.Attribute;

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final Attribute toModel(AttributeDTO dto) {
        Attribute a = new Attribute()
            .setJobId(dto.getJobid())
                .setType(AttributeTypeDTOFactory.getInstance().toModel(dto.getAttributeType()))
            .setValue(dto.getValue());
        if (dto.hasParentId())
            a.setParentID(dto.getParentId());
        a.setId(dto.getId());
        return a;
    }
}
