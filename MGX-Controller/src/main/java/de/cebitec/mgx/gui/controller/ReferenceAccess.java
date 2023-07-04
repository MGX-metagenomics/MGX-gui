package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.ReferenceAccessI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.ReferenceUploader;
import de.cebitec.mgx.client.datatransfer.TransferBase;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.ReferenceDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author belmann
 */
public class ReferenceAccess extends MasterHolder implements ReferenceAccessI {

    public ReferenceAccess(MGXDTOMaster dtomaster, MGXMasterI master) throws MGXException {
        super(master, dtomaster);
    }

//    @Override
//    public MGXReferenceI create(MGXReferenceI obj) throws MGXException {
//        ReferenceDTO dto = ReferenceDTOFactory.getInstance().toDTO(obj);
//        long id = Identifiable.INVALID_IDENTIFIER;
//        try {
//            id = dtomaster.Reference().create(dto);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//        obj.setId(id);
//        return obj;
//    }
    @Override
    public MGXReferenceI fetch(long id) throws MGXException {
        ReferenceDTO dto = null;
        try {
            dto = getDTOmaster().Reference().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        MGXReferenceI ret = ReferenceDTOFactory.getInstance().toModel(getMaster(), dto);
        return ret;
    }

    @Override
    public Iterator<MGXReferenceI> fetchall() throws MGXException {
        try {

            return new Iterator<MGXReferenceI>() {
                final Iterator<ReferenceDTO> iter = getDTOmaster().Reference().fetchall().getReferenceList().iterator();

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public MGXReferenceI next() {
                    MGXReferenceI ret = ReferenceDTOFactory.getInstance().toModel(getMaster(), iter.next());
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
    public void update(MGXReferenceI obj) throws MGXException {
        ReferenceDTO dto = ReferenceDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Reference().update(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        obj.modified();
    }

//    @Override
//    public Iterator<ReferenceRegionI> byReferenceInterval(MGXReferenceI ref, int from, int to) throws MGXException {
//        Iterator<ReferenceRegionDTO> fetchall;
//        try {
//            fetchall = getDTOmaster().ReferenceRegion().byReferenceInterval(ref.getId(), from, to);
//            return new BaseIterator<ReferenceRegionDTO, ReferenceRegionI>(fetchall) {
//                @Override
//                public ReferenceRegionI next() {
//                    ReferenceRegionI s = ReferenceRegionDTOFactory.getInstance().toModel(getMaster(), iter.next());
//                    return s;
//                }
//            };      } catch (MGXClientLoggedOutException mcle) {
//            throw new MGXLoggedoutException(mcle);
//        } catch (MGXDTOException ex) {
//            throw new MGXException(ex);
//        }
//    }
    @Override
    public String getSequence(final MGXReferenceI ref, int from, int to) throws MGXException {
        try {
            if (from < 0 || to < 0 || from > ref.getLength() - 1 || to > ref.getLength() - 1) {
                throw new IllegalArgumentException("Coordinates outside of reference sequence: requested " + from + "-" + to + ", reference length is " + ref.getLength());
            }
            return getDTOmaster().Reference().getSequence(ref.getId(), from, to);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MGXReferenceI> listGlobalReferences() throws MGXException {
        Iterator<ReferenceDTO> iter;
        try {
            iter = getDTOmaster().Reference().listGlobalReferences();
            return new BaseIterator<ReferenceDTO, MGXReferenceI>(iter) {
                @Override
                public MGXReferenceI next() {
                    MGXReferenceI reference = ReferenceDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return reference;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public TaskI<MGXReferenceI> installGlobalReference(MGXReferenceI obj) throws MGXException {
        try {
            UUID uuid = getDTOmaster().Reference().installGlobalReference(obj.getId());
            return getMaster().<MGXReferenceI>Task().get(obj, uuid, Task.TaskType.MODIFY);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public TaskI<MGXReferenceI> delete(MGXReferenceI obj) throws MGXException {
        try {
            UUID uuid = getDTOmaster().Reference().delete(obj.getId());
            return getMaster().<MGXReferenceI>Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public UploadBaseI createUploader(File localFile) throws MGXException {
        ReferenceUploader ru;
        try {
            ru = getDTOmaster().Reference().createUploader(localFile);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new ServerReferenceUploader(ru);
    }

    private static class ServerReferenceUploader extends UploadBaseI implements PropertyChangeListener {

        private final ReferenceUploader ru;

        public ServerReferenceUploader(ReferenceUploader ru) {
            this.ru = ru;
            ru.addPropertyChangeListener(this);
        }

        @Override
        public boolean upload() {
            boolean ret = ru.upload();
            if (!ret) {
                setErrorMessage(ru.getErrorMessage());
            }
            return ret;
        }

        @Override
        public long getNumElementsSent() {
            return ru.getProgress();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case TransferBase.MESSAGE:
                case TransferBase.TRANSFER_FAILED:
                    fireTaskChange(MESSAGE, evt.getNewValue());
                    break;
                default:
                    fireTaskChange(evt.getPropertyName(), ru.getProgress());
                    break;
            }
        }

    }
}
