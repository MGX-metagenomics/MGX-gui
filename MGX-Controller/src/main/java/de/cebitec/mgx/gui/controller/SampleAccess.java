package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.dtoconversion.SampleDTOFactory;
import java.util.ArrayList;
import java.util.List;
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
    public List<Sample> fetchall() {
        List<Sample> all = new ArrayList<>();
        try {
            for (SampleDTO dto : getDTOmaster().Sample().fetchall()) {
                Sample s = SampleDTOFactory.getInstance().toModel(dto);
                s.setMaster(this.getMaster());
                all.add(s);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
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
    public void delete(Sample obj) {
        try {
            getDTOmaster().Sample().delete(obj.getId());
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_DELETED, obj, null);
    }

    public Iterable<Sample> ByHabitat(long hab_id) {
        List<Sample> all = new ArrayList<>();
        try {
            for (SampleDTO dto : getDTOmaster().Sample().ByHabitat(hab_id)) {
                Sample s = SampleDTOFactory.getInstance().toModel(dto);
                s.setMaster(this.getMaster());
                all.add(s);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }
}
