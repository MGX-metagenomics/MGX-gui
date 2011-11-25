package de.cebitec.mgx.gui.access;

import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class SequenceAccess extends AccessBase<Sequence> {

//    public void sendSequences(long seqrun_id, SeqReaderI reader) {
//    }

//    private void sendChunk(SequenceDTOList seqList, String session_uuid) throws MGXServerException {
//    }

    @Override
    public long create(Sequence obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Sequence fetch(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sequence> fetchall() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Sequence obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
