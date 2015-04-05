package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.jobmonitor.JobMonitorTopComponent;
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

@ActionID(
        category = "File",
        id = "de.cebitec.mgx.gui.actions.JobMonitorAction")
@ActionRegistration(
        iconBase = "de/cebitec/mgx/gui/actions/JobMonitor.png",
        displayName = "#CTL_JobMonitorAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1725),
    @ActionReference(path = "Toolbars/UndoRedo", position = 300)
})
@Messages("CTL_JobMonitorAction=Job Monitor")
public final class JobMonitorAction implements ActionListener {

    private final MGXMasterI context;

    public JobMonitorAction(MGXMasterI context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {

        TopComponent tc = JobMonitorTopComponent.getDefault();

        if (!tc.isOpened()) {
            Mode m = WindowManager.getDefault().findMode("satellite");
            if (m != null) {
                m.dockInto(tc);
            }
            tc.open();
        }
        if (!tc.isVisible()) {
            tc.setVisible(true);
        }
        tc.toFront();
    }
}
