package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.ToolAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.gui.dtoconversion.JobParameterDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.ToolDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends AccessBase<ToolI> implements ToolAccessI {

    public ToolAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        super(master, dtomaster);
    }

    @Override
    public Iterator<ToolI> listGlobalTools() throws MGXException {
        Iterator<ToolDTO> listGlobalTools;
        try {
            listGlobalTools = getDTOmaster().Tool().listGlobalTools();
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<ToolDTO, ToolI>(listGlobalTools) {
            @Override
            public ToolI next() {
                return ToolDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }

    @Override
    public long installTool(long global_id) throws MGXException {
        assert global_id != Identifiable.INVALID_IDENTIFIER;
        try {
            return getDTOmaster().Tool().installGlobalTool(global_id);
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Collection<JobParameterI> getAvailableParameters(ToolI tool) throws MGXException {

        ToolDTO dto = ToolDTOFactory.getInstance().toDTO(tool);

        List<JobParameterI> ret = new ArrayList<>();
        try {
            for (JobParameterDTO dtoParameter : getDTOmaster().Tool().getAvailableParameters(dto)) {
                ret.add(JobParameterDTOFactory.getInstance().toModel(getMaster(), dtoParameter));
            }
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
        return ret;

    }

    @Override
    public Collection<JobParameterI> getAvailableParameters(long toolId, boolean isGlobal) throws MGXException {
        List<JobParameterI> ret = new ArrayList<>();
        try {
            for (JobParameterDTO dto : getDTOmaster().Tool().getAvailableParameters(toolId, isGlobal)) {
                ret.add(JobParameterDTOFactory.getInstance().toModel(getMaster(), dto));
            }

        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public ToolI create(ToolI obj) {
        assert obj.getId() == Identifiable.INVALID_IDENTIFIER;
        ToolDTO dto = ToolDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Tool().create(dto);
        } catch (MGXClientException | MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        return obj;
    }

    @Override
    public ToolI fetch(long id) {
        ToolI t = null;
        try {
            ToolDTO dto = getDTOmaster().Tool().fetch(id);
            t = ToolDTOFactory.getInstance().toModel(getMaster(), dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return t;
    }

    @Override
    public Iterator<ToolI> fetchall() {
        try {
            Iterator<ToolDTO> fetchall = getDTOmaster().Tool().fetchall();
            return new BaseIterator<ToolDTO, ToolI>(fetchall) {
                @Override
                public ToolI next() {
                    ToolI tool = ToolDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return tool;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    @Override
    public void update(ToolI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI delete(ToolI obj) {
        TaskI ret = null;
        try {
            UUID uuid = getDTOmaster().Tool().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public ToolI ByJob(long id) {
        ToolI t = null;
        try {
            ToolDTO dto = getDTOmaster().Tool().ByJob(id);
            t = ToolDTOFactory.getInstance().toModel(getMaster(), dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return t;
    }
}
