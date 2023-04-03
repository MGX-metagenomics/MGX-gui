/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.groups.AssemblyGroupI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
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
        id = "de.cebitec.mgx.gui.actions.RemoveAssembledSeqRunFromGroupAction"
)
@ActionRegistration(
        displayName = "#CTL_RemoveAssembledSeqRunFromGroupAction",
        lazy = false,
        asynchronous = false
)
@NbBundle.Messages("CTL_RemoveAssembledSeqRunFromGroupAction=RemoveAssembledSeqRunFromGroupAction")
public class RemoveAssembledSeqRunFromGroupAction extends NodeAction implements LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup context;
    private Lookup.Result<AssembledSeqRunI> lkpInfo;

    public RemoveAssembledSeqRunFromGroupAction() {
        this(Utilities.actionsGlobalContext());
    }

    private RemoveAssembledSeqRunFromGroupAction(Lookup context) {
        putValue(NAME, "Remove run");
        this.context = context;
        init();
    }

    private void init() {
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(AssembledSeqRunI.class);
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
            AssembledSeqRunI run = nLookup.lookup(AssembledSeqRunI.class);
            AssemblyGroupI vGroup = nLookup.lookup(AssemblyGroupI.class);
            if (run != null && vGroup != null && vGroup.getContent().contains(run)) {
                vGroup.remove(run);
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        Collection<? extends AssembledSeqRunI> toRemove = Utilities.actionsGlobalContext().lookupAll(AssembledSeqRunI.class);
        AssemblyGroupI vGroup = Utilities.actionsGlobalContext().lookup(AssemblyGroupI.class);
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
