package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.DNAExtractAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.dtoconversion.DNAExtractDTOFactory;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class DNAExtractAccess extends AccessBase<DNAExtractI> implements DNAExtractAccessI {

    public DNAExtractAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public DNAExtractI create(SampleI sample, String name, String method, String protocol, String primer5, String primer3, String targetGene, String targetFragment, String description) throws MGXException {
        DNAExtract obj = new DNAExtract(getMaster())
                .setSampleId(sample.getId())
                .setName(name)
                .setMethod(method)
                .setProtocol(protocol)
                .setFivePrimer(primer5)
                .setThreePrimer(primer3)
                .setTargetGene(targetGene)
                .setTargetFragment(targetFragment)
                .setDescription(description);
        DNAExtractDTO dto = DNAExtractDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().DNAExtract().create(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        obj.setId(id);
        return obj;
    }

//    @Override
//    public DNAExtractI create(DNAExtractI obj) throws MGXException {
//        DNAExtractDTO dto = DNAExtractDTOFactory.getInstance().toDTO(obj);
//        long id = Identifiable.INVALID_IDENTIFIER;
//        try {
//            id = getDTOmaster().DNAExtract().create(dto);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        obj.setId(id);
//        return obj;
//    }
    @Override
    public DNAExtractI fetch(long id) throws MGXException {
        DNAExtractDTO dto = null;
        try {
            dto = getDTOmaster().DNAExtract().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        DNAExtractI ret = DNAExtractDTOFactory.getInstance().toModel(getMaster(), dto);
        return ret;
    }

    @Override
    public Iterator<DNAExtractI> fetchall() throws MGXException {
        try {

            return new Iterator<DNAExtractI>() {
                final Iterator<DNAExtractDTO> iter = getDTOmaster().DNAExtract().fetchall().getExtractList().iterator();

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
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void update(DNAExtractI obj) throws MGXException {
        DNAExtractDTO dto = DNAExtractDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().DNAExtract().update(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        obj.modified();
    }

    @Override
    public TaskI<DNAExtractI> delete(DNAExtractI obj) throws MGXException {
        TaskI<DNAExtractI> ret = null;
        try {
            UUID uuid = getDTOmaster().DNAExtract().delete(obj.getId());
            ret = getMaster().<DNAExtractI>Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return ret;
    }

    @Override
    public Iterator<DNAExtractI> BySample(final SampleI sample) throws MGXException {

        try {
            return new Iterator<DNAExtractI>() {
                final Iterator<DNAExtractDTO> iter = getDTOmaster().DNAExtract().bySample(sample.getId());

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
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

}
