package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import java.util.Iterator;
import javax.swing.SwingWorker;

/**
 *
 * @author sjaenick
 */
public class ReferenceRetriever extends SwingWorker<Iterator<MGXReferenceI>, Void> {

    private final MGXMasterI master;

    public ReferenceRetriever(MGXMasterI master) {
        this.master = master;
    }

    @Override
    protected Iterator<MGXReferenceI> doInBackground() throws Exception {
        return master.Reference().fetchall();
    }
}
