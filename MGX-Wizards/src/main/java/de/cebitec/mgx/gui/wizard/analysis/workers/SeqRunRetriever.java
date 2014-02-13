/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.util.Iterator;
import javax.swing.SwingWorker;

/**
 *
 * @author belmann
 */
public class SeqRunRetriever extends SwingWorker<Iterator<SeqRun>, Void> {

    private final MGXMaster master;

    public SeqRunRetriever(MGXMaster master) {
        this.master = master;
    }

    @Override
    protected Iterator<SeqRun> doInBackground() throws Exception {
        return master.SeqRun().fetchall();
    }
}
