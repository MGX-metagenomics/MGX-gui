package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.nodefactory.JobBySeqRunNodeFactory;
import de.cebitec.mgx.gui.nodefactory.JobNodeFactory;
import de.cebitec.mgx.gui.nodes.JobNode;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JPopupMenu;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.NodePopupFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
    dtd = "-//de.cebitec.mgx.gui.jobmonitor//JobMonitor//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "JobMonitorTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "satellite", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.jobmonitor.JobMonitorTopComponent")
@ActionReference(path = "Menu/Window", position = 555)
@TopComponent.OpenActionRegistration(
    displayName = "#CTL_JobMonitorAction",
preferredID = "JobMonitorTopComponent")
@Messages({
    "CTL_JobMonitorAction=JobMonitor",
    "CTL_JobMonitorTopComponent=JobMonitor Window",
    "HINT_JobMonitorTopComponent=This is a JobMonitor window"
})
public final class JobMonitorTopComponent extends TopComponent implements LookupListener, ExplorerManager.Provider {

    private final Lookup.Result<MGXMaster> resultMaster;
    private final Lookup.Result<SeqRun> resultSeqRun;
    private MGXMaster currentMaster = null;
    private SeqRun currentSeqRun = null;
    private transient ExplorerManager explorerManager = new ExplorerManager();
    private final static int MASTER_MODE = 1;
    private final static int SEQRUN_MODE = 2;
    private int currentMode = MASTER_MODE;

    public JobMonitorTopComponent() {
        initComponents();
        setName(Bundle.CTL_JobMonitorTopComponent());
        setToolTipText(Bundle.HINT_JobMonitorTopComponent());
        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));
        explorerManager.setRootContext(new ProjectRootNode("No project selected", Children.LEAF));

        view.setPropertyColumns(//JobNode.TOOL_PROPERTY, "Tool",
                JobNode.SEQRUN_PROPERTY, "Run",
                JobNode.STATE_PROPERTY, "State");
        view.getOutline().setRootVisible(false);

        final NodePopupFactory origNpf = view.getNodePopupFactory();

        NodePopupFactory npf = new NodePopupFactory() {
            @Override
            public JPopupMenu createPopupMenu(int row, int column, Node[] selectedNodes, Component component) {
                setShowQuickFilter(false);
                return origNpf.createPopupMenu(row, 0, selectedNodes, component);
            }
        };
        view.setNodePopupFactory(npf);
        resultMaster = Utilities.actionsGlobalContext().lookupResult(MGXMaster.class);
        resultSeqRun = Utilities.actionsGlobalContext().lookupResult(SeqRun.class);

        update();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        view = new org.openide.explorer.view.OutlineView("Tool");

        setLayout(new java.awt.BorderLayout());
        add(view, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.OutlineView view;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        resultMaster.addLookupListener(this);
        resultSeqRun.addLookupListener(this);
        update();
    }

    @Override
    public void componentClosed() {
        resultMaster.removeLookupListener(this);
        resultSeqRun.removeLookupListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (!isFocusOwner()) {
            update();
        }
    }

    private void update() {
        boolean needUpdate = false;

        Collection<? extends SeqRun> runs = resultSeqRun.allInstances();

        if (runs.size() > 0) {
            currentMode = SEQRUN_MODE;

            for (SeqRun run : runs) {
                if (currentSeqRun == null || !run.equals(currentSeqRun)) {
                    currentSeqRun = run;
                    currentMaster = null;
                    needUpdate = true;
                }
            }
        } else {
            currentMode = MASTER_MODE;

            Collection<? extends MGXMaster> m = resultMaster.allInstances();
            for (MGXMaster newMaster : m) {
                if (currentMaster == null || !newMaster.equals(currentMaster)) {
                    currentMaster = newMaster;
                    currentSeqRun = null;
                    needUpdate = true;
                }
            }
        }


        if (needUpdate) {
            updateJobs();
        }
    }

//    @Override
//    public void actionPerformed(ActionEvent e) {
//        updateJobs();
//    }
    private void updateJobs() {
        if (currentMode == MASTER_MODE) {

            if (currentMaster == null) {
                return;
            }
            Children chld = Children.create(new JobNodeFactory(currentMaster), true);
            explorerManager.setRootContext(new ProjectRootNode(currentMaster, chld));
        } else {
            if (currentSeqRun == null) {
                return;
            }
            Children chld = Children.create(new JobBySeqRunNodeFactory(currentSeqRun), true);
            explorerManager.setRootContext(new ProjectRootNode((MGXMaster) currentSeqRun.getMaster(), chld));
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}
