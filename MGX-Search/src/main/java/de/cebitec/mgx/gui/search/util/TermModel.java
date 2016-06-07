/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.util.ArrayList;
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
public class TermModel extends BaseModel<String> {

    private MGXMasterI currentMaster;
    private SeqRunI[] runs;
    private String prevTerm = null;
    private String term;

    public void setMaster(MGXMasterI m) {
        currentMaster = m;
        if (currentMaster == null || runs == null || runs.length == 0 || currentMaster.isDeleted()) {
            currentMaster = null;
            if (!content.isEmpty()) {
                content.clear();
                fireContentsChanged();
            }
        }
    }

    public void setRuns(SeqRunI[] runs) {
        prevTerm = null; // make sure update() fetches fresh data
        this.runs = runs;
    }

    public void setTerm(String t) {
        prevTerm = term;
        term = t;
    }

    @Override
    public synchronized void update() {
        if (currentMaster == null || runs == null || runs.length == 0 || currentMaster.isDeleted()) {
            currentMaster = null;
            if (!content.isEmpty()) {
                content.clear();
                fireContentsChanged();
            }
            return;
        }
        if (currentMaster == null || runs == null || runs.length == 0) {
            return;
        }
        if (term == null || term.isEmpty()) {
            return;
        }

        String prevSelection = getSelectedItem();

        // if previous term is a prefix of current term, we can 
        // narrow down the current data instead of contacting the
        // server
        if (prevTerm != null && term.startsWith(prevTerm)) {
            List<String> oldTerms = new ArrayList<>(content.size());
            oldTerms.addAll(content);
            content.clear();
            for (String s : oldTerms) {
                if (s.contains(term)) {
                    content.add(s);
                }
            }
            return;
        }

        SwingWorker<Iterator<String>, Void> sw = new SwingWorker<Iterator<String>, Void>() {

            @Override
            protected Iterator<String> doInBackground() throws Exception {
                if (!currentMaster.isDeleted()) {
                    return currentMaster.Attribute().find(term, runs);
                }
                return null;
            }
        };
        sw.execute();
        Iterator<String> iter;
        try {
            iter = sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        content.clear();
        while (iter != null && iter.hasNext()) {
            String term1 = iter.next();
            if (!content.contains(term1)) {
                content.add(term1);
            }
        }
        Collections.sort(content);

        if (prevSelection != null && content.contains(prevSelection)) {
            setSelectedItem(prevSelection);
        }
        fireContentsChanged();
    }

}
