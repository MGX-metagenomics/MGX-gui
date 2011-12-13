package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.gui.datamodel.Habitat;
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
    public Long create(Sample obj) {
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        Long id = null;
        try {
            id = getDTOmaster().Sample().create(dto);
        } catch (MGXServerException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    @Override
    public Sample fetch(Long id) {
        SampleDTO dto = null;
        try {
            dto = getDTOmaster().Sample().fetch(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return SampleDTOFactory.getInstance().toModel(dto);
    }

    @Override
    public List<Sample> fetchall() {
        List<Sample> all = new ArrayList<Sample>();
        try {
            for (SampleDTO dto : getDTOmaster().Sample().fetchall()) {
                all.add(SampleDTOFactory.getInstance().toModel(dto));
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    @Override
    public void update(Sample obj) {
        SampleDTO dto = SampleDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Sample().update(dto);
        } catch (MGXServerException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            getDTOmaster().Sample().delete(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Iterable<Sample> ByHabitat(Habitat h) {
        List<Sample> all = new ArrayList<Sample>();
        try {
            for (SampleDTO dto : getDTOmaster().Sample().ByHabitat(h.getId())) {
                Sample s = SampleDTOFactory.getInstance().toModel(dto);
                s.setHabitat(h);
                all.add(s);
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SampleAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }
}
