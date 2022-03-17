/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.GeneCoverageI;
import de.cebitec.mgx.dto.dto.GeneCoverageDTO;
import de.cebitec.mgx.gui.datamodel.assembly.GeneCoverage;

/**
 *
 * @author sj
 */
public class GeneCoverageDTOFactory extends DTOConversionBase<GeneCoverageI, GeneCoverageDTO> {

    protected final static GeneCoverageDTOFactory instance = new GeneCoverageDTOFactory();

    private GeneCoverageDTOFactory() {
    }

    public static GeneCoverageDTOFactory getInstance() {
        return instance;
    }

    @Override
    public GeneCoverageDTO toDTO(GeneCoverageI a) {
        return GeneCoverageDTO.newBuilder()
                .setRegionId(a.getRegionId())
                .setRunId(a.getRunId())
                .setCoverage(a.getCoverage())
                .build();
    }

    @Override
    public GeneCoverageI toModel(MGXMasterI m, GeneCoverageDTO dto) {
        return new GeneCoverage(dto.getRegionId(), dto.getRunId(), dto.getCoverage());
    }
}
