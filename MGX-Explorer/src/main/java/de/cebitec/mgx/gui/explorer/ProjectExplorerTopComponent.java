package de.cebitec.mgx.gui.explorer;

import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.gui.nodefactory.ServerNodeFactory;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.cebitec.mgx.gui.explorer//ProjectExplorer//EN",
autostore = false)
@TopComponent.Description(preferredID = "ProjectExplorerTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.explorer.ProjectExplorerTopComponent")
@TopComponent.OpenActionRegistration(displayName = "#CTL_ProjectExplorerAction",
preferredID = "ProjectExplorerTopComponent")
public final class ProjectExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private GPMSClientI gpms = null;
    private ExplorerManager exmngr = new ExplorerManager();
    private JScrollPane jscr;

    public ProjectExplorerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(ProjectExplorerTopComponent.class, "CTL_ProjectExplorerTopComponent"));
        setToolTipText(NbBundle.getMessage(ProjectExplorerTopComponent.class, "HINT_ProjectExplorerTopComponent"));
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);
        associateLookup(ExplorerUtils.createLookup(exmngr, getActionMap()));
    }

    public void setGPMSInstance(GPMSClientI gpms) {
        this.gpms = gpms;
        initTree();
    }

    private void initComponents() {
        jscr = new BeanTreeView();
        setLayout(new BorderLayout());
        add(jscr, BorderLayout.CENTER);
    }

    private void initTree() {
        AbstractNode root = new AbstractNode(Children.create(new ServerNodeFactory(gpms), true));
        exmngr.setRootContext(root);
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
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return exmngr;
    }
}
