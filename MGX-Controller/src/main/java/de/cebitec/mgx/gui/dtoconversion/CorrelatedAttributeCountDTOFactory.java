package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.CorrelatedAttributeCount;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import java.util.AbstractMap;
import java.util.Map;

/**
 *
 * @author sj
 */
public class CorrelatedAttributeCountDTOFactory extends DTOConversionBase<Map.Entry<Pair<Attribute, Attribute>, Number>, CorrelatedAttributeCount> {

    protected final static CorrelatedAttributeCountDTOFactory instance = new CorrelatedAttributeCountDTOFactory();

    private CorrelatedAttributeCountDTOFactory() {
    }

    public static CorrelatedAttributeCountDTOFactory getInstance() {
        return instance;
    }

    @Override
    public CorrelatedAttributeCount toDTO(Map.Entry<Pair<Attribute, Attribute>, Number> e) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Map.Entry<Pair<Attribute, Attribute>, Number> toModel(CorrelatedAttributeCount dto) {
        Attribute first = AttributeDTOFactory.getInstance().toModel(dto.getRestrictedAttribute());
        Attribute second = AttributeDTOFactory.getInstance().toModel(dto.getAttribute());
        Pair<Attribute, Attribute> pair = new Pair<>(first, second);
        return new AbstractMap.SimpleEntry<Pair<Attribute, Attribute>, Number>(pair, Long.valueOf(dto.getCount()));
    }
}
