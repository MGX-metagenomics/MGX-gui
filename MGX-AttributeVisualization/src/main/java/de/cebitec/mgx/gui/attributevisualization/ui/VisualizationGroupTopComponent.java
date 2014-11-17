package de.cebitec.mgx.gui.attributevisualization.ui;

import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.gui.attributevisualization.GroupFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.cebitec.mgx.gui.attributevisualization.ui//VisualizationGroup//EN",
        autostore = false)
@TopComponent.Description(preferredID = "VisualizationGroupTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "output", openAtStartup = false, position = 1)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.attributevisualization.ui.VisualizationGroupTopComponent")
@ActionReference(path = "Menu/Window", position = 335)
@TopComponent.OpenActionRegistration(displayName = "#CTL_VisualizationGroupAction",
        preferredID = "VisualizationGroupTopComponent")
@Messages({
    "CTL_VisualizationGroupAction=VisualizationGroup",
    "CTL_VisualizationGroupTopComponent=Visualization groups",
    "HINT_VisualizationGroupTopComponent=Group window"
})
@ServiceProvider(service = VisualizationGroupTopComponent.class)
public final class VisualizationGroupTopComponent extends TopComponent implements ActionListener, PropertyChangeListener { // , ExplorerManager.Provider {

    private final VGroupManagerI groupmgr = VGroupManager.getInstance();
    private final ExplorerManager exmngr = new ExplorerManager();
    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup;

    public VisualizationGroupTopComponent() {
        initComponents();
        addGroupButton.addActionListener(this);

        groupmgr.addPropertyChangeListener(this);

        setName(Bundle.CTL_VisualizationGroupTopComponent());
        setToolTipText(Bundle.HINT_VisualizationGroupTopComponent());
        //associateLookup(ExplorerUtils.createLookup(exmngr, getActionMap()));
        lookup = new AbstractLookup(content);
        associateLookup(lookup);

        // create initial group, if necessary
        if (groupmgr.getAllGroups().isEmpty()) {
            actionPerformed(null);
        }
    }

    public Collection<VisualizationGroupI> getVisualizationGroups() {
        return groupmgr.getActiveGroups();
    }

    void removeGroup(VisualizationGroupI group) {
        groupmgr.removeGroup(group);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        addGroupButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        panel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

        addGroupButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addGroupButton, org.openide.util.NbBundle.getMessage(VisualizationGroupTopComponent.class, "VisualizationGroupTopComponent.addGroupButton.text")); // NOI18N
        addGroupButton.setFocusable(false);
        addGroupButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addGroupButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(addGroupButton);

        add(jToolBar1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jScrollPane1.setViewportView(panel);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addGroupButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

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
        Collection<VisualizationGroupI> groups = groupmgr.getAllGroups();
        p.setProperty("numGroups", String.valueOf(groups.size()));
        int num = 0;
        for (VisualizationGroupI vg : groups) {
            p.setProperty("vGroup"+num+"_active", String.valueOf(vg.isActive()));
        }
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        System.err.println(p.getProperty("numGroups"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final VisualizationGroupI newGroup = groupmgr.createGroup();
        final GroupFrame gf = new GroupFrame(newGroup, groupmgr);
        panel.add(gf);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case VGroupManagerI.VISGROUP_SELECTION_CHANGED:
                content.set(Collections.emptyList(), null); // clear content
                content.add(evt.getNewValue());
                break;
            case VisualizationGroupI.VISGROUP_ATTRTYPE_CHANGED:
                // ignore
                break;
            case VisualizationGroupI.VISGROUP_CHANGED:
                // ignore
                break;
            case VisualizationGroupI.VISGROUP_HAS_DIST:
                // ignore
                break;
            case VisualizationGroupI.VISGROUP_DEACTIVATED:
                // ignore
                break;
            case VisualizationGroupI.VISGROUP_ACTIVATED:
                // ignore
                break;
            default:
                System.err.println("VGTopComponent got unhandled event " + evt.getPropertyName());
        }
    }
}
