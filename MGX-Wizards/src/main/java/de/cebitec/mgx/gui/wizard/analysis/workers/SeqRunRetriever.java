/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.Iterator;
import javax.swing.SwingWorker;

/**
 *
 * @author belmann
 */
public class SeqRunRetriever extends SwingWorker<Iterator<SeqRunI>, Void> {

    private final MGXMasterI master;

    public SeqRunRetriever(MGXMasterI master) {
        this.master = master;
    }

    @Override
    protected Iterator<SeqRunI> doInBackground() throws Exception {
        return master.SeqRun().fetchall();
    }
}
