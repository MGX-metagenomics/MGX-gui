package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Attribute;

/**
 *
 * @author sjaenick
 */
public class AttributeDTOFactory extends DTOConversionBase<AttributeI, AttributeDTO> {

    protected final static AttributeDTOFactory instance = new AttributeDTOFactory();

    private AttributeDTOFactory() {
    }

    public static AttributeDTOFactory getInstance() {
        return instance;
    }

    @Override
    public final AttributeDTO toDTO(AttributeI a) {
        Builder b = AttributeDTO.newBuilder();
        b.setAttributeTypeId(a.getAttributeType().getId())
                .setValue(a.getValue())
                .setJobId(a.getJobId());

        if (a.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(a.getId());
        }

        if (a.getParentID() != Identifiable.INVALID_IDENTIFIER) {
            b.setParentId(a.getParentID());
        }
        return b.build();
    }

    @Override
    public final AttributeI toModel(MGXMasterI m, AttributeDTO dto) {
        AttributeI a = new Attribute()
                .setJobId(dto.getJobId());
        a.setValue(dto.getValue());
        if (dto.getParentId() != 0) {
            a.setParentID(dto.getParentId());
        }
        a.setId(dto.getId());
        return a;
    }
}
