/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.report.ui;

import de.cebitec.mgx.api.model.SeqRunI;
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

/**
 *
 * @author yuki
 */
@ActionID(
        category = "File",
        id = "de.cebitec.mgx.gui.reportcom.ReportAction"
)
@ActionRegistration(displayName = "not-used", lazy = false)
@ActionReferences({
    @ActionReference(path = "Toolbars/UndoRedo", position = 400)
})
public final class ReportAction extends AbstractAction implements ContextAwareAction, LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup.Result<SeqRunI> result;

    public ReportAction() {
        this(Utilities.actionsGlobalContext());
    }

    private ReportAction(Lookup lkp) {
        super("Show report");
        result = lkp.lookupResult(SeqRunI.class);
        result.addLookupListener(WeakListeners.create(LookupListener.class, this, result));
        super.putValue("iconBase", "de/cebitec/mgx/gui/reportcom/report.png");
        super.setEnabled(false);
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        Action a = new ReportAction(lkp);
        Collection<? extends SeqRunI> all = result.allInstances();
        a.setEnabled(!all.isEmpty());
        return a;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        TopComponent tc = new ReportSummaryTopComponent();

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
                Collection<? extends SeqRunI> all = result.allInstances();
                ReportAction.this.setEnabled(!all.isEmpty());
            }

        });

    }
}
