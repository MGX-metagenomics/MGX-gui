package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.MappingAccessI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.exception.MGXTimeoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.BAMFileDownloader;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.MappedSequenceDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.MappingDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class MappingAccess extends MasterHolder implements MappingAccessI {


    public MappingAccess(MGXDTOMaster dtomaster, MGXMasterI master) throws MGXException {
        super(master, dtomaster);
    }

//    @Override
//    public MappingI create(MappingI obj) {
//        throw new UnsupportedOperationException("Not supported.");
//    }
    @Override
    public MappingI fetch(long id) throws MGXException {
        MappingDTO dto = null;
        try {
            dto = getDTOmaster().Mapping().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        MappingI ret = MappingDTOFactory.getInstance().toModel(getMaster(), dto);
        return ret;
    }

    @Override
    public Iterator<MappingI> fetchall() throws MGXException {
        try {
            Iterator<MappingDTO> fetchall = getDTOmaster().Mapping().fetchall().getMappingList().iterator();
            return new BaseIterator<MappingDTO, MappingI>(fetchall) {
                @Override
                public MappingI next() {
                    MappingI sr = MappingDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return sr;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MappingI> BySeqRun(SeqRunI run) throws MGXException {
        try {
            Iterator<MappingDTO> fetchall = getDTOmaster().Mapping().bySeqRun(run.getId());
            return new BaseIterator<MappingDTO, MappingI>(fetchall) {
                @Override
                public MappingI next() {
                    MappingI sr = MappingDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return sr;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MappingI> ByReference(MGXReferenceI ref) throws MGXException {
        try {
            Iterator<MappingDTO> fetchall = getDTOmaster().Mapping().byReference(ref.getId());
            return new BaseIterator<MappingDTO, MappingI>(fetchall) {
                @Override
                public MappingI next() {
                    MappingI sr = MappingDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return sr;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MappingI> ByJob(JobI job) throws MGXException {
        try {
            Iterator<MappingDTO> fetchall = getDTOmaster().Mapping().byJob(job.getId());
            return new BaseIterator<MappingDTO, MappingI>(fetchall) {
                @Override
                public MappingI next() {
                    MappingI sr = MappingDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return sr;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void update(MappingI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI<MappingI> delete(MappingI obj) throws MGXException {
        try {
            UUID uuid = getDTOmaster().Mapping().delete(obj.getId());
            return getMaster().<MappingI>Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public UUID openMapping(long id) throws MGXException {
        try {
            return getDTOmaster().Mapping().openMapping(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void closeMapping(UUID uuid) throws MGXException {
        try {
            getDTOmaster().Mapping().closeMapping(uuid);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No mapping session for")) {
                if (!getDTOmaster().isClosed()) {
                    // session already closed due to timeout
                    throw new MGXTimeoutException(ex);
                }
            }
            throw new MGXException(ex);
        }
    }

    @Override
    public long getMaxCoverage(UUID uuid) throws MGXException {
        try {
            return getDTOmaster().Mapping().getMaxCoverage(uuid);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No mapping session for")) {
                throw new MGXTimeoutException(ex);
            }
            throw new MGXException(ex);
        }
    }

    @Override
    public long getGenomicCoverage(UUID uuid) throws MGXException {
        try {
            return getDTOmaster().Mapping().getGenomicCoverage(uuid);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No mapping session for")) {
                throw new MGXTimeoutException(ex);
            }
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MappedSequenceI> byReferenceInterval(UUID uuid, int from, int to) throws MGXException {
        try {
            Iterator<MappedSequenceDTO> mapped = getDTOmaster().Mapping().byReferenceInterval(uuid, from, to);
            return new BaseIterator<MappedSequenceDTO, MappedSequenceI>(mapped) {
                @Override
                public MappedSequenceI next() {
                    MappedSequenceI ms = MappedSequenceDTOFactory.getInstance().toModel(iter.next());
                    return ms;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().contains("No mapping session for")) {
                throw new MGXTimeoutException(ex);
            }
            throw new MGXException(ex);
        }
    }

    @Override
    public DownloadBaseI createDownloader(MappingI mapping, OutputStream out) throws MGXException {
        try {
            final BAMFileDownloader fd = getDTOmaster().Mapping().createDownloader(mapping.getId(), out);
            return new ServerBAMFileDownloader(fd);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    private static class ServerBAMFileDownloader extends DownloadBaseI implements PropertyChangeListener {

        private final BAMFileDownloader fd;

        public ServerBAMFileDownloader(BAMFileDownloader fd) {
            this.fd = fd;
            fd.addPropertyChangeListener(this);
        }

        @Override
        public boolean download() {
            boolean ret = fd.download();
            fd.removePropertyChangeListener(this);
            if (!ret) {
                setErrorMessage(fd.getErrorMessage());
            }
            return ret;
        }

        @Override
        public long getProgress() {
            return fd.getProgress();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            fireTaskChange(evt.getPropertyName(), fd.getProgress());
        }

    }
}
