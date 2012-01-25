package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.dtoconversion.SeqRunDTOFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccess extends AccessBase<SeqRun> {

    @Override
    public Long create(SeqRun obj) {
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        Long id = null;
        try {
            id = getDTOmaster().SeqRun().create(dto);
        } catch (MGXServerException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        obj.setId(id);
        obj.setMaster(this.getMaster());
        return id;
    }

    @Override
    public SeqRun fetch(Long id) {
        SeqRunDTO dto = null;
        try {
            dto = getDTOmaster().SeqRun().fetch(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        SeqRun ret = SeqRunDTOFactory.getInstance().toModel(dto);
        ret.setMaster(this.getMaster());
        return ret;
    }

    @Override
    public List<SeqRun> fetchall() {
        List<SeqRun> all = new ArrayList<SeqRun>();
        try {
            for (SeqRunDTO dto : getDTOmaster().SeqRun().fetchall()) {
                SeqRun sr = SeqRunDTOFactory.getInstance().toModel(dto);
                sr.setMaster(this.getMaster());
                all.add(sr);
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    @Override
    public void update(SeqRun obj) {
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().SeqRun().update(dto);
        } catch (MGXServerException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            getDTOmaster().SeqRun().delete(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Iterable<SeqRun> ByExtract(DNAExtract extract) {
        List<SeqRun> all = new ArrayList<SeqRun>();
        try {
            for (SeqRunDTO dto : getDTOmaster().SeqRun().ByExtract(extract.getId())) {
                SeqRun sr = SeqRunDTOFactory.getInstance().toModel(dto);
                sr.setMaster(this.getMaster());
                all.add(sr);
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(SeqRunAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }
}
