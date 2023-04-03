package de.cebitec.mgx.gui.explorer;

import de.cebitec.gpms.node.ServerNode;
import de.cebitec.gpms.nodefactory.ServerNodeFactory;
import de.cebitec.gpms.rest.GPMSClientI;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.Serial;
import java.net.Authenticator;
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
@TopComponent.Registration(mode = "explorer", openAtStartup = true, position = 1)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.explorer.ProjectExplorerTopComponent")
@TopComponent.OpenActionRegistration(displayName = "#CTL_ProjectExplorerAction",
        preferredID = "ProjectExplorerTopComponent")
public final class ProjectExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final transient ExplorerManager exmngr = new ExplorerManager();
    private final BeanTreeView btv;

    public ProjectExplorerTopComponent() {
        btv = new BeanTreeView();
        btv.setRootVisible(false);
        this.setLayout(new BorderLayout());
        this.add(btv, BorderLayout.CENTER);
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
        setUp();
    }

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

    private void setUp() {
        // set default authenticator to null to avoid NB-integrated password dialog
        Authenticator.setDefault(null);

        ServerNodeFactory serverNodeFactory = new ServerNodeFactory();
        final AbstractNode root = new InvisibleRoot(Children.create(serverNodeFactory, false));
        exmngr.setRootContext(root);

        // select first server node programmatically to enable the lookup-based toolbar action
        Node firstServer = root.getChildren().getNodeAt(0);
        if (firstServer != null) {
            try {
                exmngr.setSelectedNodes(new Node[]{firstServer});
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // needs to be invoked later (or it doesn't work)
                        requestActive();
                    }
                });
            } catch (PropertyVetoException ex) {
                // ignore
            }
        }

        serverNodeFactory.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() instanceof ServerNode) {
                    ServerNode src = (ServerNode) evt.getSource();
                    if (evt.getPropertyName().equals(GPMSClientI.PROP_LOGGEDIN)) {
                        Boolean newState = (Boolean) evt.getNewValue();
                        if (newState) {
                            btv.expandNode(src);
                        } else {
                            btv.collapseNode(src);
                        }
                    }
                }
            }
        });
    }

    private class InvisibleRoot extends AbstractNode {

        public InvisibleRoot(Children children) {
            super(children);
        }

    }
}
