package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.dtoconversion.ToolDTOFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends AccessBase<Tool> {

    public Collection<Tool> listGlobalTools() throws MGXServerException {
        Collection<Tool> ret = new ArrayList<>();
        for (ToolDTO dto : getDTOmaster().Tool().listGlobalTools()) {
            Tool tool = ToolDTOFactory.getInstance().toModel(dto);
            // FIXME cannot set master
            ret.add(tool);
        }
        return ret;
    }

    public long installTool(long global_id) throws MGXServerException {
        assert global_id != -1;
        return getDTOmaster().Tool().installTool(global_id);
    }

    @Override
    public Long create(Tool obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Tool fetch(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Tool> fetchall() {
        List<Tool> ret = new ArrayList<>();
        try {
            for (ToolDTO dto : getDTOmaster().Tool().fetchall()) {
                Tool tool = ToolDTOFactory.getInstance().toModel(dto);
                // FIXME cannot set master
                ret.add(tool);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public void update(Tool obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}