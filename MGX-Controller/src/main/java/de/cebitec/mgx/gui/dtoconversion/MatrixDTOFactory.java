package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.AttributeCorrelation;
import de.cebitec.mgx.dto.dto.CorrelatedAttributeCount;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Matrix;

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
    public Matrix<Attribute, Attribute> toModel(AttributeCorrelation dto) {
        Matrix<Attribute, Attribute> ret = new Matrix();
        for (CorrelatedAttributeCount cac : dto.getEntryList()) {
            Attribute first = AttributeDTOFactory.getInstance().toModel(cac.getRestrictedAttribute());
            Attribute second = AttributeDTOFactory.getInstance().toModel(cac.getAttribute());
            ret.addData(first, second, cac.getCount());
        }
        return ret;
    }
}
