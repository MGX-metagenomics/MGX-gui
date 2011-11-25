package de.cebitec.mgx.gui.dtoadapter;

import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.gui.datamodel.Attribute;

/**
 *
 * @author sjaenick
 */
public class AttributeDTOFactory extends DTOConversionBase<Attribute, AttributeDTO> {

    @Override
    public final AttributeDTO toDTO(Attribute a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final Attribute toModel(AttributeDTO dto) {
        Attribute a = new Attribute();
        a.setType(dto.getType());
        // FIXME: value!
        return a;
    }

}
