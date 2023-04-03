/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.io.Serial;
import java.util.Collection;
import static javax.swing.Action.NAME;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author sjaenick
 */
@ActionID(
        category = "Edit",
        id = "de.cebitec.mgx.gui.actions.RemoveSeqRunFromGroupAction"
)
@ActionRegistration(
        displayName = "#CTL_RemoveSeqRunFromGroupAction",
        lazy = false,
        asynchronous = false
)
@NbBundle.Messages("CTL_RemoveSeqRunFromGroupAction=RemoveSeqRunFromGroupAction")
public class RemoveSeqRunFromGroupAction extends NodeAction implements LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;

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
            if (run != null && vGroup != null && vGroup.getContent().contains(run)) {
                vGroup.remove(run);
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
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
