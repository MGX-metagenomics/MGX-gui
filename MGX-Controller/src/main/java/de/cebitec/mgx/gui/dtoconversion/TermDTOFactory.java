package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.dto.dto.TermDTO;
import de.cebitec.mgx.dto.dto.TermDTO.Builder;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.TermI;
import de.cebitec.mgx.gui.datamodel.Term;

/**
 *
 * @author sjaenick
 */
public class TermDTOFactory extends DTOConversionBase<TermI, TermDTO> {

    static {
        instance = new TermDTOFactory();
    }
    protected static TermDTOFactory instance;

    private TermDTOFactory() {
    }

    public static TermDTOFactory getInstance() {
        return instance;
    }

    @Override
    public TermDTO toDTO(TermI a) {
        Builder b = TermDTO.newBuilder()
                .setId(a.getId())
                .setName(a.getName());
        if (a.getDescription() != null) {
            b = b.setDescription(a.getDescription());
        }
        if (a.getParentId() != Identifiable.INVALID_IDENTIFIER) {
            b = b.setParentId(a.getParentId());
        }
        return b.build();
    }

    @Override
    public TermI toModel(MGXMasterI m, TermDTO dto) {
        TermI t = new Term();
        t.setId(dto.getId());
        t.setName(dto.getName());
        if (dto.getParentId() != 0) {
            t.setParentId(dto.getParentId());
        }
        if (!dto.getDescription().isEmpty()) {
            t.setDescription(dto.getDescription());
        }
        return t;
    }
}
