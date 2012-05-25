package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.dtoconversion.SampleDTOFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class SampleAccess extends AccessBase<Sample> {

    @Override
    public long create(Sample obj) {
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        long id = ModelBase.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Sample().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (MGXServerException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    @Override
    public void update(Sample obj) {
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Sample().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(long id) {
        try {
            getDTOmaster().Sample().delete(id);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Iterable<Sample> ByHabitat(Habitat h) {
        List<Sample> all = new ArrayList<>();
        try {
            for (SampleDTO dto : getDTOmaster().Sample().ByHabitat(h.getId())) {
                Sample s = SampleDTOFactory.getInstance().toModel(dto);
                s.setMaster(this.getMaster());
                all.add(s);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }
}
