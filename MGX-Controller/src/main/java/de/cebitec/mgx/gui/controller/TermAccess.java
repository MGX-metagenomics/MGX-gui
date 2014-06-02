package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.TermAccessI;
import de.cebitec.mgx.api.model.TermI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.TermDTO;
import de.cebitec.mgx.gui.dtoconversion.TermDTOFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class TermAccess implements TermAccessI {

    public static final String SEQ_METHODS = de.cebitec.mgx.client.access.rest.TermAccess.SEQ_METHODS;
    public static final String SEQ_PLATFORMS = de.cebitec.mgx.client.access.rest.TermAccess.SEQ_PLATFORMS;
    private final MGXMasterI master;
    private final MGXDTOMaster dtomaster;

    public TermAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        this.master = master;
        this.dtomaster = dtomaster;
    }
    
    @Override
    public Collection<TermI> byCategory(String cat) {
        List<TermI> ret = new ArrayList<>();
        try {
            for (TermDTO dto : dtomaster.Term().byCategory(cat)) {
                ret.add(TermDTOFactory.getInstance().toModel(master, dto));
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }
  
}
