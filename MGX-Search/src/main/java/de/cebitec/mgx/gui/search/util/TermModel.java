/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.util.Collections;
import java.util.Iterator;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class TermModel extends BaseModel<String> {

    private MGXMasterI currentMaster;
    private SeqRunI[] runs;

    public void setMaster(MGXMasterI m) {
        currentMaster = m;
    }

    public void setRuns(SeqRunI[] runs) {
        this.runs = runs;
    }

    @Override
    public void update() {
        if (currentMaster == null || runs == null || runs.length == 0) {
            return;
        }
        String term = getSelectedItem();
        if (term == null || term.isEmpty()) {
            return;
        }
        
        Iterator<String> iter = null;
        try {
            iter = currentMaster.Attribute().find(term, runs);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        content.clear();
        while (iter != null && iter.hasNext()) {
            content.add(iter.next());
        }
        Collections.sort(content);
        fireContentsChanged();
    }

}
