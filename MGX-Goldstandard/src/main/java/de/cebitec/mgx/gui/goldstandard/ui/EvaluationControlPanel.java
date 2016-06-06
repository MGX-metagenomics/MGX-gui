package de.cebitec.mgx.gui.goldstandard.ui;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.Visualizable;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWizardAction;
import de.cebitec.mgx.gui.goldstandard.wizards.selectjobs.SelectSingleJobWizardDescriptor;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author plumenk
 */
public class EvaluationControlPanel extends javax.swing.JPanel implements PropertyChangeListener, ActionListener, LookupListener {

    private EvaluationTopComponent topComponent;
    //
    private EvaluationViewerI currentViewer;
    private final SelectSingleJobWizardAction jobWizard = new SelectSingleJobWizardAction();
    private SelectSingleJobWizardDescriptor jobWz;
    //
    private final VisualizationTypeListModel vizListModel = new VisualizationTypeListModel();
    //
    private final Lookup lkp;
    private final Lookup.Result<SeqRunI> res;
    //
    private SeqRunI currentSeqrun;
    private List<JobI> currentJobs;

    //
    /**
     * Creates new form ControlPanel
     */
    public EvaluationControlPanel() {
        initComponents();
        lkp = Utilities.actionsGlobalContext();
        res = lkp.lookupResult(SeqRunI.class);
        res.addLookupListener(this);
        updateButton.addActionListener(this);
    }

    public final void setTopComponent(EvaluationTopComponent tc) {
        this.topComponent = tc;
    }

    public final synchronized void updateViewerList() {
        vizListModel.update();
        currentViewer = vizListModel.getSelectedItem();
        controlSplitPane.setBottomComponent(currentViewer.getCustomizer());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlSplitPane = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        visualizationTypeList = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        updateButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(300, 32767));
        setMinimumSize(new java.awt.Dimension(100, 0));
        setPreferredSize(new java.awt.Dimension(200, 504));

        controlSplitPane.setDividerLocation(80);
        controlSplitPane.setDividerSize(1);
        controlSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        controlSplitPane.setPreferredSize(new java.awt.Dimension(200, 450));

        visualizationTypeList.setModel(vizListModel);
        visualizationTypeList.setEnabled(false);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setText("Visualization type:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visualizationTypeList, 0, 158, Short.MAX_VALUE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualizationTypeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        controlSplitPane.setTopComponent(jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 182, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 369, Short.MAX_VALUE)
        );

        controlSplitPane.setRightComponent(jPanel2);

        updateButton.setText("Update");
        updateButton.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(controlSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(controlSplitPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(updateButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane controlSplitPane;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton updateButton;
    private javax.swing.JComboBox visualizationTypeList;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

//        switch (evt.getPropertyName()) {
//            default:
//                System.err.println("ControlPanel received unknown event " + evt);
//        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        currentViewer.init(currentSeqrun);
        topComponent.setVisualization(currentViewer);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    

    public final void dispose() {
        if (currentViewer != null) {
            currentViewer.dispose();
        }
        res.removeLookupListener(this);
        updateButton.removeActionListener(this);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends SeqRunI> seqruns = res.allInstances();
        if (seqruns == null || seqruns.isEmpty()) {
            return;
        }
        currentSeqrun = seqruns.iterator().next();
        try {
            currentJobs = currentSeqrun.getMaster().Job().BySeqRun(currentSeqrun);
            for (JobI job : currentJobs) {
                ToolI tool = currentSeqrun.getMaster().Tool().ByJob(job);
                if (tool.getName().equals(AddGoldstandard.TOOL_NAME)) {
                    updateButton.setEnabled(true);
                    return;
                }
            }
            updateButton.setEnabled(false);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private final class VisualizationTypeListModel extends BaseModel<EvaluationViewerI<Visualizable>> implements ItemListener {

        @Override
        @SuppressWarnings("unchecked")
        public synchronized void update() {
            // disable all downstream elements
            content.clear();
            visualizationTypeList.setEnabled(false);
            updateButton.setEnabled(false);

            SortedSet<EvaluationViewerI<Visualizable>> viewers = new TreeSet<>();
            for (EvaluationViewerI viewer : Lookup.getDefault().<EvaluationViewerI>lookupAll(EvaluationViewerI.class)) {
                viewers.add(viewer);
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
            fireContentsChanged();
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

            controlSplitPane.setBottomComponent(currentViewer.getCustomizer());
        }
    }
}
