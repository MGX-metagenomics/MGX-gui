package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.dtoconversion.DNAExtractDTOFactory;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class DNAExtractAccess extends AccessBase<DNAExtract> {

    @Override
    public long create(DNAExtract obj) {
        DNAExtractDTO dto = DNAExtractDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().DNAExtract().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        obj.setMaster(this.getMaster());
        return id;
    }

    @Override
    public DNAExtract fetch(long id) {
        DNAExtractDTO dto = null;
        try {
            dto = getDTOmaster().DNAExtract().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
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
            Exceptions.printStackTrace(ex);
        }
        return all;
    }

    @Override
    public void update(DNAExtract obj) {
        DNAExtractDTO dto = DNAExtractDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().DNAExtract().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_MODIFIED, null, obj);
    }

    @Override
    public boolean delete(DNAExtract obj) {
        boolean ret;
        try {
            ret = getDTOmaster().DNAExtract().delete(obj.getId());
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
        obj.firePropertyChange(ModelBase.OBJECT_DELETED, obj, null);
        return ret;
    }

    public Iterable<DNAExtract> BySample(long sample_id) {
        List<DNAExtract> all = new ArrayList<>();
        try {
            for (DNAExtractDTO dto : getDTOmaster().DNAExtract().BySample(sample_id)) {
                DNAExtract extract = DNAExtractDTOFactory.getInstance().toModel(dto);
                extract.setMaster(this.getMaster());
                all.add(extract);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }
}
