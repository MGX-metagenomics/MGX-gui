/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Mapping;
import java.util.Iterator;
import javax.swing.SwingWorker;

/**
 *
 * @author belmann
 */
public class MappingRetriever extends SwingWorker<Iterator<Mapping>, Void> {

    private MGXMaster master;
    
    public MappingRetriever(MGXMaster master) {
        this.master = master;
    }

    @Override
    protected Iterator<Mapping> doInBackground() throws Exception {
        return master.Mapping().fetchall();
    }
}
