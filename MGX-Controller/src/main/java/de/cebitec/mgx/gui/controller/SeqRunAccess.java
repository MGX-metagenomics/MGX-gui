package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.JobAndAttributeTypes;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.JobDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SeqRunDTOFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccess extends AccessBase<SeqRun> {

    @Override
    public long create(SeqRun obj) {
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        long id = ModelBase.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().SeqRun().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        obj.setMaster(this.getMaster());
        return id;
    }

    @Override
    public SeqRun fetch(long id) {
        SeqRunDTO dto = null;
        try {
            dto = getDTOmaster().SeqRun().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        SeqRun ret = SeqRunDTOFactory.getInstance().toModel(dto);
        ret.setMaster(this.getMaster());
        return ret;
    }

    @Override
    public List<SeqRun> fetchall() {
        List<SeqRun> all = new ArrayList<>();
        try {
            for (SeqRunDTO dto : getDTOmaster().SeqRun().fetchall()) {
                SeqRun sr = SeqRunDTOFactory.getInstance().toModel(dto);
                sr.setMaster(this.getMaster());
                all.add(sr);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }

    @Override
    public void update(SeqRun obj) {
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().SeqRun().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void delete(long id) {
        try {
            getDTOmaster().SeqRun().delete(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public Iterable<SeqRun> ByExtract(long extract_id) {
        List<SeqRun> all = new ArrayList<>();
        try {
            for (SeqRunDTO dto : getDTOmaster().SeqRun().ByExtract(extract_id)) {
                SeqRun sr = SeqRunDTOFactory.getInstance().toModel(dto);
                sr.setMaster(this.getMaster());
                all.add(sr);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }

    public Map<Job, List<AttributeType>> getJobsAndAttributeTypes(long run_id) {
        Map<Job, List<AttributeType>> ret = new HashMap<>();
        try {
            for (JobAndAttributeTypes jat : getDTOmaster().SeqRun().getJobsAndAttributeTypes(run_id)) {
                Job job = JobDTOFactory.getInstance().toModel(jat.getJob());
                job.setMaster(this.getMaster());

                List<AttributeType> all = new ArrayList<>();
                for (AttributeTypeDTO atDTO : jat.getAttributeTypes().getAttributeTypeList()) {
                    AttributeType aType = AttributeTypeDTOFactory.getInstance().toModel(atDTO);
                    aType.setMaster(this.getMaster());
                    all.add(aType);
                }

                ret.put(job, all);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return ret;
    }
}
