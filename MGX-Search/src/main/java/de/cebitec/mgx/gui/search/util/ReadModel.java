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
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ReadModel extends BaseModel<SequenceI> {

    @Serial
    private static final long serialVersionUID = 1L;

    private SeqRunI run;
    private String term;

    public void setRun(SeqRunI run) {
        this.run = run;
    }

    public void setTerm(String t) {
        this.term = t;
    }

    @Override
    public synchronized void update() {
        if (term == null || term.isEmpty() || run == null) {
            clear();
            return;
        }
        clear();

        List<SequenceI> tmp = new ArrayList<>();
        MGXMasterI master = run.getMaster();
        if (!master.isDeleted()) {
            Iterator<SequenceI> iter = null;
            try {
                iter = master.Attribute().search(term, true, run);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
            while (iter != null && iter.hasNext()) {
                tmp.add(iter.next());
            }
        }

        Collections.sort(tmp);
        addAll(tmp);
        fireContentsChanged();
    }
}
