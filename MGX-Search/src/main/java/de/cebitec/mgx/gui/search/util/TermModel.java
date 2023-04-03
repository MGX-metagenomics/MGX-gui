/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
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

    @Serial
    private static final long serialVersionUID = 1L;

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
            if (!isEmpty()) {
                clear();
                fireContentsChanged();
            }
            return;
        }

        String prevSelection = getSelectedItem();

        // if previous term is a prefix of current term, we can 
        // narrow down the current data instead of contacting the
        // server
        if (prevTerm != null && !prevTerm.isEmpty() && term.startsWith(prevTerm)) {
            List<String> oldTerms = new ArrayList<>(getSize());
            oldTerms.addAll(getAll());
            clear();
            // case-insensitive comparison
            for (String s : oldTerms) {
                if (s.toLowerCase().contains(term)) {
                    add(s);
                }
            }
            fireContentsChanged();
            if (contains(prevSelection)) {
                setSelectedItem(prevSelection);
            }
            return;
        }

        SwingWorker<List<String>, Void> sw = new SwingWorker<List<String>, Void>() {

            @Override
            protected List<String> doInBackground() throws Exception {
                List<String> terms = new ArrayList<>();
                MGXMasterI master = run.getMaster();
                if (!master.isDeleted()) {
                    Iterator<String> iter = master.Attribute().find(term, run);
                    while (iter != null && iter.hasNext()) {
                        terms.add(iter.next());
                    }
                }
                Collections.sort(terms);
                return terms;
            }
        };
        sw.execute();
        Collection<String> set;
        try {
            set = sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        clear();
        addAll(set);

        if (prevSelection != null && contains(prevSelection)) {
            setSelectedItem(prevSelection);
        }
        fireContentsChanged();
    }
}
