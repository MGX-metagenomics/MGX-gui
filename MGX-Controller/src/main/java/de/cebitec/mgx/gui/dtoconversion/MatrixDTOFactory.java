package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.dto.dto.AttributeCorrelation;
import de.cebitec.mgx.dto.dto.CorrelatedAttributeCount;
import de.cebitec.mgx.gui.datamodel.misc.Matrix;

/**
 *
 * @author sj
 */
public class MatrixDTOFactory extends DTOConversionBase<Matrix<AttributeI, AttributeI>, AttributeCorrelation> {

    protected final static MatrixDTOFactory instance = new MatrixDTOFactory();

    private MatrixDTOFactory() {
    }

    public static MatrixDTOFactory getInstance() {
        return instance;
    }

    @Override
    public AttributeCorrelation toDTO(Matrix<AttributeI, AttributeI> a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Matrix<AttributeI, AttributeI> toModel(MGXMasterI m, AttributeCorrelation dto) {
        Matrix<AttributeI, AttributeI> ret = new Matrix<>();
        for (CorrelatedAttributeCount cac : dto.getEntryList()) {
            AttributeI first = AttributeDTOFactory.getInstance().toModel(m, cac.getRestrictedAttribute());
            AttributeI second = AttributeDTOFactory.getInstance().toModel(m, cac.getAttribute());
            ret.addData(first, second, cac.getCount());
        }
        return ret;
    }
}
