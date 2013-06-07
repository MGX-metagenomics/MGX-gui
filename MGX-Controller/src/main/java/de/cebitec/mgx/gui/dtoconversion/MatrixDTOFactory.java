package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.AttributeCorrelation;
import de.cebitec.mgx.dto.dto.CorrelatedAttributeCount;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Matrix;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sj
 */
public class MatrixDTOFactory extends DTOConversionBase<Matrix, AttributeCorrelation> {

    protected final static MatrixDTOFactory instance = new MatrixDTOFactory();

    private MatrixDTOFactory() {
    }

    public static MatrixDTOFactory getInstance() {
        return instance;
    }

    @Override
    public AttributeCorrelation toDTO(Matrix a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Matrix toModel(AttributeCorrelation dto) {
        Map<Pair<Attribute, Attribute>, Number> data = new HashMap<>(dto.getEntryList().size());
        CorrelatedAttributeCountDTOFactory converter = CorrelatedAttributeCountDTOFactory.getInstance();
        for (CorrelatedAttributeCount cac : dto.getEntryList()) {
            Map.Entry<Pair<Attribute, Attribute>, Number> entry = converter.toModel(cac);
            data.put(entry.getKey(), entry.getValue());
        }
        
        return new Matrix(); // FIXME
    }
}
