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
        return new AttributeType(dto.getId(), dto.getName(), dto.getValueType().charAt(0), dto.getStructure().charAt(0));
    }

//    public List<AttributeType> toModelList(AttributeTypeDTOList dtolist) {
//        List<AttributeType> all = new ArrayList<>();
//        for (AttributeTypeDTO dto : dtolist.getAttributeTypeList()) {
//            AttributeType aType = toModel(dto);
//            all.add(aType);
//        }
//        return all;
//    }
}
