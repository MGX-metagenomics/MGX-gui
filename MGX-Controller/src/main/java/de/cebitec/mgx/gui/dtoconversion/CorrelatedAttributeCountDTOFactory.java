package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.dto.dto.CorrelatedAttributeCount;
import java.util.AbstractMap;
import java.util.Map;

/**
 *
 * @author sj
 */
public class CorrelatedAttributeCountDTOFactory extends DTOConversionBase<Map.Entry<Pair<AttributeI, AttributeI>, Number>, CorrelatedAttributeCount> {

    protected final static CorrelatedAttributeCountDTOFactory instance = new CorrelatedAttributeCountDTOFactory();

    private CorrelatedAttributeCountDTOFactory() {
    }

    public static CorrelatedAttributeCountDTOFactory getInstance() {
        return instance;
    }

    @Override
    public CorrelatedAttributeCount toDTO(Map.Entry<Pair<AttributeI, AttributeI>, Number> e) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Map.Entry<Pair<AttributeI, AttributeI>, Number> toModel(MGXMasterI m, CorrelatedAttributeCount dto) {
        AttributeI first = AttributeDTOFactory.getInstance().toModel(m, dto.getRestrictedAttribute());
        AttributeI second = AttributeDTOFactory.getInstance().toModel(m, dto.getAttribute());
        Pair<AttributeI, AttributeI> pair = new Pair<>(first, second);
        return new AbstractMap.SimpleEntry<Pair<AttributeI, AttributeI>, Number>(pair, Long.valueOf(dto.getCount()));
    }
}
