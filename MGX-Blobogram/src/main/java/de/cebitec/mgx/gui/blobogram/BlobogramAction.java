/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.blobogram;

import de.cebitec.mgx.api.model.assembly.BinI;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.Serial;
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
        id = "de.cebitec.mgx.gui.blobogram.BlobogramAction"
)
@ActionRegistration(displayName = "not-used", lazy = false)
@ActionReferences({
    @ActionReference(path = "Toolbars/UndoRedo", position = 635)
})
public final class BlobogramAction extends AbstractAction implements ContextAwareAction, LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup.Result<BinI> result;

    public BlobogramAction() {
        this(Utilities.actionsGlobalContext());
    }

    private BlobogramAction(Lookup lkp) {
        super("Blob Plot");
        result = lkp.lookupResult(BinI.class);
        result.addLookupListener(WeakListeners.create(LookupListener.class, this, result));
        super.putValue("iconBase", "de/cebitec/mgx/gui/blobogram/blobogram.png");
        super.setEnabled(false);
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        Action a = new BlobogramAction(lkp);
        Collection<? extends BinI> allBins = result.allInstances();
        a.setEnabled(!allBins.isEmpty());
        return a;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        TopComponent tc = BlobogramTopComponent.getDefault();

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

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Collection<? extends BinI> allBins = result.allInstances();
                BlobogramAction.this.setEnabled(!allBins.isEmpty());
            }

        });

    }
}
