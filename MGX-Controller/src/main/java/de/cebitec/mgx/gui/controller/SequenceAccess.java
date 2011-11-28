package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class SequenceAccess extends AccessBase<Sequence> {

    public void sendSequences(long seqrun_id, SeqReaderI reader) {
        try {
            getDTOmaster().Sequence().sendSequences(seqrun_id, reader);
        } catch (MGXServerException ex) {
            Logger.getLogger(SequenceAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Long create(Sequence obj) {
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
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
