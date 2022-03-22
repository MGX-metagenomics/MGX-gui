/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.AssembledRegionI;
import de.cebitec.mgx.common.RegionType;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.AssembledRegionDTO;
import de.cebitec.mgx.gui.datamodel.assembly.AssembledRegion;

/**
 *
 * @author sj
 */
public class AssembledRegionDTOFactory extends DTOConversionBase<AssembledRegionI, AssembledRegionDTO> {

    protected final static AssembledRegionDTOFactory instance = new AssembledRegionDTOFactory();

    private AssembledRegionDTOFactory() {
    }

    public static AssembledRegionDTOFactory getInstance() {
        return instance;
    }

    @Override
    public AssembledRegionDTO toDTO(AssembledRegionI a) {
        return AssembledRegionDTO.newBuilder()
                .setId(a.getId())
                .setContigId(a.getParentId())
                .setStart(a.getStart())
                .setStop(a.getStop())
                .setCoverage(a.getCoverage())
                .setType(dto.RegionType.forNumber(a.getType().getValue()))
                .build();
    }

    @Override
    public AssembledRegionI toModel(MGXMasterI m, AssembledRegionDTO dto) {
        AssembledRegionI ret = new AssembledRegion(m, dto.getId(), dto.getContigId(), dto.getStart(), dto.getStop(), 
                RegionType.values()[dto.getType().ordinal()], dto.getCoverage());
        return ret;
    }
}
