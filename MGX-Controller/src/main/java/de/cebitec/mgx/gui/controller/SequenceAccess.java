package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.SequenceAccessI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.exception.MGXTimeoutException;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqByAttributeDownloader;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.exception.MGXDTOTimeoutException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.AttributeDTOList.Builder;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqWriterI;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class SequenceAccess extends MasterHolder implements SequenceAccessI {

    public SequenceAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public Iterator<Long> fetchSequenceIDs(AttributeI attr) throws MGXException {
        try {
            Iterator<Long> ret = getDTOmaster().Sequence().fetchSequenceIDs(attr.getId());
            return ret;
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOTimeoutException tex) {
            throw new MGXTimeoutException(tex.getMessage());
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void sendSequences(SeqRunI seqrun, SeqReaderI<? extends DNASequenceI> reader) throws MGXException {
        try {
            getDTOmaster().Sequence().sendSequences(seqrun.getId(), seqrun.isPaired(), reader);      
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public UploadBaseI createUploader(SeqRunI seqrun, SeqReaderI<? extends DNASequenceI> reader) throws MGXException {
        try {
            final SeqUploader su = getDTOmaster().Sequence().createUploader(seqrun.getId(), seqrun.isPaired(), reader);
            return new ServerSeqRunUploader(seqrun, su);      
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public DownloadBaseI createDownloader(SeqRunI seqrun, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) throws MGXException {
        try {
            final SeqDownloader sd = getDTOmaster().Sequence().createDownloader(seqrun.getId(), writer, closeWriter);
            ServerSeqRunDownloader ret = new ServerSeqRunDownloader(sd);
            seqrun.addPropertyChangeListener(ret);
            return ret;      
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public DownloadBaseI createDownloaderByAttributes(Set<AttributeI> attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) throws MGXException {
        Builder b = AttributeDTOList.newBuilder();
        for (AttributeI a : attrs) {
            b.addAttribute(AttributeDTOFactory.getInstance().toDTO(a));
        }
        final SeqByAttributeDownloader dl;
        try {
            dl = getDTOmaster().Sequence().createDownloaderByAttributes(b.build(), writer, closeWriter);      
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new ServerSeqRunDownloader(dl);
    }

    @Override
    public void downloadSequencesForAttributes(Set<AttributeI> attrs, SeqWriterI<? extends DNASequenceI> writer, boolean closeWriter) throws MGXException {
        try {
            Builder b = AttributeDTOList.newBuilder();
            for (AttributeI a : attrs) {
                b.addAttribute(AttributeDTOFactory.getInstance().toDTO(a));
            }
            getDTOmaster().Sequence().fetchAnnotatedReads(b.build(), writer, closeWriter);      
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public SequenceI fetch(long id) throws MGXException {
        SequenceDTO dto = null;
        try {
            dto = getDTOmaster().Sequence().fetch(id);      
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return SequenceDTOFactory.getInstance().toModel(getMaster(), dto);
    }

    @Override
    public Iterator<SequenceI> fetchByIds(long[] ids) throws MGXException {
        try {
            Iterator<SequenceDTO> iter = getDTOmaster().Sequence().fetchByIds(ids).getSeqList().iterator();
            return new BaseIterator<SequenceDTO, SequenceI>(iter) {

                private final SequenceDTOFactory fact = SequenceDTOFactory.getInstance();

                @Override
                public SequenceI next() {
                    return fact.toModel(null, iter.next());
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void fetchSeqData(Iterable<SequenceI> seqs) throws MGXException {
        TLongObjectMap<SequenceI> idx = new TLongObjectHashMap<>();
        try {
            for (SequenceI s : seqs) {
                if (s.getSequence() == null) {
                    idx.put(s.getId(), s);
                }
            }

            if (idx.isEmpty()) {
                return;
            }

            SequenceDTOList dto = getDTOmaster().Sequence().fetchByIds(idx.keys());

            // update fields
            for (SequenceDTO sdto : dto.getSeqList()) {
                if (!idx.containsKey(sdto.getId())) {
                    throw new MGXException("MGX server returned sequence ID " + sdto.getId() + " which was not included in the request");
                }
                SequenceI s = idx.get(sdto.getId());

                s.setName(sdto.getName());
                if (sdto.getLength() > 0) {
                    s.setLength(sdto.getLength());
                }
                if (!sdto.getSequence().isEmpty()) {
                    s.setSequence(new String(sdto.getSequence().toByteArray()));
                }
            }      
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        } finally {
            idx.clear();
        }
    }

    private static class ServerSeqRunUploader extends UploadBaseI implements PropertyChangeListener {

        private final SeqUploader su;
        private final SeqRunI run;

        public ServerSeqRunUploader(SeqRunI run, SeqUploader su) {
            this.run = run;
            this.su = su;
            su.addPropertyChangeListener(this);
        }

        @Override
        public boolean upload() {
            boolean ret = su.upload();
            if (!ret) {
                setErrorMessage(su.getErrorMessage());
            } else {
                run.setNumSequences(su.getProgress());
                run.modified();
            }
            return ret;
        }

        @Override
        public long getNumElementsSent() {
            return su.getProgress();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            fireTaskChange(evt.getPropertyName(), su.getProgress());
        }

    }

    private static class ServerSeqRunDownloader extends DownloadBaseI implements PropertyChangeListener {

        private final SeqDownloader sd;

        public ServerSeqRunDownloader(SeqDownloader sd) {
            this.sd = sd;
            sd.addPropertyChangeListener(this);
        }

        @Override
        public boolean download() {
            boolean ret = sd.download();
            if (!ret) {
                setErrorMessage(sd.getErrorMessage());
            }
            sd.removePropertyChangeListener(this);
            return ret;
        }

        @Override
        public long getProgress() {
            return sd.getProgress();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case TRANSFER_FAILED:
                    fireTaskChange(evt.getPropertyName(), evt.getNewValue());
                    break;
                default:
                    fireTaskChange(evt.getPropertyName(), sd.getProgress());
            }
        }

    }
}
