package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.dtoconversion.JobParameterDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.ToolDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends AccessBase<Tool> {

    public Iterator<Tool> listGlobalTools() throws MGXServerException {
        Iterator<ToolDTO> listGlobalTools = getDTOmaster().Tool().listGlobalTools();
        return new BaseIterator<ToolDTO, Tool>(listGlobalTools) {
            @Override
            public Tool next() {
                Tool tool = ToolDTOFactory.getInstance().toModel(iter.next());
                // FIXME cannot set master
                return tool;
            }
        };
    }

    public long installTool(long global_id) throws MGXServerException {
        assert global_id != Identifiable.INVALID_IDENTIFIER;
        return getDTOmaster().Tool().installGlobalTool(global_id);
    }

    public Collection<JobParameter> getAvailableParameters(Tool tool) {

        ToolDTO dto = ToolDTOFactory.getInstance().toDTO(tool);

        List<JobParameter> ret = new ArrayList<>();
        try {
            for (JobParameterDTO dtoParameter : getDTOmaster().Tool().getAvailableParameters(dto)) {
                ret.add(JobParameterDTOFactory.getInstance().toModel(dtoParameter));
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;

    }

    public Collection<JobParameter> getAvailableParameters(long toolId, boolean isGlobal) {
        List<JobParameter> ret = new ArrayList<>();
        try {
            for (JobParameterDTO dto : getDTOmaster().Tool().getAvailableParameters(toolId, isGlobal)) {
                ret.add(JobParameterDTOFactory.getInstance().toModel(dto));
            }

        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public long create(Tool obj) {
        assert obj.getId() == Identifiable.INVALID_IDENTIFIER;
        ToolDTO dto = ToolDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Tool().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        obj.setMaster(this.getMaster());
        return id;
    }

    @Override
    public Tool fetch(long id) {
        Tool t = null;
        try {
            ToolDTO dto = getDTOmaster().Tool().fetch(id);
            t = ToolDTOFactory.getInstance().toModel(dto);
            t.setMaster(getMaster());
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return t;
    }

    @Override
    public Iterator<Tool> fetchall() {
        try {
            Iterator<ToolDTO> fetchall = getDTOmaster().Tool().fetchall();
            return new BaseIterator<ToolDTO, Tool>(fetchall) {
                @Override
                public Tool next() {
                    Tool tool = ToolDTOFactory.getInstance().toModel(iter.next());
                    tool.setMaster(getMaster());
                    return tool;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    @Override
    public void update(Tool obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean delete(Tool obj) {
        boolean ret;
        try {
            ret = getDTOmaster().Tool().delete(obj.getId());
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
        obj.firePropertyChange(ModelBase.OBJECT_DELETED, obj, null);
        return ret;
    }

    public Tool ByJob(long id) {
        Tool t = null;
        try {
            ToolDTO dto = getDTOmaster().Tool().ByJob(id);
            t = ToolDTOFactory.getInstance().toModel(dto);
            t.setMaster(getMaster());
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return t;
    }
}
