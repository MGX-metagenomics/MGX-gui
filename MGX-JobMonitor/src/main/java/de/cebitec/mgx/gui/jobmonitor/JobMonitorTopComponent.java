package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodes.JobNode;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.swing.ActionMap;
import javax.swing.JPopupMenu;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.NodePopupFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.jobmonitor//JobMonitor//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "JobMonitorTopComponent",
        iconBase = "de/cebitec/mgx/gui/jobmonitor/JobMonitor.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "satellite", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.jobmonitor.JobMonitorTopComponent")
//@ActionReference(path = "Menu/Window", position = 555)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_JobMonitorAction",
        preferredID = "JobMonitorTopComponent")
@Messages({
    "CTL_JobMonitorAction=Job Monitor",
    "CTL_JobMonitorTopComponent=Job Monitor",
    "HINT_JobMonitorTopComponent=Job Monitor"
})
public final class JobMonitorTopComponent extends TopComponent implements LookupListener, ExplorerManager.Provider, PropertyChangeListener {

    private static enum MODE {
        MASTER,
        SEQRUN,
        ASSEMBLY
    };

    private final Lookup.Result<MGXMasterI> resultMaster;
    private final Lookup.Result<SeqRunI> resultSeqRun;
    private final Lookup.Result<AssemblyI> resultAssembly;
    private final Lookup.Result<JobI> resultJobs;
    private MGXMasterI currentMaster = null;
    private Set<SeqRunI> currentSeqRuns = new ConcurrentSkipListSet<>();
    private List<AssemblyI> currentAssemblies = new ArrayList<>();
    private transient ExplorerManager explorerManager = new ExplorerManager();
    private MODE currentMode = MODE.MASTER;
    private ProjectRootNode currentRoot = null;

    private JobMonitorTopComponent() {
        initComponents();
        setName(Bundle.CTL_JobMonitorTopComponent());
        setToolTipText(Bundle.HINT_JobMonitorTopComponent());
        ActionMap map = getActionMap();
        //map.put("delete", ExplorerUtils.actionDelete(explorerManager, true));
        associateLookup(ExplorerUtils.createLookup(explorerManager, map));
        explorerManager.setRootContext(new ProjectRootNode("No project selected"));

        view.setPropertyColumns(
                JobNode.SEQRUN_OR_ASSEMBLY_PROPERTY, "Run(s) / Assemblies",
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
        resultMaster = Utilities.actionsGlobalContext().lookupResult(MGXMasterI.class);
        resultSeqRun = Utilities.actionsGlobalContext().lookupResult(SeqRunI.class);
        resultAssembly = Utilities.actionsGlobalContext().lookupResult(AssemblyI.class);
        resultJobs = Utilities.actionsGlobalContext().lookupResult(JobI.class);

        update();
    }

    private static JobMonitorTopComponent instance = null;

    public static JobMonitorTopComponent getDefault() {
        if (instance == null) {
            instance = new JobMonitorTopComponent();
        }
        return instance;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
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
        super.componentOpened();
        resultMaster.addLookupListener(this);
        resultSeqRun.addLookupListener(this);
        resultAssembly.addLookupListener(this);
        resultJobs.addLookupListener(this);
        update();
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        resultMaster.removeLookupListener(this);
        resultSeqRun.removeLookupListener(this);
        resultAssembly.removeLookupListener(this);
        resultJobs.removeLookupListener(this);
        if (currentRoot != null) {
            try {
                currentRoot.destroy();
            } catch (IOException ex) {
            }
            currentRoot = null;
        }
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public void resultChanged(LookupEvent le) {
        update();
    }

    private void update() {

        Collection<? extends SeqRunI> runs = resultSeqRun.allInstances();
        Collection<? extends AssemblyI> assemblies = resultAssembly.allInstances();
        Collection<? extends MGXMasterI> masters = resultMaster.allInstances();
        Collection<? extends JobI> jobs = resultJobs.allInstances();

        if (!jobs.isEmpty()) {
            return; // selection within own topcomponent, no update
        }

        // remove propertychangelisteners from previous runs and assemblies
        if (currentMaster != null) {
            currentMaster.removePropertyChangeListener(this);
            currentMaster = null;
        }
        for (SeqRunI run : currentSeqRuns) {
            run.removePropertyChangeListener(this);
        }
        currentSeqRuns.clear();
        for (AssemblyI ass : currentAssemblies) {
            ass.removePropertyChangeListener(this);
        }
        currentAssemblies.clear();

        if (!runs.isEmpty()) {
            currentMode = MODE.SEQRUN;
            currentSeqRuns.addAll(runs);
        } else if (!assemblies.isEmpty()) {
            currentMode = MODE.ASSEMBLY;
            currentAssemblies.addAll(assemblies);
        } else if (!masters.isEmpty()) {
            currentMode = MODE.MASTER;

            for (MGXMasterI newMaster : masters) {
                if (!newMaster.equals(currentMaster)) {
                    currentMaster = newMaster;
                }
            }

        } else {
            return;
        }

        // add propertychangelisteners to new objects
        if (currentMaster != null) {
            currentMaster.addPropertyChangeListener(this);
        }
        for (SeqRunI run : currentSeqRuns) {
            run.addPropertyChangeListener(this);
        }
        for (AssemblyI ass : currentAssemblies) {
            ass.addPropertyChangeListener(this);
        }

        updateJobs();
    }

    private synchronized void updateJobs() {
        if (currentRoot != null) {
            try {
                currentRoot.destroy();
                currentRoot = null;
            } catch (IOException ex) {
            }
        }
        if (currentMode == MODE.MASTER && currentMaster != null) {
            currentRoot = new ProjectRootNode(currentMaster);
        } else if (currentMode == MODE.ASSEMBLY) {
            currentRoot = new ProjectRootNode(currentAssemblies);
        } else if (currentMode == MODE.SEQRUN && !currentSeqRuns.isEmpty()) {
            currentRoot = new ProjectRootNode(currentSeqRuns);
        }
        if (currentRoot != null) {
            explorerManager.setRootContext(currentRoot);
        }

    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
            if (evt.getSource() instanceof SeqRunI) {
                SeqRunI run = (SeqRunI) evt.getSource();
                run.removePropertyChangeListener(this);
                if (currentSeqRuns.contains(run)) {
                    currentSeqRuns.remove(run);
                }
            } else if (evt.getSource() instanceof AssemblyI) {
                AssemblyI ass = (AssemblyI) evt.getSource();
                ass.removePropertyChangeListener(this);
                if (currentAssemblies.contains(ass)) {
                    currentAssemblies.remove(ass);
                }
            } else if (evt.getSource() instanceof MGXMasterI) {
                MGXMasterI master = (MGXMasterI) evt.getSource();
                master.removePropertyChangeListener(this);
                if (master == currentMaster) {
                    currentMaster = null;
                }
            }
        }
        updateJobs();
    }
}
