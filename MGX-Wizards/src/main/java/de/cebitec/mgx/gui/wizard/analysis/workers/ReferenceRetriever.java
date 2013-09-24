package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.Iterator;
import javax.swing.SwingWorker;

/**
 *
 * @author sjaenick
 */
public class ReferenceRetriever extends SwingWorker<Iterator<Reference>, Void> {

    private final MGXMaster master;

    public ReferenceRetriever(MGXMaster master) {
        this.master = master;
    }

    @Override
    protected Iterator<Reference> doInBackground() throws Exception {
        return master.Reference().fetchall();
    }
}
