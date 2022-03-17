/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.dto.dto.ContigDTO;
import de.cebitec.mgx.gui.datamodel.assembly.Contig;

/**
 *
 * @author sj
 */
public class ContigDTOFactory extends DTOConversionBase<ContigI, ContigDTO> {

    protected final static ContigDTOFactory instance = new ContigDTOFactory();

    private ContigDTOFactory() {
    }

    public static ContigDTOFactory getInstance() {
        return instance;
    }

    @Override
    public ContigDTO toDTO(ContigI a) {
        return ContigDTO.newBuilder()
                .setId(a.getId())
                .setName(a.getName())
                .setGc(a.getGC())
                .setLengthBp(a.getLength())
                .setBinId(a.getBinId())
                .build();
    }

    @Override
    public ContigI toModel(MGXMasterI m, ContigDTO dto) {
        return new Contig(m, dto.getId(), dto.getName(), dto.getBinId(), dto.getGc(), dto.getLengthBp(), dto.getCoverage(), dto.getNumberSubregions());
    }
}
