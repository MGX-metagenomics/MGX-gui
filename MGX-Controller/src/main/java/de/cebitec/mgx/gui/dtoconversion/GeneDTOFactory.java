/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.dto.dto.GeneDTO;
import de.cebitec.mgx.gui.datamodel.assembly.Gene;

/**
 *
 * @author sj
 */
public class GeneDTOFactory extends DTOConversionBase<GeneI, GeneDTO> {

    protected final static GeneDTOFactory instance = new GeneDTOFactory();

    private GeneDTOFactory() {
    }

    public static GeneDTOFactory getInstance() {
        return instance;
    }

    @Override
    public GeneDTO toDTO(GeneI a) {
        return GeneDTO.newBuilder()
                .setId(a.getId())
                .setContigId(a.getContigId())
                .setStart(a.getStart())
                .setStop(a.getStop())
                .setCoverage(a.getCoverage())
                .build();
    }

    @Override
    public GeneI toModel(MGXMasterI m, GeneDTO dto) {
        return new Gene(m, dto.getId(), dto.getContigId(), dto.getStart(), dto.getStop(), dto.getCoverage());
    }
}
