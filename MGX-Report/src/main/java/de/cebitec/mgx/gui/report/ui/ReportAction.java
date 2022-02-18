/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.report.ui;

import de.cebitec.mgx.api.model.SeqRunI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
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
@ActionRegistration(
        iconBase="de/cebitec/mgx/gui/reportcom/report.svg",
        displayName = "Show Report"
)
@ActionReferences({
    //@ActionReference(path = "Menu/File", position = 1712),
    @ActionReference(path = "Toolbars/UndoRedo", position = 400)
})
//@Messages("CTL_ReportAction=Report")
public final class ReportAction implements ActionListener {

    private final SeqRunI context;

    public ReportAction(SeqRunI context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        TopComponent tc = ReportSummaryTopComponent.getDefault();

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
}
