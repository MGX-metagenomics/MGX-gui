/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ReadModel extends BaseModel<SequenceI> {

    private MGXMasterI currentMaster;
    private SeqRunI[] runs;
    private String term;

    public void setMaster(MGXMasterI m) {
        currentMaster = m;
        if (currentMaster == null || runs == null || runs.length == 0 || currentMaster.isDeleted()) {
            content.clear();
            currentMaster = null;
            fireContentsChanged();
        }
    }

    public void setRuns(SeqRunI[] runs) {
        this.runs = runs;
    }

    public void setTerm(String t) {
        this.term = t;
    }

    @Override
    public void update() {
        if (currentMaster == null || term == null || runs == null || runs.length == 0) {
            return;
        }

        SwingWorker<Iterator<SequenceI>, Void> sw = new SwingWorker<Iterator<SequenceI>, Void>() {

            @Override
            protected Iterator<SequenceI> doInBackground() throws Exception {
                if (!currentMaster.isDeleted()) {
                    return currentMaster.Attribute().search(term, true, runs);
                }
                return null;
            }
        };
        sw.execute();

        Iterator<SequenceI> iter;
        try {
            iter = sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        content.clear();
        while (iter != null && iter.hasNext()) {
            SequenceI seq = iter.next();
            content.add(seq);
        }
        Collections.sort(content);
        fireContentsChanged();
    }
}
