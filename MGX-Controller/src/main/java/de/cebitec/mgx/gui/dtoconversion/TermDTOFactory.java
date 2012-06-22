package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.TermDTO;
import de.cebitec.mgx.gui.datamodel.Term;

/**
 *
 * @author sjaenick
 */
public class TermDTOFactory extends DTOConversionBase<Term, TermDTO> {

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
    public TermDTO toDTO(Term a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Term toModel(TermDTO dto) {
        Term t = new Term();
        t.setId(dto.getId());
        t.setName(dto.getName());
        if (dto.hasParentId()) {
            t.setParentId(dto.getParentId());
        }
        if (dto.hasDescription()) {
            t.setDescription(dto.getDescription());
        }
        return t;
    }
}
