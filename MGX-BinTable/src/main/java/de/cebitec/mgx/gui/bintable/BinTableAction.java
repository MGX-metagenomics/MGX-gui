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
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "de.cebitec.mgx.gui.bintable.BinTableAction"
)
@ActionRegistration(displayName = "Open bin table", lazy = true)
@ActionReferences({
    //@ActionReference(path = "Menu/File", position = 1712),
    @ActionReference(path = "Toolbars/UndoRedo", position = 645)
})
public final class BinTableAction extends AbstractAction implements ContextAwareAction, LookupListener {

    private final Lookup context;
    private Lookup.Result<BinI> binResult;
    private Lookup.Result<AssemblyI> asmResult;

    public BinTableAction() {
        this(Utilities.actionsGlobalContext());
    }

    private BinTableAction(Lookup context) {
        super();
        putValue(NAME, "Open bin table");
        //super.putValue("iconBase", "de/cebitec/mgx/gui/binexplorer/binexplorer.png");
        this.context = context;
        init();
    }

    private void init() {
        if (binResult != null && asmResult != null) {
            return;
        }
        binResult = context.lookupResult(BinI.class);
        binResult.addLookupListener(this);

        asmResult = context.lookupResult(AssemblyI.class);
        asmResult.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        return new BinTableAction(lkp);
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
