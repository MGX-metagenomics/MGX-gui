package de.cebitec.mgx.gui.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.ToolAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.common.ToolScope;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.dtoconversion.JobParameterDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.ToolDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author sjaenick
 */
public class ToolAccess extends MasterHolder implements ToolAccessI {

    private Cache<JobI, ToolI> toolCache = null;

    public ToolAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);

        toolCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public Iterator<ToolI> listGlobalTools() throws MGXException {
        Iterator<ToolDTO> listGlobalTools;
        try {
            listGlobalTools = getDTOmaster().Tool().listGlobalTools();
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
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
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public Collection<JobParameterI> getAvailableParameters(String toolXml) throws MGXException {
        List<JobParameterI> ret = new ArrayList<>();
        try {
            for (JobParameterDTO dtoParameter : getDTOmaster().Tool().getAvailableParameters(toolXml)) {
                ret.add(JobParameterDTOFactory.getInstance().toModel(getMaster(), dtoParameter));
            }
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
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
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        return ret;
    }

    @Override
    public ToolI create(ToolScope scope, String name, String description, String author, String webSite, float version, String toolDefinition) throws MGXException {
        ToolI obj = new Tool(getMaster());
        obj.setScope(scope);
        obj.setName(name);
        obj.setDescription(description);
        obj.setAuthor(author);
        obj.setUrl(webSite);
        obj.setVersion(version);
        obj.setDefinition(toolDefinition);
        ToolDTO dto = ToolDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Tool().create(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        obj.setId(id);
        return obj;
    }

    @Override
    public ToolI fetch(long id) throws MGXException {
        ToolI t = null;
        try {
            ToolDTO dto = getDTOmaster().Tool().fetch(id);
            t = ToolDTOFactory.getInstance().toModel(getMaster(), dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        return t;
    }

    @Override
    public Iterator<ToolI> fetchall() throws MGXException {
        try {
            Iterator<ToolDTO> fetchall = getDTOmaster().Tool().fetchall().getToolList().iterator();
            return new BaseIterator<ToolDTO, ToolI>(fetchall) {
                @Override
                public ToolI next() {
                    ToolI tool = ToolDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return tool;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public TaskI<ToolI> delete(ToolI obj) throws MGXException {
        try {
            UUID uuid = getDTOmaster().Tool().delete(obj.getId());
            return getMaster().<ToolI>Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public ToolI ByJob(JobI job) throws MGXException {
        if (job.getTool() != null) {
            return job.getTool();
        }
        ToolI t = toolCache.getIfPresent(job);
        if (t != null) {
            if (job.getTool() == null) {
                job.setTool(t);
            }
            return t;
        }

        try {
            ToolDTO dto = getDTOmaster().Tool().byJob(job.getId());
            t = ToolDTOFactory.getInstance().toModel(getMaster(), dto);
            job.setTool(t);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }

        toolCache.put(job, t);
        return t;
    }

    @Override
    public String getDefinition(ToolI tool) throws MGXException {
        if (tool.getDefinition() != null && !"".equals(tool.getDefinition())) {
            return tool.getDefinition();
        }
        String toolDefinition;
        try {
            toolDefinition = getDTOmaster().Tool().getDefinition(tool.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        tool.setDefinition(toolDefinition);
        return toolDefinition;
    }

    @Override
    public void dispose() {
        toolCache.cleanUp();
    }
}
