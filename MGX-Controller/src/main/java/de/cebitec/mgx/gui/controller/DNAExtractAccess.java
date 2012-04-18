package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.dtoconversion.DNAExtractDTOFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class DNAExtractAccess extends AccessBase<DNAExtract> {

    @Override
    public Long create(DNAExtract obj) {
        DNAExtractDTO dto = DNAExtractDTOFactory.getInstance().toDTO(obj);
        Long id = null;
        try {
            id = getDTOmaster().DNAExtract().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(DNAExtractAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        obj.setId(id);
        obj.setMaster(this.getMaster());
        return id;
    }

    @Override
    public DNAExtract fetch(Long id) {
        DNAExtractDTO dto = null;
        try {
            dto = getDTOmaster().DNAExtract().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(DNAExtractAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        DNAExtract ret = DNAExtractDTOFactory.getInstance().toModel(dto);
        ret.setMaster(this.getMaster());
        return ret;
    }

    @Override
    public List<DNAExtract> fetchall() {
        List<DNAExtract> all = new ArrayList<>();
        try {
            for (DNAExtractDTO dto : getDTOmaster().DNAExtract().fetchall()) {
                DNAExtract ret = DNAExtractDTOFactory.getInstance().toModel(dto);
                ret.setMaster(this.getMaster());
                all.add(ret);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(DNAExtractAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    @Override
    public void update(DNAExtract obj) {
        DNAExtractDTO dto = DNAExtractDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().DNAExtract().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(DNAExtractAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            getDTOmaster().DNAExtract().delete(id);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(DNAExtractAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Iterable<DNAExtract> BySample(Sample s) {
        List<DNAExtract> all = new ArrayList<>();
        try {
            for (DNAExtractDTO dto : getDTOmaster().DNAExtract().BySample(s.getId())) {
                DNAExtract extract = DNAExtractDTOFactory.getInstance().toModel(dto);
                extract.setMaster(this.getMaster());
                all.add(extract);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(DNAExtractAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }
}
