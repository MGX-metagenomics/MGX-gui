package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.datamodel.misc.Task.TaskType;
import de.cebitec.mgx.gui.dtoconversion.SampleDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class SampleAccess extends AccessBase<Sample> {

    @Override
    public long create(Sample obj) {
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Sample().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        obj.setMaster(this.getMaster());
        return id;
    }

    @Override
    public Sample fetch(long id) {
        SampleDTO dto = null;
        try {
            dto = getDTOmaster().Sample().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        Sample s = SampleDTOFactory.getInstance().toModel(dto);
        s.setMaster(this.getMaster());
        return s;
    }

    @Override
    public Iterator<Sample> fetchall() {
        try {
            Iterator<SampleDTO> fetchall = getDTOmaster().Sample().fetchall();
            return new BaseIterator<SampleDTO, Sample>(fetchall) {
                @Override
                public Sample next() {
                    Sample s = SampleDTOFactory.getInstance().toModel(iter.next());
                    s.setMaster(getMaster());
                    return s;
                }
            };

        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    @Override
    public void update(Sample obj) {
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Sample().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_MODIFIED, null, obj);
    }

    @Override
    public Task delete(Sample obj) {
        Task ret = null;
        try {
            UUID uuid = getDTOmaster().Sample().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    public Iterator<Sample> ByHabitat(final long hab_id) {
        try {
            Iterator<SampleDTO> fetchall = getDTOmaster().Sample().ByHabitat(hab_id);
            return new BaseIterator<SampleDTO, Sample>(fetchall) {
                @Override
                public Sample next() {
                    Sample s = SampleDTOFactory.getInstance().toModel(iter.next());
                    s.setMaster(getMaster());
                    return s;
                }
            };

        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }
}
