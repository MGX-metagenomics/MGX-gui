package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.datatransfer.SeqByAttributeDownloader;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.client.datatransfer.SeqUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeDTOList;
import de.cebitec.mgx.dto.dto.AttributeDTOList.Builder;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SequenceDTOFactory;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.List;
import java.util.Set;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class SequenceAccess extends AccessBase<Sequence> {

    public void sendSequences(long seqrun_id, SeqReaderI reader) {
        try {
            getDTOmaster().Sequence().sendSequences(seqrun_id, reader);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public SeqUploader createUploader(long seqrun_id, SeqReaderI reader) {
        return getDTOmaster().Sequence().createUploader(seqrun_id, reader);
    }

    public SeqDownloader createDownloader(long seqrun_id, SeqWriterI writer) {
        return getDTOmaster().Sequence().createDownloader(seqrun_id, writer);
    }

    public void downloadSequences(long seqrun_id, SeqWriterI writer) {
        try {
            getDTOmaster().Sequence().downloadSequences(seqrun_id, writer);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public SeqByAttributeDownloader createDownloaderByAttributes(Set<Attribute> attrs, SeqWriterI<DNASequenceI> writer) {
        Builder b = AttributeDTOList.newBuilder();
        for (Attribute a : attrs) {
            b.addAttribute(AttributeDTOFactory.getInstance().toDTO(a));
        }
        return getDTOmaster().Sequence().createDownloaderByAttributes(b.build(), writer);
    }

    public void downloadSequencesForAttributes(Set<Attribute> attrs, SeqWriterI writer) {
        try {
            Builder b = AttributeDTOList.newBuilder();
            for (Attribute a : attrs) {
                b.addAttribute(AttributeDTOFactory.getInstance().toDTO(a));
            }
            getDTOmaster().Sequence().fetchAnnotatedReads(b.build(), writer);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public long create(Sequence obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Sequence fetch(long id) {
        SequenceDTO dto = null;
        try {
            dto = getDTOmaster().Sequence().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return SequenceDTOFactory.getInstance().toModel(dto);
    }

    @Override
    public List<Sequence> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(Sequence obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void delete(Sequence obj) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
