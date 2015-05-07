package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.SequenceAccessI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.SeqByAttributeDownloader;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.AttributeDTOList.Builder;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class SequenceAccess extends AccessBase<SequenceI> implements SequenceAccessI {

    public SequenceAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        super(master, dtomaster);
    }

    @Override
    public void sendSequences(SeqRunI seqrun, SeqReaderI<DNASequenceI> reader) throws MGXException {
        try {
            getDTOmaster().Sequence().sendSequences(seqrun.getId(), reader);
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public UploadBaseI createUploader(SeqRunI seqrun, SeqReaderI<DNASequenceI> reader) {
        final SeqUploader su = getDTOmaster().Sequence().createUploader(seqrun.getId(), reader);
        return new ServerSeqRunUploader(seqrun, su);
    }

    @Override
    public DownloadBaseI createDownloader(SeqRunI seqrun, SeqWriterI<DNASequenceI> writer, boolean closeWriter) {
        final SeqDownloader sd = getDTOmaster().Sequence().createDownloader(seqrun.getId(), writer, closeWriter);
        return new ServerSeqRunDownloader(sd);
    }

//    public void downloadSXXXequences(long seqrun_id, SeqWriterI<DNASequenceI> writer, boolean closeWriter) throws MGXException {
//        try {
//            getDTOmaster().Sequence().downloadSequences(seqrun_id, writer, closeWriter);
//        } catch (MGXServerException ex) {
//            throw new MGXException(ex);
//        }
//    }

    @Override
    public DownloadBaseI createDownloaderByAttributes(Set<AttributeI> attrs, SeqWriterI<DNASequenceI> writer, boolean closeWriter) {
        Builder b = AttributeDTOList.newBuilder();
        for (AttributeI a : attrs) {
            b.addAttribute(AttributeDTOFactory.getInstance().toDTO(a));
        }
        final SeqByAttributeDownloader dl = getDTOmaster().Sequence().createDownloaderByAttributes(b.build(), writer, closeWriter);
        return new ServerSeqRunDownloader(dl);
    }

    @Override
    public void downloadSequencesForAttributes(Set<AttributeI> attrs, SeqWriterI<DNASequenceI> writer, boolean closeWriter) throws MGXException {
        try {
            Builder b = AttributeDTOList.newBuilder();
            for (AttributeI a : attrs) {
                b.addAttribute(AttributeDTOFactory.getInstance().toDTO(a));
            }
            getDTOmaster().Sequence().fetchAnnotatedReads(b.build(), writer, closeWriter);
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public SequenceI create(SequenceI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public SequenceI fetch(long id) throws MGXException {
        SequenceDTO dto = null;
        try {
            dto = getDTOmaster().Sequence().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
        return SequenceDTOFactory.getInstance().toModel(getMaster(), dto);
    }

    @Override
    public void fetchSeqData(Iterable<SequenceI> seqs) throws MGXException {
        Map<Long, SequenceI> idx = new HashMap<>();
        try {
            for (SequenceI s : seqs) {
                if (s.getSequence() == null) {
                    idx.put(s.getId(), s);
                }
            }

            if (idx.isEmpty()) {
                return;
            }

            SequenceDTOList dto = getDTOmaster().Sequence().fetchSeqData(idx.keySet());

            // update fields
            for (SequenceDTO sdto : dto.getSeqList()) {
                assert idx.containsKey(sdto.getId());
                SequenceI s = idx.get(sdto.getId());

                s.setName(sdto.getName());
                if (sdto.hasLength()) {
                    s.setLength(sdto.getLength());
                }
                if (sdto.hasSequence()) {
                    s.setSequence(sdto.getSequence());
                }
            }
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        } finally {
            idx.clear();
        }
    }

    @Override
    public Iterator<SequenceI> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(SequenceI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI<SequenceI> delete(SequenceI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    private class ServerSeqRunUploader extends UploadBaseI implements PropertyChangeListener {

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

    private class ServerSeqRunDownloader extends DownloadBaseI implements PropertyChangeListener {

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
            fireTaskChange(evt.getPropertyName(), sd.getProgress());
        }

    }
}
