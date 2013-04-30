package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.dtoconversion.DNAExtractDTOFactory;
import java.util.Iterator;
import java.util.UUID;
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
    public Iterator<DNAExtract> fetchall() {
        try {

            return new Iterator<DNAExtract>() {
                final Iterator<DNAExtractDTO> iter = getDTOmaster().DNAExtract().fetchall();

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public DNAExtract next() {
                    DNAExtract ret = DNAExtractDTOFactory.getInstance().toModel(iter.next());
                    ret.setMaster(getMaster());
                    return ret;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported.");
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
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
    public UUID delete(DNAExtract obj) {
        UUID ret = null;
        try {
            ret = getDTOmaster().DNAExtract().delete(obj.getId());
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_DELETED, obj, null);
        return ret;
    }

    public Iterator<DNAExtract> BySample(final long sample_id) {

        try {
            return new Iterator<DNAExtract>() {
                final Iterator<DNAExtractDTO> iter = getDTOmaster().DNAExtract().BySample(sample_id);

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public DNAExtract next() {
                    DNAExtract ret = DNAExtractDTOFactory.getInstance().toModel(iter.next());
                    ret.setMaster(getMaster());
                    return ret;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported.");
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
