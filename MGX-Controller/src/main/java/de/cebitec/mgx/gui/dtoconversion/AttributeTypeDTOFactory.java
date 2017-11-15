package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.gui.datamodel.AttributeType;

/**
 *
 * @author sj
 */
public class AttributeTypeDTOFactory extends DTOConversionBase<AttributeTypeI, AttributeTypeDTO> {

    protected final static AttributeTypeDTOFactory instance = new AttributeTypeDTOFactory();

    private AttributeTypeDTOFactory() {
    }

    public static AttributeTypeDTOFactory getInstance() {
        return instance;
    }

    @Override
    public AttributeTypeDTO toDTO(AttributeTypeI a) {
        return AttributeTypeDTO.newBuilder()
                .setName(a.getName())
                .setStructure(String.valueOf(a.getStructure()))
                .setValueType(String.valueOf(a.getValueType()))
                .build();
    }

    @Override
    public AttributeTypeI toModel(MGXMasterI m, AttributeTypeDTO dto) {
        return new AttributeType(m, dto.getId(), dto.getName(), dto.getValueType().charAt(0), dto.getStructure().charAt(0));
    }
}
