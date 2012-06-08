/*
 * ControlPanel.java
 *
 * Created on 06.01.2012, 14:12:52
 */
package de.cebitec.mgx.gui.attributevisualization.ui;

import de.cebitec.mgx.gui.attributevisualization.BaseModel;
import de.cebitec.mgx.gui.attributevisualization.VGroupManager;
import de.cebitec.mgx.gui.attributevisualization.conflictwizard.ConflictResolverWizardIterator;
import de.cebitec.mgx.gui.attributevisualization.filter.SortOrder;
import de.cebitec.mgx.gui.attributevisualization.filter.ToFractionFilter;
import de.cebitec.mgx.gui.attributevisualization.filter.VisFilterI;
import de.cebitec.mgx.gui.attributevisualization.filter.VisFilterSupport;
import de.cebitec.mgx.gui.attributevisualization.util.ResultCollector;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
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

    private VGroupManager vgmgr = VGroupManager.getInstance();
    private AttributeVisualizationTopComponent topComponent;
    //
    private List<Pair<VisualizationGroup, Distribution>> currentDistributions = new ArrayList<>();
    private List<Pair<VisualizationGroup, Tree<Long>>> currentHierarchies = new ArrayList<>();
    //
    private AttributeTypeListModel attrListModel = new AttributeTypeListModel();
    private VisualizationTypeListModel vizListModel = new VisualizationTypeListModel();
    //
    private AttributeType currentAttributeType;
    private ViewerI currentViewer;

    /**
     * Creates new form ControlPanel
     */
    public ControlPanel() {
        initComponents();
        attributeTypeList.addItemListener(attrListModel);
        visualizationTypeList.addItemListener(vizListModel);
        updateButton.addActionListener(this);

        vgmgr.addPropertyChangeListener(this);
        vgmgr.registerResolver(new VGroupManager.ConflictResolver() {

            @Override
            public boolean resolve(List<VisualizationGroup> groups) {
                ConflictResolverWizardIterator iter = new ConflictResolverWizardIterator(groups);
                //iter.setGroups(groups);
                WizardDescriptor wiz = new WizardDescriptor(iter);
                //             // {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
                //             // {1} will be replaced by WizardDescriptor.Iterator.name()
                wiz.setTitleFormat(new MessageFormat("{0}"));
                wiz.setTitle("Tool selection");
                if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                    for (Pair<VisualizationGroup, Pair<SeqRun, Job>> p : iter.getSelection()) {
                        VisualizationGroup vg = p.getFirst();
                        vg.resolveConflict(p.getSecond().getFirst(), p.getSecond().getSecond());
                        groups.remove(vg);
                    }
                    return true;
                } else {
                    // FIXME - cancel?
                    return false;
                }
            }
        });
    }

    public final void setTopComponent(AttributeVisualizationTopComponent tc) {
        this.topComponent = tc;
    }

    public final void updateAttributeTypeList() {
        attrListModel.update();
    }

    public final void updateViewerList() {
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

        sortOrderGroup = new javax.swing.ButtonGroup();
        attributeTypeList = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        visualizationTypeList = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        fractions = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        sortAscending = new javax.swing.JRadioButton();
        sortDescending = new javax.swing.JRadioButton();

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

        fractions.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        fractions.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.fractions.text")); // NOI18N

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.jLabel3.text")); // NOI18N

        sortOrderGroup.add(sortAscending);
        sortAscending.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sortAscending.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.sortAscending.text")); // NOI18N

        sortOrderGroup.add(sortDescending);
        sortDescending.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sortDescending.setSelected(true);
        sortDescending.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.sortDescending.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(attributeTypeList, 0, 276, Short.MAX_VALUE)
                    .addComponent(visualizationTypeList, 0, 276, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(updateButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(sortAscending)
                                .addGap(18, 18, 18)
                                .addComponent(sortDescending))
                            .addComponent(fractions))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortAscending)
                    .addComponent(sortDescending))
                .addGap(18, 18, 18)
                .addComponent(fractions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 250, Short.MAX_VALUE)
                .addComponent(updateButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox attributeTypeList;
    private javax.swing.JCheckBox fractions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton sortAscending;
    private javax.swing.JRadioButton sortDescending;
    private javax.swing.ButtonGroup sortOrderGroup;
    private javax.swing.JButton updateButton;
    private javax.swing.JComboBox visualizationTypeList;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() != null) {
            System.err.println("control received event " + evt.getPropertyName() + " " + evt.getOldValue() + " " + evt.getNewValue());
        }

        String propName = evt.getPropertyName();

        switch (propName) {
            case VisualizationGroup.VISGROUP_CHANGED:
                updateAttributeTypeList();
                break;
            case VGroupManager.VISGROUP_NUM_CHANGED:
                updateAttributeTypeList();
                break;
            case VisualizationGroup.VISGROUP_ACTIVATED:
                updateAttributeTypeList();
                break;
            case VisualizationGroup.VISGROUP_DEACTIVATED:
                updateAttributeTypeList();
                break;
            default:
                System.err.println("unknown event " + propName);
        }
    }

    private final class AttributeTypeListModel extends BaseModel<AttributeType> implements ItemListener {

        @Override
        public void update() {
            // disable all downstream elements, including self
            content.clear();
            attributeTypeList.setEnabled(false);
            visualizationTypeList.setEnabled(false);
            updateButton.setEnabled(false);

            SwingWorker<SortedSet<AttributeType>, Void> worker = new SwingWorker<SortedSet<AttributeType>, Void>() {

                @Override
                protected SortedSet<AttributeType> doInBackground() throws Exception {
                    SortedSet<AttributeType> types = new TreeSet<>();
                    for (VisualizationGroup vg : vgmgr.getActiveGroups()) {
                        types.addAll(vg.getAttributeTypes());
                    }
                    return types;
                }

                @Override
                protected void done() {
                    SortedSet<AttributeType> types = null;
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
                    fireContentsChanged();
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
            if (currentViewer != null) {
                currentViewer.setAttributeType(currentAttributeType);
            }

            // disable all downstream elements, excluding self
            visualizationTypeList.setEnabled(false);
            updateButton.setEnabled(false);

            if (vgmgr.selectAttributeType(currentAttributeType.getName())) {
                // fetch distribution (and hierarchy) in background
                System.err.println("fetching data for "+currentAttributeType.getName());
                ResultCollector rc = new ResultCollector(currentAttributeType, currentDistributions, currentHierarchies, ControlPanel.this);
                rc.execute();
            } else {
                // unresolved conflicts remain
                //assert false;
            }
        }
    }

    private final class VisualizationTypeListModel extends BaseModel<ViewerI> implements ItemListener {

        @Override
        public void update() {
            // disable all downstream elements
            content.clear();
            visualizationTypeList.setEnabled(false);
            updateButton.setEnabled(false);

            SortedSet<ViewerI> viewers = new TreeSet<>();
            for (ViewerI viewer : Lookup.getDefault().lookupAll(ViewerI.class)) {
                if (viewer.canHandle(currentAttributeType)) {
                    viewers.add(viewer);
                }
            }

            content.addAll(viewers);

            if (vizListModel.getSize() > 0) {
                // if previously selected attribute type still exists, restore selection
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
            fireContentsChanged();
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            currentViewer = vizListModel.getSelectedItem();
            currentViewer.setAttributeType(currentAttributeType);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        SwingWorker worker;

        if (currentViewer.getInputType().equals(Distribution.class)) {

            worker = new SwingWorker<Void, Void>() {

                private VisFilterI filterChain;

                @Override
                protected Void doInBackground() throws Exception {

                    filterChain = currentViewer;

                    if (fractions.isSelected()) {
                        VisFilterI fracFilter = new ToFractionFilter();
                        filterChain = VisFilterSupport.append(fracFilter, filterChain);
                    }

                    // sort filter
                    SortOrder sorter = new SortOrder();
                    sorter.setSortCriteria(currentAttributeType.getValueType() == AttributeType.VALUE_NUMERIC
                            ? SortOrder.BY_TYPE : SortOrder.BY_VALUE);

                    currentViewer.sortAscending(sortAscending.isSelected());

                    filterChain = VisFilterSupport.append(sorter, filterChain);

                    return null;
                }

                @Override
                protected void done() {
                    filterChain.filter(currentDistributions);
                    topComponent.setVisualization(currentViewer.getComponent());
                    super.done();
                }
            };

            worker.execute();

        } else {
            // hierarchy
            currentViewer.filter(currentHierarchies);
            topComponent.setVisualization(currentViewer.getComponent());
        }
    }
}
