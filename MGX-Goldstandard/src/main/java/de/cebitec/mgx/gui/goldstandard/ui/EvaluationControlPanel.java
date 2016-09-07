package de.cebitec.mgx.gui.goldstandard.ui;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
import de.cebitec.mgx.gui.goldstandard.ui.charts.ComparisonTypeI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison.GSComparisonI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison.PipelineComparisonI;
import de.cebitec.mgx.gui.goldstandard.util.EvalExceptions;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author plumenk
 */
public class EvaluationControlPanel extends javax.swing.JPanel implements ActionListener, LookupListener {

    private EvaluationTopComponent topComponent;
    //
    private ComparisonTypeI currentComparisonType;
    private EvaluationViewerI currentViewer;
    //
    private final ComparisonTypeListModel compListModel = new ComparisonTypeListModel();
    private final VisualizationTypeListModel visListModel = new VisualizationTypeListModel();
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
        comparisonTypeList.addActionListener(this);
        visualizationTypeList.addActionListener(this);
    }

    public final void setTopComponent(EvaluationTopComponent tc) {
        this.topComponent = tc;
    }

    public final synchronized void updateViewerList() {
        compListModel.update();
//        controlSplitPane.setBottomComponent(currentViewer.getCustomizer());
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
        comparisonTypeList = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        visualizationTypeList = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        updateButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(150, 32767));
        setMinimumSize(new java.awt.Dimension(100, 0));
        setPreferredSize(new java.awt.Dimension(150, 504));

        controlSplitPane.setDividerLocation(120);
        controlSplitPane.setDividerSize(1);
        controlSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        controlSplitPane.setPreferredSize(new java.awt.Dimension(200, 450));

        comparisonTypeList.setModel(compListModel);
        comparisonTypeList.setEnabled(false);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setText("Comparison type");

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel1.setText("Visualization type");

        visualizationTypeList.setModel(visListModel);
        visualizationTypeList.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comparisonTypeList, 0, 135, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(0, 22, Short.MAX_VALUE))
                    .addComponent(visualizationTypeList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comparisonTypeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualizationTypeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        controlSplitPane.setTopComponent(jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 159, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 328, Short.MAX_VALUE)
        );

        controlSplitPane.setRightComponent(jPanel2);

        updateButton.setText("Start");
        updateButton.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(controlSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JComboBox comparisonTypeList;
    private javax.swing.JSplitPane controlSplitPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton updateButton;
    private javax.swing.JComboBox visualizationTypeList;
    // End of variables declaration//GEN-END:variables

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == comparisonTypeList) {
            currentComparisonType = compListModel.getSelectedItem();
            visListModel.update();
        } else if (e.getSource() == visualizationTypeList) {
            if (currentViewer != null) {
                currentViewer.dispose();
            }
            currentViewer = visListModel.getSelectedItem();
            controlSplitPane.setBottomComponent(currentViewer.getCustomizer());
            topComponent.setVisualization(null);
        } else {
            topComponent.setVisualization(null);
            currentViewer.selectJobs(currentSeqrun);
            EvaluationTopComponent.getExecutorService().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        topComponent.setVisualization(currentViewer);
                    } catch (Exception ex) {
                        EvalExceptions.printStackTrace(ex);                        
                    }
                }
            });
        }
    }

    public final void dispose() {
        if (currentViewer != null) {
            currentViewer.dispose();
        }
        res.removeLookupListener(this);
        updateButton.removeActionListener(this);
        comparisonTypeList.removeActionListener(this);
        visualizationTypeList.removeActionListener(this);
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
            updateButton.setEnabled(false);
        }
    }

    private final class ComparisonTypeListModel extends BaseModel<ComparisonTypeI> implements ItemListener {

        @Override
        @SuppressWarnings("unchecked")
        public synchronized void update() {
            // disable all downstream elements
            content.clear();
            comparisonTypeList.setEnabled(false);
            updateButton.setEnabled(false);

            SortedSet<ComparisonTypeI> viewers = new TreeSet<>();
            for (ComparisonTypeI viewer : Lookup.getDefault().<ComparisonTypeI>lookupAll(ComparisonTypeI.class)) {
                viewers.add(viewer);
            }

            content.addAll(viewers);

            if (compListModel.getSize() > 0) {
                // if previously selected element still exists, restore selection
                if (currentComparisonType != null && content.contains(currentComparisonType)) {
                    setSelectedItem(currentComparisonType);
                    itemStateChanged(new ItemEvent(comparisonTypeList,
                            ItemEvent.ITEM_STATE_CHANGED,
                            getSelectedItem(),
                            ItemEvent.SELECTED));
                } else {
                    setSelectedItem(content.get(0));
                }

                comparisonTypeList.setEnabled(true);
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

            currentComparisonType = compListModel.getSelectedItem();

            controlSplitPane.setBottomComponent(null);
        }
    }

    private final class VisualizationTypeListModel extends BaseModel<EvaluationViewerI> implements ItemListener {

        @Override
        @SuppressWarnings("unchecked")
        public synchronized void update() {
            // disable all downstream elements
            content.clear();
            visualizationTypeList.setEnabled(false);
            updateButton.setEnabled(false);

            Class chartInterface = currentComparisonType.getChartInterface();

            SortedSet<EvaluationViewerI> viewers = new TreeSet<>();
            if (chartInterface == GSComparisonI.class) {
                for (GSComparisonI viewer : Lookup.getDefault().<GSComparisonI>lookupAll(GSComparisonI.class)) {
                    if (viewer instanceof EvaluationViewerI) {
                        viewers.add((EvaluationViewerI) viewer);
                    }
                }
            } else if (chartInterface == PipelineComparisonI.class) {
                for (PipelineComparisonI viewer : Lookup.getDefault().<PipelineComparisonI>lookupAll(PipelineComparisonI.class)) {
                    if (viewer instanceof EvaluationViewerI) {
                        viewers.add((EvaluationViewerI) viewer);
                    }
                }
            }

            content.addAll(viewers);

            if (visListModel.getSize() > 0) {
                // if previously selected element still exists, restore selection
                if (currentViewer != null && content.contains(currentViewer)) {
                    setSelectedItem(currentViewer);
                    itemStateChanged(new ItemEvent(visualizationTypeList,
                            ItemEvent.ITEM_STATE_CHANGED,
                            getSelectedItem(),
                            ItemEvent.SELECTED));
                } else {
                    setSelectedItem(content.get(0));
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

            currentViewer = visListModel.getSelectedItem();

            controlSplitPane.setBottomComponent(currentViewer.getCustomizer());
        }
    }
}
