package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.gui.datamodel.Tool;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends AccessBase<Tool> {

    public Collection<Tool> listGlobalTools() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Long installTool(Long global_id) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
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