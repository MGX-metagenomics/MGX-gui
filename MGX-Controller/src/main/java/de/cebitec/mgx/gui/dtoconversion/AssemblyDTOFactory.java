/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.dto.dto.AssemblyDTO;
import de.cebitec.mgx.gui.datamodel.assembly.Assembly;

/**
 *
 * @author sj
 */
public class AssemblyDTOFactory extends DTOConversionBase<AssemblyI, AssemblyDTO> {

    protected final static AssemblyDTOFactory instance = new AssemblyDTOFactory();

    private AssemblyDTOFactory() {
    }

    public static AssemblyDTOFactory getInstance() {
        return instance;
    }

    @Override
    public AssemblyDTO toDTO(AssemblyI a) {
        return AssemblyDTO.newBuilder()
                .setId(a.getId())
                .setName(a.getName())
                .setReadsAssembled(a.getReadsAssembled())
                .setN50(a.getN50())
                .setJobId(a.getAssemblyJobId())
                .build();
    }

    @Override
    public AssemblyI toModel(MGXMasterI m, AssemblyDTO dto) {
        return new Assembly(m, dto.getId(), dto.getName(), dto.getReadsAssembled(), dto.getN50(), dto.getTotalCds(), dto.getJobId());
    }
}
