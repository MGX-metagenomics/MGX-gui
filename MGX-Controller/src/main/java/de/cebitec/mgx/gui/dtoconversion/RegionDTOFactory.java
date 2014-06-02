/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.dto.dto.RegionDTO;
import de.cebitec.mgx.dto.dto.RegionDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Region;

/**
 *
 * @author belmann
 */
public class RegionDTOFactory extends DTOConversionBase<RegionI, RegionDTO> {

    static {
        instance = new RegionDTOFactory();
    }
    protected final static RegionDTOFactory instance;

    private RegionDTOFactory() {
    }

    public static RegionDTOFactory getInstance() {
        return instance;
    }

    @Override
    public RegionDTO toDTO(RegionI reg) {
        Builder b = RegionDTO.newBuilder();
        if (reg.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(reg.getId());
        }
        b = b.setName(reg.getName());
        b = b.setDescription(reg.getDescription());
        b = b.setStart(reg.getStart());
        b = b.setStop(reg.getStop());
        return b.build();
    }

    @Override
    public RegionI toModel(MGXMasterI m, RegionDTO dto) {
        RegionI d = new Region(m, dto.getStart(), dto.getStop());
        d.setName(dto.getName());
        d.setDescription(dto.getDescription());
        d.setId(dto.getId());
        return d;
    }

}
