package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.gui.datamodel.Attribute;

/**
 *
 * @author sjaenick
 */
public class AttributeDTOFactory extends DTOConversionBase<Attribute, AttributeDTO> {

    static {
        instance = new AttributeDTOFactory();
    }
    protected final static AttributeDTOFactory instance;

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
        Attribute a = new Attribute();
        a.setId(dto.getId());
        a.setType(dto.getType());
        // FIXME: value!
        return a;
    }
}
