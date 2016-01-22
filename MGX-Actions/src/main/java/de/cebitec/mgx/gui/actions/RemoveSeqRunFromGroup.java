///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.actions;
//
//import de.cebitec.mgx.api.groups.VisualizationGroupI;
//import de.cebitec.mgx.api.model.SeqRunI;
//import de.cebitec.mgx.gui.controller.RBAC;
//import java.util.Collection;
//import static javax.swing.Action.NAME;
//import org.openide.awt.ActionID;
//import org.openide.awt.ActionRegistration;
//import org.openide.nodes.Node;
//import org.openide.util.HelpCtx;
//import org.openide.util.Lookup;
//import org.openide.util.LookupEvent;
//import org.openide.util.LookupListener;
//import org.openide.util.Utilities;
//import org.openide.util.actions.NodeAction;
//
///**
// *
// * @author sjaenick
// */
//@ActionID(category = "Edit", id = "de.cebitec.mgx.gui.actions.RemoveSeqRunFromGroup")
//@ActionRegistration(displayName = "Remove seqrun", lazy = true)
//public class RemoveSeqRunFromGroup extends NodeAction implements LookupListener {
//
//    private final Lookup context;
//    private Lookup.Result<SeqRunI> lkpRun;
//    private Lookup.Result<VisualizationGroupI> lkpGrp;
//
//    public RemoveSeqRunFromGroup() {
//        this(Utilities.actionsGlobalContext());
//    }
//
//    private RemoveSeqRunFromGroup(Lookup context) {
//        putValue(NAME, "Remove run");
//        this.context = context;
//        init();
//    }
//
//    private void init() {
//        if (lkpRun != null) {
//            return;
//        }
//        lkpRun = context.lookupResult(SeqRunI.class);
//        lkpGrp = context.lookupResult(VisualizationGroupI.class);
//        lkpRun.addLookupListener(this);
//        lkpGrp.addLookupListener(this);
//        resultChanged(null);
//    }
//
//    @Override
//    public void resultChanged(LookupEvent ev) {
//        setEnabled(!lkpRun.allInstances().isEmpty() && !lkpGrp.allInstances().isEmpty());
//    }
//
//    @Override
//    protected boolean asynchronous() {
//        return true;
//    }
//
//    @Override
//    protected boolean enable(Node[] activatedNodes) {
//        return true;
//    }
//
//    @Override
//    public String getName() {
//        return "Remove run";
//    }
//
//    @Override
//    public HelpCtx getHelpCtx() {
//        return null;
//    }
//
//    @Override
//    protected void performAction(Node[] activatedNodes) {
//        Collection<? extends SeqRunI> seqruns = lkpRun.allInstances();
//        if (seqruns.isEmpty()) {
//            return;
//        }
//        Collection<? extends VisualizationGroupI> groups = lkpGrp.allInstances();
//        //if (groups.s)
//    }
//
//    @Override
//    public boolean isEnabled() {
//        init();
//        Collection<? extends SeqRunI> seqruns = lkpRun.allInstances();
//        long numSeqs = 0;
//        for (SeqRunI sr : seqruns) {
//            numSeqs += sr.getNumSequences();
//        }
//        // make sure we don't accidentally start analysis on datasets without sequences
//        return super.isEnabled() && RBAC.isUser() && numSeqs > 0;
//    }
//}
