
package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.ReferenceDTO.Builder;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.datamodel.Reference;


/**
 *
 * @author belmann
 */
public class ReferenceDTOFactory extends DTOConversionBase<MGXReferenceI, ReferenceDTO>{

    static {
        instance = new ReferenceDTOFactory();
    }
    protected final static ReferenceDTOFactory instance;

    private ReferenceDTOFactory() {
    }

    public static ReferenceDTOFactory getInstance() {
        return instance;
    }
    
    
    @Override
    public ReferenceDTO toDTO(MGXReferenceI ref) {
        Builder b = ReferenceDTO.newBuilder();
        if (ref.getId() != Identifiable.INVALID_IDENTIFIER) {
            b.setId(ref.getId());
        }
        b = b.setName(ref.getName());
        b = b.setLength(ref.getLength());
        return b.build();
    }

    @Override
    public MGXReferenceI toModel(MGXMasterI m, ReferenceDTO dto) {
       MGXReferenceI d = new Reference(m);
        d.setName(dto.getName());
        d.setLength(dto.getLength());
        d.setId(dto.getId());
        return d;
    }
}
