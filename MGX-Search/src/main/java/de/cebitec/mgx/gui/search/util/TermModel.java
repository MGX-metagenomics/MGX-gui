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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class TermModel extends BaseModel<String> {

    private SeqRunI run;
    private String prevTerm = null;
    private String term;

    public void setRun(SeqRunI run) {
        prevTerm = null; // make sure update() fetches fresh data
        this.run = run;
    }

    public void setTerm(String t) {
        prevTerm = term;
        term = t.toLowerCase();
    }

    @Override
    public synchronized void update() {
        if (run == null || term == null || term.isEmpty()) {
            if (!content.isEmpty()) {
                content.clear();
                fireContentsChanged();
            }
            return;
        }

        String prevSelection = getSelectedItem();

        // if previous term is a prefix of current term, we can 
        // narrow down the current data instead of contacting the
        // server
        if (prevTerm != null && !prevTerm.isEmpty() && term.startsWith(prevTerm)) {
            List<String> oldTerms = new ArrayList<>(content.size());
            oldTerms.addAll(content);
            content.clear();
            // case-insensitive comparison
            for (String s : oldTerms) {
                if (s.toLowerCase().contains(term)) {
                    content.add(s);
                }
            }
            fireContentsChanged();
            if (content.contains(prevSelection)) {
                setSelectedItem(prevSelection);
            }
            return;
        }

        SwingWorker<Iterator<String>, Void> sw = new SwingWorker<Iterator<String>, Void>() {

            @Override
            protected Iterator<String> doInBackground() throws Exception {
                Set<String> terms = new HashSet<>();
                MGXMasterI master = run.getMaster();
                if (!master.isDeleted()) {
                    Iterator<String> iter = master.Attribute().find(term, run);
                    while (iter != null && iter.hasNext()) {
                        terms.add(iter.next());
                    }
                }
                return terms.iterator();
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
