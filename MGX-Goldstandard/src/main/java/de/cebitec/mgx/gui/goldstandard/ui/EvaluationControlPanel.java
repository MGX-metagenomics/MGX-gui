package de.cebitec.mgx.gui.goldstandard.ui;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
import de.cebitec.mgx.gui.goldstandard.ui.charts.ComparisonTypeI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.EvaluationViewerI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison.GSComparison;
import de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison.GSComparisonI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison.PipelineComparison;
import de.cebitec.mgx.gui.goldstandard.ui.charts.pipelinecomparison.PipelineComparisonI;
import de.cebitec.mgx.gui.goldstandard.util.EvalExceptions;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.gui.swingutils.BaseModel;
import java.awt.Cursor;
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
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author plumenk
 */
public class EvaluationControlPanel extends javax.swing.JPanel implements ActionListener, LookupListener, PropertyChangeListener {

    private final EvaluationTopComponent topComponent;
    //
    private ComparisonTypeI currentComparisonType;
    private EvaluationViewerI currentViewer;
    //
    private final ComparisonTypeListModel compListModel = new ComparisonTypeListModel();
    private final VisualizationTypeListModel visListModel = new VisualizationTypeListModel();
    //
    private final Lookup.Result<SeqRunI> res;
    //
    private SeqRunI currentSeqrun = null;
    private boolean haveGoldStandard = false;
    private int numFinishedJobs = 0;

    //
    /**
     * Creates new form ControlPanel
     */
    public EvaluationControlPanel(EvaluationTopComponent etc) {
        initComponents();
        topComponent = etc;
        res = Utilities.actionsGlobalContext().lookupResult(SeqRunI.class);
        res.addLookupListener(this);
        updateButton.addActionListener(this);

//        comparisonTypeList.addActionListener(this);
        comparisonTypeList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED && comparisonTypeList.isEnabled()) {
                    currentComparisonType = compListModel.getSelectedItem();
                    visListModel.update();
                }
            }
        });
        compListModel.update(); // initial fill 

//        visualizationTypeList.addActionListener(this);
        visualizationTypeList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED && visualizationTypeList.isEnabled()) {
                    if (currentViewer != null) {
                        currentViewer.dispose();
                    }
                    currentViewer = visListModel.getSelectedItem();
                    if (currentViewer != null) {
                        controlSplitPane.setBottomComponent(currentViewer.getCustomizer());
                    }
                    topComponent.setVisualization(null);
                }
            }
        });

        update();
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

        setMaximumSize(new java.awt.Dimension(100, 32767));
        setMinimumSize(new java.awt.Dimension(100, 0));
        setPreferredSize(new java.awt.Dimension(100, 504));

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
//        if (e.getSource() == comparisonTypeList) {
//            currentComparisonType = compListModel.getSelectedItem();
//            visListModel.update();
//        } else if (e.getSource() == visualizationTypeList) {
//            if (currentViewer != null) {
//                currentViewer.dispose();
//            }
//            currentViewer = visListModel.getSelectedItem();
//            if (currentViewer != null) {
//                controlSplitPane.setBottomComponent(currentViewer.getCustomizer());
//            }
//            topComponent.setVisualization(null);
//        } else if (e.getSource() == updateButton) {
        //disable upstream and self
        comparisonTypeList.setEnabled(false);
        visualizationTypeList.setEnabled(false);
        updateButton.setEnabled(false);

        // busy
        topComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        topComponent.setVisualization(null);
        assert currentSeqrun != null;
        currentViewer.selectJobs(currentSeqrun);
        MGXPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    topComponent.setVisualization(currentViewer);
                } catch (Exception ex) {
                    EvalExceptions.printStackTrace(ex);
                } finally {
                    comparisonTypeList.setEnabled(true);
                    visualizationTypeList.setEnabled(true);
                    updateButton.setEnabled(true);
                    topComponent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
//        } else {
//            assert false;
//        }
    }

//    public final void dispose() {
//        if (currentViewer != null) {
//            currentViewer.dispose();
//        }
//        res.removeLookupListener(this);
//        updateButton.removeActionListener(this);
//        comparisonTypeList.removeActionListener(this);
//        visualizationTypeList.removeActionListener(this);
//    }
    private synchronized void update() {
        Collection<? extends SeqRunI> seqruns = res.allInstances();

        // we're only interested in single-selection
        if (seqruns == null || seqruns.size() != 1) {
            return;
        }

        SeqRunI newRun = seqruns.iterator().next();
        if (newRun != currentSeqrun) {
            if (currentSeqrun != null) {
                currentSeqrun.removePropertyChangeListener(this);
            }
            currentSeqrun = newRun;
            currentSeqrun.addPropertyChangeListener(this);
        }

        updateButton.setEnabled(false);
        haveGoldStandard = false;

        numFinishedJobs = 0;
        try {
            List<JobI> currentJobs = currentSeqrun.getMaster().Job().BySeqRun(currentSeqrun);
            for (JobI job : currentJobs) {
                // we only need tool info for finished jobs
                if (job.getStatus().equals(JobState.FINISHED)) {
                    // fetch tool
                    currentSeqrun.getMaster().Tool().ByJob(job);
                    if (job.getTool().getName().equals(AddGoldstandard.TOOL_NAME)) {
                        haveGoldStandard = true;
                    }
                    numFinishedJobs++;
                }
            }

            compListModel.update();
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (visualizationTypeList.isEnabled()) {
            update();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (currentSeqrun != null && currentSeqrun == evt.getSource()) {
            if (SeqRunI.OBJECT_DELETED.equals(evt.getPropertyName())) {
                currentSeqrun.removePropertyChangeListener(this);
                currentSeqrun = null;
                update();
            }
        }
    }

    private final class ComparisonTypeListModel extends BaseModel<ComparisonTypeI> implements ItemListener {

        @Override
        @SuppressWarnings("unchecked")
        public synchronized void update() {
            // disable all downstream elements
            content.clear();
            fireContentsChanged();
            comparisonTypeList.setEnabled(false);
            updateButton.setEnabled(false);

            if (currentSeqrun != null && numFinishedJobs > 1) {
                content.add(new PipelineComparison());
                if (haveGoldStandard) {
                    content.add(new GSComparison());
                }
            }

            if (currentComparisonType != null && content.contains(currentComparisonType)) {
                setSelectedItem(currentComparisonType);
                itemStateChanged(new ItemEvent(comparisonTypeList,
                        ItemEvent.ITEM_STATE_CHANGED,
                        getSelectedItem(),
                        ItemEvent.SELECTED));
            } else {
                if (!content.isEmpty()) {
                    setSelectedItem(content.get(0));
                }
            }

            comparisonTypeList.setEnabled(!content.isEmpty());
            fireContentsChanged();
            visListModel.update();
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

//            if (currentViewer != null) {
//                currentViewer.dispose();
//            }
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
            fireContentsChanged();
            updateButton.setEnabled(false);

            if (currentComparisonType != null) {

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
            }

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

            }
            visualizationTypeList.setEnabled(!content.isEmpty());
            updateButton.setEnabled(!content.isEmpty());
            fireContentsChanged();
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

//            if (currentViewer != null) {
//                currentViewer.dispose();
//            }
            currentViewer = visListModel.getSelectedItem();

            controlSplitPane.setBottomComponent(currentViewer.getCustomizer());
        }
    }
}
