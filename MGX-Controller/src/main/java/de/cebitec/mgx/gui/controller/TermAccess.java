package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.TermDTO;
import de.cebitec.mgx.gui.datamodel.Term;
import de.cebitec.mgx.gui.dtoconversion.TermDTOFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class TermAccess extends AccessBase<Term> {
    
    public static final String SEQ_METHODS = de.cebitec.mgx.client.access.rest.TermAccess.SEQ_METHODS;
    public static final String SEQ_PLATFORMS = de.cebitec.mgx.client.access.rest.TermAccess.SEQ_PLATFORMS;

    public Collection<Term> byCategory(String cat) {
        List<Term> ret = new ArrayList<>();
        try {
            for (TermDTO dto : getDTOmaster().Term().byCategory(cat)) {
                ret.add(TermDTOFactory.getInstance().toModel(dto));
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public long create(Term obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Term fetch(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Term> fetchall() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Term obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
