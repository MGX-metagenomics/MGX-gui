/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.common.RegionType;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.ReferenceRegionDTO;
import de.cebitec.mgx.gui.datamodel.ReferenceRegion;

/**
 *
 * @author belmann
 */
public class ReferenceRegionDTOFactory extends DTOConversionBase<ReferenceRegionI, ReferenceRegionDTO> {

    static {
        instance = new ReferenceRegionDTOFactory();
    }
    private final static ReferenceRegionDTOFactory instance;

    private ReferenceRegionDTOFactory() {
    }

    public static ReferenceRegionDTOFactory getInstance() {
        return instance;
    }

    @Override
    public ReferenceRegionDTO toDTO(ReferenceRegionI reg) {
        ReferenceRegionDTO.Builder b = ReferenceRegionDTO.newBuilder();
        if (reg.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(reg.getId());
        }
        b = b.setName(reg.getName());
        b = b.setDescription(reg.getDescription());
        b = b.setStart(reg.getStart());
        b = b.setStop(reg.getStop());
        b = b.setType(dto.RegionType.forNumber(reg.getType().getValue()));
        b = b.setParentId(reg.getParentId());
        return b.build();
    }

    @Override
    public ReferenceRegionI toModel(MGXMasterI m, ReferenceRegionDTO dto) {
        ReferenceRegionI d = new ReferenceRegion(m, dto.getId(), dto.getParentId(), dto.getStart(), dto.getStop(), 
                RegionType.values()[dto.getType().ordinal()], dto.getDescription());
        d.setName(dto.getName());
        return d;
    }

}
