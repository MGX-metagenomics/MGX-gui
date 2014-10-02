package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.ReferenceAccessI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.ReferenceUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.RegionDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.ReferenceDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.RegionDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.io.File;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author belmann
 */
public class ReferenceAccess implements ReferenceAccessI {

    private final MGXDTOMaster dtomaster;
    private final MGXMasterI master;

    public ReferenceAccess(MGXDTOMaster dtomaster, MGXMasterI master) {
        this.dtomaster = dtomaster;
        this.master = master;
    }

    @Override
    public MGXReferenceI create(MGXReferenceI obj) {
        ReferenceDTO dto = ReferenceDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = dtomaster.Reference().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        return obj;
    }

    @Override
    public MGXReferenceI fetch(long id) {
        ReferenceDTO dto = null;
        try {
            dto = dtomaster.Reference().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        MGXReferenceI ret = ReferenceDTOFactory.getInstance().toModel(master, dto);
        return ret;
    }

    @Override
    public Iterator<MGXReferenceI> fetchall() throws MGXException {
        try {

            return new Iterator<MGXReferenceI>() {
                final Iterator<ReferenceDTO> iter = dtomaster.Reference().fetchall();

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public MGXReferenceI next() {
                    MGXReferenceI ret = ReferenceDTOFactory.getInstance().toModel(master, iter.next());
                    return ret;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported.");
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void update(MGXReferenceI obj) {
        ReferenceDTO dto = ReferenceDTOFactory.getInstance().toDTO(obj);
        try {
            dtomaster.Reference().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.modified();
    }

    @Override
    public Iterator<RegionI> byReferenceInterval(MGXReferenceI ref, int from, int to) {
        Iterator<RegionDTO> fetchall;
        try {
            fetchall = dtomaster.Reference().byReferenceInterval(ref.getId(), from, to);
            return new BaseIterator<RegionDTO, RegionI>(fetchall) {
                @Override
                public RegionI next() {
                    RegionI s = RegionDTOFactory.getInstance().toModel(master, iter.next());
                    return s;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public String getSequence(final MGXReferenceI ref, int from, int to) {
        try {
            return dtomaster.Reference().getSequence(ref.getId(), from, to);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public Iterator<MGXReferenceI> listGlobalReferences() {
        Iterator<ReferenceDTO> iter;
        try {
            iter = dtomaster.Reference().listGlobalReferences();
            return new BaseIterator<ReferenceDTO, MGXReferenceI>(iter) {
                @Override
                public MGXReferenceI next() {
                    MGXReferenceI reference = ReferenceDTOFactory.getInstance().toModel(master, iter.next());
                    return reference;
                }
            };
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public long installGlobalReference(long id) {
        assert id != Identifiable.INVALID_IDENTIFIER;
        try {
            return dtomaster.Reference().installGlobalReference(id);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Identifiable.INVALID_IDENTIFIER;
    }

    @Override
    public TaskI delete(MGXReferenceI obj) throws MGXException {
        try {
            UUID uuid = dtomaster.Reference().delete(obj.getId());
            return master.Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public UploadBaseI createUploader(File localFile) {
        final ReferenceUploader ru = dtomaster.Reference().createUploader(localFile);
        return new UploadBaseI() {

            @Override
            public boolean upload() {
                return ru.upload();
            }

            @Override
            public long getNumElementsSent() {
                return ru.getProgress();
            }
        };
    }
}
