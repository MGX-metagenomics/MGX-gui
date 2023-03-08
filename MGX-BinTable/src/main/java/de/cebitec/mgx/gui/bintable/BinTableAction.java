/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.bintable;

import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.api.model.assembly.BinI;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "de.cebitec.mgx.gui.bintable.BinTableAction"
)
@ActionRegistration(displayName = "Bin Table", lazy = false)
@ActionReferences({
    @ActionReference(path = "Toolbars/UndoRedo", position = 646)
})
public final class BinTableAction extends AbstractAction implements ContextAwareAction, LookupListener {

    private Lookup.Result<BinI> binResult;
    private Lookup.Result<AssemblyI> asmResult;

    public BinTableAction() {
        this(Utilities.actionsGlobalContext());
    }

    private BinTableAction(Lookup lkp) {
        super("Bin Table");

        binResult = lkp.lookupResult(BinI.class);
        binResult.addLookupListener(WeakListeners.create(LookupListener.class, this, lkp));

        asmResult = lkp.lookupResult(AssemblyI.class);
        asmResult.addLookupListener(WeakListeners.create(LookupListener.class, this, lkp));

        super.putValue("iconBase", "de/cebitec/mgx/gui/bintable/bintable.svg");

        Collection<? extends BinI> allBins = binResult.allInstances();
        Collection<? extends AssemblyI> allAsms = asmResult.allInstances();

        boolean disableAction = allBins.isEmpty() && allAsms.isEmpty();
        super.setEnabled(false);

    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        Action a =  new BinTableAction(lkp);
        Collection<? extends BinI> allBins = binResult.allInstances();
        Collection<? extends AssemblyI> allAsms = asmResult.allInstances();

        boolean disableAction = allBins.isEmpty() && allAsms.isEmpty();
        a.setEnabled(!disableAction);
        return a;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {

        TopComponent explorer = WindowManager.getDefault().findTopComponent("BinTableTopComponent");
        if (explorer == null) {
            explorer = new BinTableTopComponent();
        }

        if (!explorer.isOpened()) {
            Mode m = WindowManager.getDefault().findMode("editor");
            if (m != null) {
                m.dockInto(explorer);
            }
            explorer.open();
        }
        if (!explorer.isVisible()) {
            explorer.setVisible(true);
        }
        explorer.toFront();
        explorer.requestActive();
    }

    @Override
    public synchronized void resultChanged(LookupEvent le) {
        Collection<? extends BinI> allBins = binResult.allInstances();
        Collection<? extends AssemblyI> allAsms = asmResult.allInstances();

        boolean disableAction = allBins.isEmpty() && allAsms.isEmpty();

        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    BinTableAction.super.setEnabled(!disableAction);
                }
            });
        } else {
            super.setEnabled(!disableAction);
        }
    }
}
