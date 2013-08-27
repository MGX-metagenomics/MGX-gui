/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.dto.dto.ReferenceDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.Reference;
import static de.cebitec.mgx.gui.dtoconversion.DNAExtractDTOFactory.instance;


/**
 *
 * @author belmann
 */
public class ReferenceDTOFactory extends DTOConversionBase<Reference, dto.ReferenceDTO>{

    static {
        instance = new ReferenceDTOFactory();
    }
    protected static ReferenceDTOFactory instance;

    private ReferenceDTOFactory() {
    }

    public static ReferenceDTOFactory getInstance() {
        Builder b = ReferenceDTO.newBuilder();
        return instance;
    }
    
    
    @Override
    public dto.ReferenceDTO toDTO(Reference ref) {
       
        
        Builder b = ReferenceDTO.newBuilder();
        if (ref.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(ref.getId());
        }
        b = b.setName(ref.getName());
        b = b.setLength(ref.getLength());

        
        return b.build();
        
        
    }

    @Override
    public Reference toModel(dto.ReferenceDTO dto) {
       Reference d = new Reference();
        d.setName(dto.getName());
        d.setLength(dto.getLength());
        d.setId(dto.getId());
        return d;
    }
}
