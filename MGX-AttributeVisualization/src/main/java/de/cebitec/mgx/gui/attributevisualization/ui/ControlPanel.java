/*
 * ControlPanel.java
 *
 * Created on 06.01.2012, 14:12:52
 */
package de.cebitec.mgx.gui.attributevisualization.ui;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.misc.Visualizable;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.api.visualization.ConflictResolver;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.common.visualization.ViewerI;
import de.cebitec.mgx.gui.attributevisualization.conflictwizard.ConflictResolverWizardIterator;
import de.cebitec.mgx.gui.attributevisualization.util.ResultCollector;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author sjaenick
 */
public class ControlPanel extends javax.swing.JPanel implements PropertyChangeListener, ActionListener {

    private final VGroupManagerI vgmgr;
    private AttributeVisualizationTopComponent topComponent;
    //
    private List<Pair<VisualizationGroupI, DistributionI<Long>>> currentDistributions = new ArrayList<>();
    private List<Pair<VisualizationGroupI, TreeI<Long>>> currentHierarchies = new ArrayList<>();
    //
    private AttributeTypeListModel attrListModel = new AttributeTypeListModel();
    private VisualizationTypeListModel vizListModel = new VisualizationTypeListModel();
    //
    private AttributeTypeI currentAttributeType;
    private ViewerI currentViewer;

    /**
     * Creates new form ControlPanel
     */
    public ControlPanel() {
        this.vgmgr = VGroupManager.getInstance();
        initComponents();
        attributeTypeList.addItemListener(attrListModel);
        visualizationTypeList.addItemListener(vizListModel);
        updateButton.addActionListener(this);

        vgmgr.addPropertyChangeListener(this);

        vgmgr.registerResolver(new ConflictResolver() {
            @Override
            public void resolve(List<VisualizationGroupI> groups) {
                ConflictResolverWizardIterator iter = new ConflictResolverWizardIterator(groups);
                WizardDescriptor wiz = new WizardDescriptor(iter);
                wiz.setTitleFormat(new MessageFormat("{0}"));
                wiz.setTitle("Job selection");
                if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                    for (Pair<VisualizationGroupI, Triple<AttributeRank, SeqRunI, JobI>> p : iter.getSelection()) {
                        VisualizationGroupI vg = p.getFirst();
                        vg.resolveConflict(p.getSecond().getFirst(), p.getSecond().getSecond(), p.getSecond().getThird());
                        groups.remove(vg);
                    }
                }
            }
        });
    }

    public final void setTopComponent(AttributeVisualizationTopComponent tc) {
        this.topComponent = tc;
    }

    public final synchronized void updateAttributeTypeList() {
        attrListModel.update();
    }

    public final synchronized void updateViewerList() {
        vizListModel.update();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        attributeTypeList = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        visualizationTypeList = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        customPane = new javax.swing.JScrollPane();

        setMaximumSize(new java.awt.Dimension(300, 32767));
        setPreferredSize(new java.awt.Dimension(300, 504));

        attributeTypeList.setModel(attrListModel);
        attributeTypeList.setActionCommand(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.attributeTypeList.actionCommand")); // NOI18N
        attributeTypeList.setEnabled(false);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel1.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.jLabel1.text")); // NOI18N

        visualizationTypeList.setModel(vizListModel);
        visualizationTypeList.setEnabled(false);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.jLabel2.text")); // NOI18N

        updateButton.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.updateButton.text")); // NOI18N
        updateButton.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customPane)
                    .addComponent(attributeTypeList, 0, 276, Short.MAX_VALUE)
                    .addComponent(visualizationTypeList, 0, 276, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(updateButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 189, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributeTypeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualizationTypeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(customPane, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(updateButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox attributeTypeList;
    private javax.swing.JScrollPane customPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton updateButton;
    private javax.swing.JComboBox visualizationTypeList;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {
            case VisualizationGroupI.VISGROUP_CHANGED:
                updateAttributeTypeList();
                break;
            case VGroupManagerI.VISGROUP_ADDED:
            case ModelBaseI.OBJECT_DELETED:
                updateAttributeTypeList();
                break;
            case VisualizationGroupI.VISGROUP_ACTIVATED:
            case ReplicateGroupI.REPLICATEGROUP_ACTIVATED:
                updateAttributeTypeList();
                break;
            case VisualizationGroupI.VISGROUP_DEACTIVATED:
            case ReplicateGroupI.REPLICATEGROUP_DEACTIVATED:
                updateAttributeTypeList();
                break;
            case VisualizationGroupI.VISGROUP_ATTRTYPE_CHANGED:
                break; // ignore
            case ModelBaseI.OBJECT_MODIFIED:
                break;
            case VGroupManagerI.VISGROUP_SELECTION_CHANGED:
                // ignore
                break;
            case VisualizationGroupI.VISGROUP_HAS_DIST:
                // ignore
                break;
            case VisualizationGroupI.VISGROUP_RENAMED:
                // ignore
                break;
            case VGroupManagerI.REPLICATEGROUP_SELECTION_CHANGED:
            case VGroupManagerI.REPLGROUP_ADDED:
                // ignore
                break;
            case ReplicateGroupI.REPLICATEGROUP_REPLICATE_ADDED:
                break;
            case ReplicateGroupI.REPLICATEGROUP_REPLICATE_REMOVED:
                updateAttributeTypeList();
                break;
            default:
                System.err.println("ControlPanel received unknown event " + evt);
        }
    }

    private final class AttributeTypeListModel extends BaseModel<AttributeTypeI> implements ItemListener {

        @Override
        public void update() {
            // disable all downstream elements, including self
            clear();
            attributeTypeList.setEnabled(false);
            visualizationTypeList.setEnabled(false);
            updateButton.setEnabled(false);

            SwingWorker<SortedSet<AttributeTypeI>, Void> worker = new SwingWorker<SortedSet<AttributeTypeI>, Void>() {
                @Override
                protected SortedSet<AttributeTypeI> doInBackground() throws Exception {
                    SortedSet<AttributeTypeI> types = new TreeSet<>();
                    for (VisualizationGroupI vg : vgmgr.getActiveVisualizationGroups()) {
                        Iterator<AttributeTypeI> atIter = vg.getAttributeTypes();
                        while (atIter.hasNext()) {
                            types.add(atIter.next());
                        }
                    }
                    return types;
                }

                @Override
                protected void done() {
                    SortedSet<AttributeTypeI> types = null;
                    try {
                        types = get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    content.addAll(types);

                    if (attrListModel.getSize() > 0) {
                        // if previously selected attribute type still exists, restore selection
                        if (currentAttributeType != null && content.contains(currentAttributeType)) {
                            setSelectedItem(currentAttributeType);
                            AttributeTypeListModel.this.itemStateChanged(new ItemEvent(attributeTypeList,
                                    ItemEvent.ITEM_STATE_CHANGED,
                                    getSelectedItem(),
                                    ItemEvent.SELECTED));
                        } else {
                            attributeTypeList.setSelectedIndex(0);
                        }

                        attributeTypeList.setEnabled(true);
                    }
                    if (!content.isEmpty()) {
                        fireContentsChanged();
                    }
                    super.done();
                }
            };
            worker.execute();
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            currentAttributeType = getSelectedItem();
            if (currentAttributeType == null) {
                return;
            }

            if (currentViewer != null) {
                currentViewer.setAttributeType(currentAttributeType);
            }

            // disable all downstream elements, excluding self
            visualizationTypeList.setEnabled(false);
            updateButton.setEnabled(false);
            
            System.err.println("Selecting " +currentAttributeType.getName());

            if (vgmgr.selectAttributeType(currentAttributeType.getName())) {
                // fetch distribution (and hierarchy) in background
                ResultCollector rc = new ResultCollector(vgmgr, currentAttributeType, currentDistributions, currentHierarchies, ControlPanel.this);
                rc.execute();
            } else {
                // unresolved conflicts remain
                //assert false;
            }
        }
    }

    private final class VisualizationTypeListModel extends BaseModel<ViewerI<Visualizable>> implements ItemListener {

        @Override
        @SuppressWarnings("unchecked")
        public synchronized void update() {
            // disable all downstream elements
            clear();
            visualizationTypeList.setEnabled(false);
            updateButton.setEnabled(false);

            SortedSet<ViewerI<Visualizable>> viewers = new TreeSet<>();
            for (ViewerI viewer : Lookup.getDefault().<ViewerI>lookupAll(ViewerI.class)) {
                if (viewer.canHandle(currentAttributeType)) {
                    viewers.add(viewer);
                }
            }

            content.addAll(viewers);

            if (vizListModel.getSize() > 0) {
                // if previously selected element still exists, restore selection
                if (currentViewer != null && content.contains(currentViewer)) {
                    setSelectedItem(currentViewer);
                    itemStateChanged(new ItemEvent(visualizationTypeList,
                            ItemEvent.ITEM_STATE_CHANGED,
                            getSelectedItem(),
                            ItemEvent.SELECTED));
                } else {
                    visualizationTypeList.setSelectedIndex(0);
                }

                visualizationTypeList.setEnabled(true);
                updateButton.setEnabled(true);
            }
            if (!content.isEmpty()) {
                fireContentsChanged();
            }
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            if (currentViewer != null) {
                currentViewer.dispose();
            }

            currentViewer = vizListModel.getSelectedItem();
            currentViewer.setAttributeType(currentAttributeType);

            customPane.setViewportView(currentViewer.getCustomizer());
            customPane.getVerticalScrollBar().setUnitIncrement(16);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (currentViewer.getInputType().equals(DistributionI.class)) {
                currentViewer.show(currentDistributions);
            } else {
                currentViewer.show(currentHierarchies);
            }
            topComponent.updateLookup(currentDistributions);
            topComponent.setVisualization(currentViewer);
        } finally {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public final void dispose() {
        if (currentViewer != null) {
            currentViewer.dispose();
        }
        vgmgr.removePropertyChangeListener(this);
    }
}
