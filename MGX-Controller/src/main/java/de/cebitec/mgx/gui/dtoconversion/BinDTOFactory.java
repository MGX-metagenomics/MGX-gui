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
                .setCompleteness(a.getCompleteness())
                .setContamination(a.getContamination())
                .setN50(a.getN50())
                .setTaxonomy(a.getTaxonomy())
                .setPredictedCds(a.getPredictedCDS())
                .setAssemblyId(a.getAssemblyId())
                .build();
    }

    @Override
    public BinI toModel(MGXMasterI m, BinDTO dto) {
        return new Bin(m, dto.getId(), dto.getName(), dto.getCompleteness(), dto.getContamination(),
                dto.getN50(), dto.getTotalBp(), dto.getTaxonomy(), dto.getPredictedCds(), 
                dto.getNumContigs(), dto.getAssemblyId());
    }
}
