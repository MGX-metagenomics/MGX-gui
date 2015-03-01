/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.qcmon.QCTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "de.cebitec.mgx.gui.actions.QCAction"
)
@ActionRegistration(
        iconBase = "de/cebitec/mgx/gui/actions/QC.png",
        displayName = "#CTL_QCAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1712),
    @ActionReference(path = "Toolbars/UndoRedo", position = 299)
})
@Messages("CTL_QCAction=Quality control")
public final class QCAction implements ActionListener {

    private final SeqRunI context;

    public QCAction(SeqRunI context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        QCTopComponent qc = new QCTopComponent();
        qc.setVisible(true);

        Mode m = WindowManager.getDefault().findMode("satellite");
        if (m != null) {
            m.dockInto(qc);
        } else {
            System.err.println("satellite mode not found");
        }
        qc.open();
    }
}
