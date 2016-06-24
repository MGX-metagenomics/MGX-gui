/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ReadModel extends BaseModel<SequenceI> {

    private List<SeqRunI> runs;
    private String term;

    public void setRuns(List<SeqRunI> runs) {
        this.runs = runs;
    }

    public void setTerm(String t) {
        this.term = t;
    }

    @Override
    public synchronized void update() {
        if (term == null || runs == null || runs.isEmpty()) {
            return;
        }

        clear();

        for (SeqRunI run : runs) {
            MGXMasterI master = run.getMaster();
            if (!master.isDeleted()) {
                Iterator<SequenceI> iter = null;
                try {
                    iter = master.Attribute().search(term, true, run);
                } catch (MGXException ex) {
                    Exceptions.printStackTrace(ex);
                }
                while (iter != null && iter.hasNext()) {
                    content.add(iter.next());
                }
            }
        }

        Collections.sort(content);
        fireContentsChanged();
    }
}
