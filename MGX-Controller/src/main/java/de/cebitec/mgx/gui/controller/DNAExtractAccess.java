package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.DNAExtractAccessI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.gui.dtoconversion.DNAExtractDTOFactory;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class DNAExtractAccess extends AccessBase<DNAExtractI> implements DNAExtractAccessI {

    public DNAExtractAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        super(master, dtomaster);
    }
    
    @Override
    public long create(DNAExtractI obj) {
        DNAExtractDTO dto = DNAExtractDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().DNAExtract().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        return id;
    }

    @Override
    public DNAExtractI fetch(long id) {
        DNAExtractDTO dto = null;
        try {
            dto = getDTOmaster().DNAExtract().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        DNAExtractI ret = DNAExtractDTOFactory.getInstance().toModel(getMaster(), dto);
        return ret;
    }

    @Override
    public Iterator<DNAExtractI> fetchall() {
        try {

            return new Iterator<DNAExtractI>() {
                final Iterator<DNAExtractDTO> iter = getDTOmaster().DNAExtract().fetchall();

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public DNAExtractI next() {
                    DNAExtractI ret = DNAExtractDTOFactory.getInstance().toModel(getMaster(), iter.next());
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
    public void update(DNAExtractI obj) {
        DNAExtractDTO dto = DNAExtractDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().DNAExtract().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.modified();
    }

    @Override
    public TaskI delete(DNAExtractI obj) {
        TaskI ret = null;
        try {
            UUID uuid = getDTOmaster().DNAExtract().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    public Iterator<DNAExtractI> BySample(final long sample_id) {

        try {
            return new Iterator<DNAExtractI>() {
                final Iterator<DNAExtractDTO> iter = getDTOmaster().DNAExtract().BySample(sample_id);

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public DNAExtractI next() {
                    DNAExtractI ret = DNAExtractDTOFactory.getInstance().toModel(getMaster(), iter.next());
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
