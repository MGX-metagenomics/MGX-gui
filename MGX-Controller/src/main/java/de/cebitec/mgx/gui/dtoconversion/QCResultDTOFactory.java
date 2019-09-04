/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.qc.DataRowI;
import de.cebitec.mgx.api.model.qc.QCResultI;
import de.cebitec.mgx.dto.dto.DataRowDTO;
import de.cebitec.mgx.dto.dto.QCResultDTO;
import de.cebitec.mgx.gui.datamodel.qc.DataRow;
import de.cebitec.mgx.gui.datamodel.qc.QCResult;
import java.util.List;

/**
 *
 * @author sj
 */
public class QCResultDTOFactory extends DTOConversionBase<QCResultI, QCResultDTO> {

    static {
        instance = new QCResultDTOFactory();
    }
    protected static QCResultDTOFactory instance;

    private QCResultDTOFactory() {
    }

    public static QCResultDTOFactory getInstance() {
        return instance;
    }

    @Override
    public QCResultDTO toDTO(QCResultI a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public QCResultI toModel(MGXMasterI m, QCResultDTO dto) {
        DataRowI[] drs = new DataRowI[dto.getRowCount()];
        int pos=0;
        for (DataRowDTO drDTO : dto.getRowList()) {
            List<Float> valueList = drDTO.getValueList();
            float[] f = new float[valueList.size()];
            for (int i = 0; i < valueList.size(); i++) {
                f[i] = valueList.get(i);
            }
            DataRowI dr = new DataRow(drDTO.getName(), f);
            drs[pos++] = dr;
        }
        return new QCResult(dto.getName(), !dto.getDescription().isEmpty() ? dto.getDescription() : dto.getName(), drs);
    }

}
