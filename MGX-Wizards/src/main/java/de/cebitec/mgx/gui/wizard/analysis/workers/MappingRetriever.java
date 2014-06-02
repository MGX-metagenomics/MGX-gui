/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MappingI;
import java.util.Iterator;
import javax.swing.SwingWorker;

/**
 *
 * @author belmann
 */
public class MappingRetriever extends SwingWorker<Iterator<MappingI>, Void> {

    private MGXMasterI master;
    
    public MappingRetriever(MGXMasterI master) {
        this.master = master;
    }

    @Override
    protected Iterator<MappingI> doInBackground() throws Exception {
        return master.Mapping().fetchall();
    }
}
