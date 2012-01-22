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
        Attribute a = new Attribute();
        a.setId(dto.getId());
        a.setJobId(dto.getJobid());
        a.setType(AttributeTypeDTOFactory.getInstance().toModel(dto.getType()));
        a.setValue(dto.getValue());
        return a;
    }
}
