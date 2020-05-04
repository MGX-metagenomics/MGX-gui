/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer;

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
        id = "de.cebitec.mgx.gui.binexplorer.BinExplorerAction"
)
@ActionRegistration(displayName = "not-used", lazy = false)
@ActionReferences({
    //@ActionReference(path = "Menu/File", position = 1712),
    @ActionReference(path = "Toolbars/UndoRedo", position = 645)
})
public final class BinExplorerAction extends AbstractAction implements ContextAwareAction, LookupListener {

    private final Lookup.Result<BinI> result;

    public BinExplorerAction() {
        this(Utilities.actionsGlobalContext());
    }

    private BinExplorerAction(Lookup lkp) {
        super("Bin Explorer");
        result = lkp.lookupResult(BinI.class);
        result.addLookupListener(WeakListeners.create(LookupListener.class, this, result));
        super.putValue("iconBase", "de/cebitec/mgx/gui/binexplorer/binexplorer.png");
        super.setEnabled(false);
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        return new BinExplorerAction(lkp);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        TopComponent tc = new BinExplorerTopComponent();

        if (!tc.isOpened()) {
            Mode m = WindowManager.getDefault().findMode("editor");
            if (m != null) {
                m.dockInto(tc);
            }
            tc.open();
        }
        if (!tc.isVisible()) {
            tc.setVisible(true);
        }
        tc.toFront();
        tc.requestActive();
    }

    @Override
    public synchronized void resultChanged(LookupEvent le) {
        Collection<? extends BinI> allBins = result.allInstances();
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    BinExplorerAction.super.setEnabled(!allBins.isEmpty());
                }
            });
        } else {
            super.setEnabled(!allBins.isEmpty());
        }
    }
}
