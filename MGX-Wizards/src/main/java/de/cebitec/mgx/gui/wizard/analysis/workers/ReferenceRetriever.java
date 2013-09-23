package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import java.util.Iterator;
import javax.swing.SwingWorker;

/**
 *
 * @author sjaenick
 */
public class ReferenceRetriever extends SwingWorker<Pair<Iterator<Reference>, Iterator<Reference>>, Void> {

    private final MGXMaster master;

    public ReferenceRetriever(MGXMaster master) {
        this.master = master;
    }

    @Override
    protected Pair<Iterator<Reference>, Iterator<Reference>> doInBackground() throws Exception {
        Iterator<Reference> projectReferences = master.Reference().fetchall();
        Iterator<Reference> globalReferences = master.Reference().listGlobalReferences();
        return new Pair<>(globalReferences, projectReferences);
    }
}
