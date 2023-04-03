package de.cebitec.mgx.gui.attributevisualization.ui;

import de.cebitec.mgx.api.groups.AssemblyGroupI;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.attributevisualization.view.GroupExplorerView;
import de.cebitec.mgx.gui.attributevisualization.NodeMapperImpl;
import de.cebitec.mgx.gui.nodes.VgMgrNode;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import java.awt.BorderLayout;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ActionMap;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.actions.DeleteAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
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
public final class VisualizationGroupTopComponent extends TopComponent implements ExplorerManager.Provider {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Node rootNode = new VgMgrNode(VGroupManager.getInstance());
    private final transient ExplorerManager exmngr = new ExplorerManager();
    //

    public VisualizationGroupTopComponent() {
        initComponents();
        addGroupButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                VGroupManager.getInstance().createVisualizationGroup();
            }
        });

        addReplGroupButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                VGroupManager.getInstance().createReplicateGroup();
            }
        });

        addAsmGroup.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                VGroupManager.getInstance().createAssemblyGroup();
            }
        });

        //groupmgr.addPropertyChangeListener(this);
        setName(Bundle.CTL_VisualizationGroupTopComponent());
        setToolTipText(Bundle.HINT_VisualizationGroupTopComponent());

        GroupExplorerView view = new GroupExplorerView<>(new NodeMapperImpl());
        panel.add(view, BorderLayout.CENTER);
        //getActionMap().put("delete", ExplorerUtils.actionDelete(exmngr, true));
        // combined lookup
        //associateLookup(ExplorerUtils.createLookup(exmngr, getActionMap()));
        // init actions
        ActionMap map = getActionMap();
        map.put(SystemAction.get(DeleteAction.class).getActionMapKey(), SystemAction.get(DeleteAction.class));
        //
        associateLookup(new ProxyLookup(view.getLookup(), Lookups.singleton(map)));
        //
        exmngr.setRootContext(rootNode);
        try {
            exmngr.setSelectedNodes(new Node[]{rootNode});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }

        // create initial group, if necessary
        if (VGroupManager.getInstance().getAllGroups().isEmpty()) {
            VGroupManager.getInstance().createVisualizationGroup();
        }

        // set the panel as a drop target for one or multiple sequencing runs
        setDropTarget();
    }

    private void setDropTarget() {
        DropTarget dt = new DropTarget(panel, new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {

                Set<String> allGroupNames = new HashSet<>();
                for (GroupI<?> vg : VGroupManager.getInstance().getAllGroups()) {
                    allGroupNames.add(vg.getName());
                }

                Node[] nodes = NodeTransfer.nodes(dtde.getTransferable(), DnDConstants.ACTION_COPY);
                if (nodes == null || nodes.length == 0) {
                    return;
                }
                for (Node n : nodes) {
                    SeqRunI run = n.getLookup().lookup(SeqRunI.class);
                    if (run != null) {
                        if (allGroupNames.contains(run.getName())) {
                            dtde.rejectDrag();
                            return;
                        }
                    }
                    AssembledSeqRunI arun = n.getLookup().lookup(AssembledSeqRunI.class);
                    if (arun != null) {
                        if (allGroupNames.contains(arun.getName())) {
                            dtde.rejectDrag();
                            return;
                        }
                    }
                }
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {

                Set<String> allGroupNames = new HashSet<>();
                for (GroupI<?> vg : VGroupManager.getInstance().getAllGroups()) {
                    allGroupNames.add(vg.getName());
                }

                Node[] nodes = NodeTransfer.nodes(dtde.getTransferable(), DnDConstants.ACTION_COPY);
                for (Node n : nodes) {
                    SeqRunI run = n.getLookup().lookup(SeqRunI.class);
                    if (run != null) {
                        if (allGroupNames.contains(run.getName())) {
                            dtde.rejectDrop();
                            return;
                        }
                    }
                    AssembledSeqRunI arun = n.getLookup().lookup(AssembledSeqRunI.class);
                    if (arun != null) {
                        if (allGroupNames.contains(arun.getName())) {
                            dtde.rejectDrop();
                            return;
                        }
                    }
                }

                for (Node n : nodes) {
                    SeqRunI run = n.getLookup().lookup(SeqRunI.class);
                    if (run != null) {
                        VisualizationGroupI newGrp = VGroupManager.getInstance().createVisualizationGroup();
                        newGrp.setName(run.getName());
                        newGrp.add(run);
                        continue;
                    }
                    AssembledSeqRunI arun = n.getLookup().lookup(AssembledSeqRunI.class);
                    if (arun != null) {
                        AssemblyGroupI newGrp = VGroupManager.getInstance().createAssemblyGroup();
                        newGrp.setName(arun.getName());
                        newGrp.add(arun);
                    }
                }
                dtde.dropComplete(true);
            }
        });
        panel.setDropTarget(dt);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        addGroupButton = new javax.swing.JButton();
        addReplGroupButton = new javax.swing.JButton();
        addAsmGroup = new javax.swing.JButton();
        panel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(400, 200));
        setPreferredSize(new java.awt.Dimension(500, 200));
        setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(200, 29));
        jToolBar1.setPreferredSize(new java.awt.Dimension(200, 29));

        addGroupButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addGroupButton, org.openide.util.NbBundle.getMessage(VisualizationGroupTopComponent.class, "VisualizationGroupTopComponent.addGroupButton.text")); // NOI18N
        addGroupButton.setToolTipText(org.openide.util.NbBundle.getMessage(VisualizationGroupTopComponent.class, "VisualizationGroupTopComponent.addGroupButton.toolTipText")); // NOI18N
        addGroupButton.setFocusable(false);
        addGroupButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addGroupButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(addGroupButton);

        addReplGroupButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addReplGroupButton, org.openide.util.NbBundle.getMessage(VisualizationGroupTopComponent.class, "VisualizationGroupTopComponent.addReplGroupButton.text")); // NOI18N
        addReplGroupButton.setFocusable(false);
        addReplGroupButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addReplGroupButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(addReplGroupButton);

        addAsmGroup.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addAsmGroup, org.openide.util.NbBundle.getMessage(VisualizationGroupTopComponent.class, "VisualizationGroupTopComponent.addAsmGroup.text")); // NOI18N
        addAsmGroup.setFocusable(false);
        addAsmGroup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addAsmGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(addAsmGroup);

        add(jToolBar1, java.awt.BorderLayout.NORTH);

        panel.setMinimumSize(new java.awt.Dimension(200, 100));
        panel.setLayout(new java.awt.BorderLayout());
        add(panel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAsmGroup;
    private javax.swing.JButton addGroupButton;
    private javax.swing.JButton addReplGroupButton;
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
        Collection<GroupI> groups = VGroupManager.getInstance().getAllGroups();
        p.setProperty("numGroups", String.valueOf(groups.size()));
        int num = 0;
        for (GroupI<?> vg : groups) {
            p.setProperty("vGroup" + num + "_active", String.valueOf(vg.isActive()));
        }
    }

    void readProperties(java.util.Properties p) {
        //String version = p.getProperty("version");
        //System.err.println(p.getProperty("numGroups"));
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return exmngr;
    }

}
