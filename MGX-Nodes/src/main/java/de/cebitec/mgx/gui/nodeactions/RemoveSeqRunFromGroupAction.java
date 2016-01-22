/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.Collection;
import static javax.swing.Action.NAME;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author sjaenick
 */
public class RemoveSeqRunFromGroupAction extends NodeAction implements LookupListener {

    private final Lookup context;
    private Lookup.Result<SeqRunI> lkpInfo;

    public RemoveSeqRunFromGroupAction() {
        this(Utilities.actionsGlobalContext());
    }

    private RemoveSeqRunFromGroupAction(Lookup context) {
        putValue(NAME, "Remove run");
        this.context = context;
        init();
    }

    private void init() {
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(SeqRunI.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(enable(getActivatedNodes()));
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        for (Node node : activatedNodes) {
            Lookup nLookup = node.getLookup();
            SeqRunI run = nLookup.lookup(SeqRunI.class);
            VisualizationGroupI vGroup = nLookup.lookup(VisualizationGroupI.class);
            if (run != null && vGroup != null && vGroup.getSeqRuns().contains(run)) {
                vGroup.removeSeqRun(run);
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        Collection<? extends SeqRunI> toRemove = Utilities.actionsGlobalContext().lookupAll(SeqRunI.class);
        VisualizationGroupI vGroup = Utilities.actionsGlobalContext().lookup(VisualizationGroupI.class);
        return vGroup != null && !toRemove.isEmpty();
    }

    @Override
    public String getName() {
        return "Remove run";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
