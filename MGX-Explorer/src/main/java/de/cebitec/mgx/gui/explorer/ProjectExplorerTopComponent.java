package de.cebitec.mgx.gui.explorer;

import de.cebitec.mgx.gui.nodefactory.ServerNodeFactory;
import de.cebitec.mgx.gui.nodes.ServerNode;
import de.cebitec.mgx.restgpms.GPMS;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.SwingWorker;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.cebitec.mgx.gui.explorer//ProjectExplorer//EN",
        autostore = false)
@TopComponent.Description(preferredID = "ProjectExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "explorer", openAtStartup = false, position = 1)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.explorer.ProjectExplorerTopComponent")
@TopComponent.OpenActionRegistration(displayName = "#CTL_ProjectExplorerAction",
        preferredID = "ProjectExplorerTopComponent")
public final class ProjectExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager exmngr = new ExplorerManager();
    private final BeanTreeView btv;

    public ProjectExplorerTopComponent() {
        btv = new BeanTreeView();
        btv.setRootVisible(false);
        setLayout(new BorderLayout());
        add(btv, BorderLayout.CENTER);
//        initComponents();
        setName(NbBundle.getMessage(ProjectExplorerTopComponent.class, "CTL_ProjectExplorerTopComponent"));
        setToolTipText(NbBundle.getMessage(ProjectExplorerTopComponent.class, "HINT_ProjectExplorerTopComponent"));
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);
        associateLookup(ExplorerUtils.createLookup(exmngr, getActionMap()));
        getActionMap().put("delete", ExplorerUtils.actionDelete(exmngr, true));
    }

//    private void initComponents() {
//        btv = new BeanTreeView();
//        btv.setRootVisible(false);
//        setLayout(new BorderLayout());
//        add(btv, BorderLayout.CENTER);
//    }
    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    protected void componentActivated() {
        ExplorerUtils.activateActions(exmngr, true);
    }

    @Override
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(exmngr, false);
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        //String version = p.getProperty("version");
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return exmngr;
    }

    public void setGPMS(final GPMS gpms) {
        final AbstractNode root = new AbstractNode(Children.create(new ServerNodeFactory(gpms), false));
        exmngr.setRootContext(root);
        SwingWorker<Void, Node> sw = new SwingWorker<Void, Node>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (Node server : root.getChildren().getNodes()) {
                    publish(server);
                }
                return null;
            }

            @Override
            protected void process(List<Node> chunks) {
                for (Node n : chunks) {
                    if (n instanceof ServerNode) {
                        btv.expandNode(n);
                    }
                }
            }
        };
        sw.execute();
    }
}
