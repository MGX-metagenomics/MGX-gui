package de.cebitec.mgx.gui.jobmonitor;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
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
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "navigator", openAtStartup = false)
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
public final class JobMonitorTopComponent extends TopComponent implements LookupListener, ActionListener, ExplorerManager.Provider {

    private Lookup.Result<MGXMaster> result;
    private MGXMaster currentMaster = null;
    private Timer t = new Timer(1000 * 10, this);
    private transient ExplorerManager explorerManager = new ExplorerManager();

    public JobMonitorTopComponent() {
        initComponents();
        setName(Bundle.CTL_JobMonitorTopComponent());
        setToolTipText(Bundle.HINT_JobMonitorTopComponent());
        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));
        explorerManager.setRootContext(new ProjectRootNode("No project selected", Children.LEAF));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btv = new org.openide.explorer.view.BeanTreeView();

        setLayout(new java.awt.BorderLayout());
        add(btv, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView btv;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(MGXMaster.class);
        result.addLookupListener(this);
        t.start();
    }

    @Override
    public void componentClosed() {
        t.stop();
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
        getMaster();
    }

    private void getMaster() {
        Collection<? extends MGXMaster> m = result.allInstances();
        boolean needUpdate = false;
        for (MGXMaster newMaster : m) {
            if (currentMaster == null || !newMaster.equals(currentMaster)) {
                currentMaster = newMaster;
                needUpdate = true;
            }
        }
        if (needUpdate) {
            updateJobs();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateJobs();
    }

    private void updateJobs() {
        if (currentMaster == null) {
            return;
        }
        SwingWorker<Map<SeqRun, List<Job>>, Void> worker = new SwingWorker<Map<SeqRun, List<Job>>, Void>() {
            @Override
            protected Map<SeqRun, List<Job>> doInBackground() throws Exception {
                Map<SeqRun, List<Job>> ret = new HashMap<>();
                for (SeqRun sr : currentMaster.SeqRun().fetchall()) {
                    List<Job> jobs = currentMaster.Job().BySeqRun(sr.getId());
                    for (Job j : jobs) {
                        j.setTool(currentMaster.Tool().ByJob(j.getId()));
                    }
                    ret.put(sr, jobs);
                }
                return ret;
            }

            @Override
            protected void done() {
                try {
                    Map<SeqRun, List<Job>> get = get();
                    explorerManager.setRootContext(new ProjectRootNode(currentMaster.getProject(), new SeqRunChildren(currentMaster, get)));
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                super.done();
            }
        };
        worker.execute();


    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}
