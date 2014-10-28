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
        Iterator<SequenceI> iter = null;
        try {
            iter = currentMaster.Attribute().search(term, true, runs);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (iter == null) {
            return;
        }
        content.clear();
        while (iter.hasNext()) {
            SequenceI seq = iter.next();
            content.add(seq);
        }
        Collections.sort(content);
        fireContentsChanged();
    }
}
