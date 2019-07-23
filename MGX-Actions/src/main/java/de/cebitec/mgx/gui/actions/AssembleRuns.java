/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGX2MasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.rbac.RBAC;
import java.util.Collection;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
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
@ActionID(category = "Edit", id = "de.cebitec.mgx.gui.actions.AssembleRuns")
@ActionRegistration(displayName = "Assemble", lazy = false)
public class AssembleRuns extends NodeAction implements LookupListener {

    private final Lookup context;
    private Lookup.Result<SeqRunI> lkpInfo;

    public AssembleRuns() {
        this(Utilities.actionsGlobalContext());
    }

    private AssembleRuns(Lookup context) {
        putValue(NAME, "Assemble");
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
    public Action createContextAwareInstance(Lookup lkp) {
        return this;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }

    @Override
    protected boolean asynchronous() {
        return true;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return "Assemble";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        final MGX2MasterI master = context.lookup(MGX2MasterI.class);
        Collection<? extends SeqRunI> seqruns = lkpInfo.allInstances();
        if (seqruns.isEmpty()) {
            return;
        }

        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Assembly name:", "Choose a distinct assembly name");
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(nd))) {
            final String asmName = nd.getInputText().trim();
        }
    }

    @Override
    public boolean isEnabled() {
        init();
        Collection<? extends SeqRunI> seqruns = lkpInfo.allInstances();
        long numSeqs = 0;
        for (SeqRunI sr : seqruns) {
            numSeqs += sr.getNumSequences();
        }
        // make sure we don't accidentally start analysis on datasets without sequences
        return super.isEnabled() && RBAC.isUser() && numSeqs > 0;
    }

}
