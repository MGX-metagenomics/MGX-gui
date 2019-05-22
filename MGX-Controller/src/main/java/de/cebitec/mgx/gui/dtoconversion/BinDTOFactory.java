/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.dto.dto.BinDTO;
import de.cebitec.mgx.gui.datamodel.assembly.Bin;

/**
 *
 * @author sj
 */
public class BinDTOFactory extends DTOConversionBase<BinI, BinDTO> {

    protected final static BinDTOFactory instance = new BinDTOFactory();

    private BinDTOFactory() {
    }

    public static BinDTOFactory getInstance() {
        return instance;
    }

    @Override
    public BinDTO toDTO(BinI a) {
        return BinDTO.newBuilder()
                .setId(a.getId())
                .setName(a.getName())
                .setAssemblyId(a.getAssemblyId())
                .build();
    }

    @Override
    public BinI toModel(MGXMasterI m, BinDTO dto) {
        return new Bin(m, dto.getId(), dto.getName(), dto.getAssemblyId());
    }
}
