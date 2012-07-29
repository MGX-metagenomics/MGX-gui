package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.upload.SeqUploader;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.util.List;
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

    @Override
    public long create(Sequence obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Sequence fetch(long id) {
        throw new UnsupportedOperationException("Not supported.");
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
