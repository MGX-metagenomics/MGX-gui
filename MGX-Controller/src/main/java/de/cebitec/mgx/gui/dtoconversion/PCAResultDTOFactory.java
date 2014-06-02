package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.dto.dto.PCAResultDTO;
import de.cebitec.mgx.dto.dto.PointDTO;
import de.cebitec.mgx.gui.datamodel.misc.PCAResult;

/**
 *
 * @author sjaenick
 */
public class PCAResultDTOFactory extends DTOConversionBase<PCAResultI, PCAResultDTO> {

    static {
        instance = new PCAResultDTOFactory();
    }
    protected static PCAResultDTOFactory instance;

    private PCAResultDTOFactory() {
    }

    public static PCAResultDTOFactory getInstance() {
        return instance;
    }

    @Override
    public PCAResultDTO toDTO(PCAResultI a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public PCAResultI toModel(MGXMasterI m, PCAResultDTO dto) {
        double[] variances = new double[dto.getVarianceCount()];
        int i = 0;
        for (double d : dto.getVarianceList()) {
            variances[i++] = d;
        }
        PCAResult ret = new PCAResult(variances);

        for (PointDTO p : dto.getDatapointList()) {
            ret.addPoint(PointDTOFactory.getInstance().toModel(m, p));
        }

        for (PointDTO p : dto.getLoadingList()) {
            ret.addLoading(PointDTOFactory.getInstance().toModel(m, p));
        }
        return ret;
    }
}
