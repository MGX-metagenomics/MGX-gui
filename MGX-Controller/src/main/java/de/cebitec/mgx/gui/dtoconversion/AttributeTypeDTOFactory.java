package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.gui.datamodel.AttributeType;

/**
 *
 * @author sj
 */
public class AttributeTypeDTOFactory extends DTOConversionBase<AttributeType, AttributeTypeDTO> {

    protected final static AttributeTypeDTOFactory instance = new AttributeTypeDTOFactory();

    private AttributeTypeDTOFactory() {
    }

    public static AttributeTypeDTOFactory getInstance() {
        return instance;
    }

    @Override
    public AttributeTypeDTO toDTO(AttributeType a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AttributeType toModel(AttributeTypeDTO dto) {
        AttributeType at = new AttributeType();
        at.setId(dto.getId());
        at.setName(dto.getName());
        at.setValueType(dto.getValueType());
        return at;
    }
}
