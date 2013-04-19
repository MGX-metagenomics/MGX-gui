//package de.cebitec.mgx.gui.search;
//
///**
// *
// * @author pbelmann
// */
//import de.cebitec.mgx.gui.controller.MGXMaster;
//import de.cebitec.mgx.gui.datamodel.Observation;
//import de.cebitec.mgx.gui.datamodel.SeqRun;
//import de.cebitec.mgx.gui.datamodel.Sequence;
//import de.cebitec.mgx.gui.search.util.ObservationListCellRenderer;
//import de.cebitec.mgx.gui.search.util.ResultListModel;
//import java.awt.Cursor;
//import java.awt.Point;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.swing.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import javax.swing.event.ListSelectionEvent;
//import javax.swing.event.ListSelectionListener;
//import org.netbeans.api.settings.ConvertAsProperties;
//import org.openide.awt.ActionID;
//import org.openide.awt.ActionReference;
//import org.openide.awt.ActionReferences;
//import org.openide.util.*;
//import org.openide.util.NbBundle.Messages;
//import org.openide.windows.TopComponent;
//
///**
// * Top component which displays something.
// */
//@ConvertAsProperties(dtd = "-//de.cebitec.mgx.gui.search//Search//EN",
//        autostore = false)
//@TopComponent.Description(preferredID = "SearchTopComponent",
//        iconBase = "de/cebitec/mgx/gui/search/SequenceSearch.png",
//        persistenceType = TopComponent.PERSISTENCE_NEVER)
//@TopComponent.Registration(mode = "editor", openAtStartup = false)
//@ActionID(category = "Window", id = "de.cebitec.mgx.gui.search.SearchTopComponent")
//@ActionReferences({
//    @ActionReference(path = "Menu/Window", position = 332),
//    @ActionReference(path = "Toolbars/UndoRedo", position = 525)
//})
//@TopComponent.OpenActionRegistration(displayName = "#CTL_SearchAction",
//        preferredID = "SearchTopComponent")
//@Messages({
//    "CTL_SearchAction=Search",
//    "CTL_SearchTopComponent=Search Window",
//    "HINT_SearchTopComponent=MGX Search window"
//})
//public final class SearchTopComponent extends TopComponent implements LookupListener, ActionListener, DocumentListener {
//
//    private final Lookup.Result<MGXMaster> result;
//    private MGXMaster currentMaster = null;
//    private final DefaultListModel<SeqRun> runListModel = new DefaultListModel<>();
//    private ResultListModel resultModel = new ResultListModel();
//    //private RequestProcessor proc;
//    private JButton selectAll;
//
//    public SearchTopComponent() {
//        initComponents();
//        setName(Bundle.CTL_SearchTopComponent());
//        setToolTipText(Bundle.HINT_SearchTopComponent());
//        result = Utilities.actionsGlobalContext().lookupResult(MGXMaster.class);
//        runList.setModel(runListModel);
//        runList.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                enableButton();
//            }
//        });
//
//        searchTerm.getDocument().addDocumentListener(this);
//        button.addActionListener(this);
//        resultList.setModel(resultModel);
//        resultList.setEnabled(true);
//        //resultList.setCellRenderer(new ObservationListCellRenderer());
//        readList.setModel(resultModel);
//
//
////        readList.addPropertyChangeListener(new PropertyChangeListener() {
////            @Override
////            public void propertyChange(PropertyChangeEvent evt) {
////
////                if (evt.getPropertyName().equals(JCheckBoxList.selectionChange) && evt.getNewValue() == true) {
////                    deselectAll.setEnabled(true);
////                    fetchSeqButton.setEnabled(true);
////                } else if (evt.getPropertyName().equals(JCheckBoxList.selectionChange) && readList.getSelectedEntries().isEmpty()) {
////                    deselectAll.setEnabled(false);
////                    fetchSeqButton.setEnabled(false);
////                }
////            }
////        });
//
//
//        /*
//         * make sure result list is synced to read list whenever a read name gets
//         * selected
//         */
//
//        readList.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                int[] idx = readList.getSelectedIndices();
//                resultList.setSelectedIndices(idx);
//                resultList.ensureIndexIsVisible(idx.length > 0 ? idx[0] : 0);
//            }
//        });
//
//    }
//
//    private void enableButton() {
//        /* 
//         * enable "search" button only when seqruns are selected and a 
//         * search term is present
//         */
//        button.setEnabled(!"".equals(searchTerm.getText()) && getSelectedSeqRuns().length > 0);
//    }
//
//    /**
//     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
//     * regenerated by the Form Editor.
//     */
//    private void initComponents() {
//
//        jScrollPane2 = new javax.swing.JScrollPane();
//        jList2 = new javax.swing.JList();
//        jSplitPane1 = new javax.swing.JSplitPane();
//        jPanel1 = new javax.swing.JPanel();
//        jPanel2 = new javax.swing.JPanel();
//        jSplitPane2 = new javax.swing.JSplitPane();
//        jScrollPane3 = new javax.swing.JScrollPane();
//
//
//        resultList = new javax.swing.JList() {
//            private ObservationListCellRenderer renderer = null;
//
//            @Override
//            public String getToolTipText(MouseEvent event) {
//                int index = locationToIndex(event.getPoint());
//                if (renderer == null) {
//                    renderer = (ObservationListCellRenderer) getCellRenderer();
//                }
//
//                StringBuilder names = null;
//
//                if (!renderer.getToolTips().isEmpty()) {
//                    names = new StringBuilder("<html><table border=0><colgroup width=200></colgroup><td>");
//                    names.append("<tr> Read Name: ").append(renderer.getReadNames().get(index)).append("</tr>");
//                    int counter = -1;
//                    String obsName;
//                    String[] parts;
//
//                    for (Layer layer : renderer.getToolTips().get(index)) {
//                        for (Observation obs : layer.getObservations()) {
//                            counter++;
//                            obsName = obs.getAttributeName();
//                            if (obsName.length() > 50) {
//                                parts = obsName.split(" ");
//                                obsName = "";
//                                for (int i = 0; i < parts.length; i++) {
//
//                                    if (i == parts.length / 2) {
//                                        obsName += "<br>";
//                                    }
//                                    obsName += parts[i] + " ";
//
//                                }
//                            }
//                            names.append("<tr> ").append(counter).append(": ")
//                                    .append(obsName)
//                                    //                                + " Start/Stop: " +obs.getStart()+"/"+obs.getStop()
//                                    .append(" </tr>");
//                        }
//                    }
//
//                    names.append("</td></table></html>");
//
//                }
//                assert names != null;
//                return names.toString();
//            }
//
//            @Override
//            public Point getToolTipLocation(MouseEvent event) {
//                Point point;
//
//                try {
//                    // FIXME indexToLocation(locationToIndex) - ???
//                    point = new Point(getX() + getWidth(),
//                            (int) indexToLocation(locationToIndex(event.getPoint())).getY());
//
//                } catch (NullPointerException ex) {
//                    point = new Point(0, 0);
//                }
//
//                return point;
//            }
//        };
//        jScrollPane4 = new javax.swing.JScrollPane();
//        readList = new JCheckBoxList();
//        topPanel = new javax.swing.JPanel();
//        jScrollPane1 = new javax.swing.JScrollPane();
//        runList = new javax.swing.JList();
//        jLabel1 = new javax.swing.JLabel();
//        searchTerm = new javax.swing.JTextField();
//        button = new javax.swing.JButton();
//        exact = new javax.swing.JCheckBox();
//        numResults = new javax.swing.JLabel("Hits found:");
//        fetchSeqButton = new javax.swing.JButton("Get Sequences");
//        deselectAll = new javax.swing.JButton("Deselect All");
//        selectAll = new javax.swing.JButton("Select All");
//
//        fetchSeqButton.setEnabled(false);
//        fetchSeqButton.setActionCommand("getSequence");
//        deselectAll.setEnabled(false);
//        deselectAll.setActionCommand("DeselectAll");
//        selectAll.setEnabled(false);
//        selectAll.setActionCommand("SelectAll");
//        deselectAll.addActionListener(this);
//        selectAll.addActionListener(this);
//        fetchSeqButton.addActionListener(this);
//
//
//        jScrollPane2.setViewportView(jList2);
//
//        setLayout(new java.awt.BorderLayout());
//
//        jSplitPane1.setDividerLocation(200);
//        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
//
//        jPanel1.setLayout(new java.awt.BorderLayout());
//        jSplitPane1.setBottomComponent(jPanel1);
//
//        jPanel2.setLayout(new java.awt.BorderLayout());
//        jSplitPane1.setLeftComponent(jPanel2);
//
//        jSplitPane2.setDividerLocation(540);
//
//        jScrollPane3.setViewportView(resultList);
//
//
//        jSplitPane2.setLeftComponent(jScrollPane3);
//
//        readList.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
//
//
//        jScrollPane4.setViewportView(readList);
//
//        jSplitPane2.setRightComponent(jScrollPane4);
//
//        jSplitPane1.setBottomComponent(jSplitPane2);
//
//        jScrollPane1.setViewportView(runList);
//
//        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel1.text")); // NOI18N
//
//        searchTerm.setText(org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.searchTerm.text")); // NOI18N
//
//        org.openide.awt.Mnemonics.setLocalizedText(button, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.button.text")); // NOI18N
//        button.setEnabled(false);
//
//        org.openide.awt.Mnemonics.setLocalizedText(exact, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.exact.text")); // NOI18N
//
//        org.openide.awt.Mnemonics.setLocalizedText(numResults, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.numResults.text")); // NOI18N
//
//        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
//        topPanel.setLayout(topPanelLayout);
//        topPanelLayout.setHorizontalGroup(
//                topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addGroup(topPanelLayout.createSequentialGroup()
//                .addContainerGap()
//                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
//                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addComponent(button, javax.swing.GroupLayout.Alignment.TRAILING)
//                .addGroup(GroupLayout.Alignment.CENTER, topPanelLayout.createSequentialGroup().addComponent(fetchSeqButton)
//                .addGap(5, 5, 5).addComponent(deselectAll)
//                .addGap(5, 5, 5).addComponent(selectAll))
//                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
//                .addComponent(jLabel1)
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
//                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
//                .addComponent(exact)
//                .addComponent(searchTerm, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
//                .addComponent(numResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
//                .addContainerGap()));
//        topPanelLayout.setVerticalGroup(
//                topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addGroup(topPanelLayout.createSequentialGroup()
//                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addGroup(topPanelLayout.createSequentialGroup()
//                .addGap(22, 22, 22)
//                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                .addComponent(jLabel1)
//                .addComponent(searchTerm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
//                .addComponent(exact)
//                .addGap(10, 10, 10)
//                .addComponent(numResults)
//                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                .addComponent(button).addGap(15, 15, 15)
//                .addGroup(topPanelLayout.createSequentialGroup().addGroup(topPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
//                .addComponent(fetchSeqButton).addComponent(deselectAll).addComponent(selectAll))))
//                .addGroup(topPanelLayout.createSequentialGroup()
//                .addContainerGap()
//                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)))
//                .addContainerGap()));
//
//        jSplitPane1.setLeftComponent(topPanel);
//
//        add(jSplitPane1, java.awt.BorderLayout.CENTER);
//    }
//    // Variables declaration - do not modify
//    private javax.swing.JButton button;
//    private javax.swing.JCheckBox exact;
//    private javax.swing.JLabel jLabel1;
//    private javax.swing.JList jList2;
//    private javax.swing.JPanel jPanel1;
//    private javax.swing.JPanel jPanel2;
//    private javax.swing.JScrollPane jScrollPane1;
//    private javax.swing.JScrollPane jScrollPane2;
//    private javax.swing.JScrollPane jScrollPane3;
//    private javax.swing.JScrollPane jScrollPane4;
//    private javax.swing.JSplitPane jSplitPane1;
//    private javax.swing.JSplitPane jSplitPane2;
//    private javax.swing.JLabel numResults;
//    private JCheckBoxList<Sequence> readList;
//    private javax.swing.JList resultList;
//    private javax.swing.JList runList;
//    private javax.swing.JTextField searchTerm;
//    private javax.swing.JPanel topPanel;
//    private javax.swing.JButton fetchSeqButton;
//    private javax.swing.JButton deselectAll;
//    // End of variables declaration
//
//    @Override
//    public void componentOpened() {
//        result.addLookupListener(this);
//       // proc = new RequestProcessor("MGX-ObservationFetch-Pool", Runtime.getRuntime().availableProcessors() + 4);
//        updateSeqRunList();
//    }
//
//    @Override
//    public void componentClosed() {
//        result.removeLookupListener(this);
//       // proc.shutdownNow();
//    }
//
//    void writeProperties(java.util.Properties p) {
//        p.setProperty("version", "1.0");
//    }
//
//    void readProperties(java.util.Properties p) {
//        String version = p.getProperty("version");
//    }
//
//    @Override
//    public void resultChanged(LookupEvent le) {
//        updateSeqRunList();
//    }
//
//    private void updateSeqRunList() {
//        for (MGXMaster newMaster : result.allInstances()) {
//            if (currentMaster == null || !newMaster.equals(currentMaster)) {
//
//                /*
//                 * we have a new MGXMaster instance; remember it and start a
//                 * worker to fetch the list of associated sequencing runs
//                 */
//                currentMaster = newMaster;
//
//                SwingWorker worker = new SwingWorker<List<SeqRun>, Void>() {
//                    @Override
//                    protected List<SeqRun> doInBackground() throws Exception {
//                        return currentMaster.SeqRun().fetchall();
//                    }
//
//                    @Override
//                    protected void done() {
//                        runListModel.removeAllElements();
//                        try {
//                            for (SeqRun sr : get()) {
//                                runListModel.addElement(sr);
//                            }
//                            runList.setSelectedIndex(0);
//                        } catch (InterruptedException | ExecutionException ex) {
//                            Exceptions.printStackTrace(ex);
//                        }
//                        super.done();
//                    }
//                };
//                worker.execute();
//
//                /*
//                 * we have a new MGXMaster set, return..
//                 */
//                return;
//            }
//        }
//    }
//
//    private SeqRun[] getSelectedSeqRuns() {
//        List<SeqRun> selected = runList.getSelectedValuesList();
//        return selected.toArray(new SeqRun[selected.size()]);
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//
//        switch (e.getActionCommand()) {
//            case "getSequence":
//                ReadWindow rw = new ReadWindow(currentMaster, new ArrayList(resultModel.getSelectedEntries()));
//                break;
//            case "Search":
//
//                SwingWorker<Sequence[], Void> worker = new SwingWorker<Sequence[], Void>() {
//                    @Override
//                    protected Sequence[] doInBackground() throws Exception {
//                        long start = System.currentTimeMillis();
//                        Sequence[] ret = currentMaster.Attribute().search(getSelectedSeqRuns(), searchTerm.getText(), exact.isSelected());
//                        start = System.currentTimeMillis() - start;
//                        Logger.getGlobal().log(Level.INFO, "search for {0} took {1}ms", new Object[]{searchTerm.getText(), start});
//                        return ret;
//                    }
//
//                    @Override
//                    protected void done() {
//                        Sequence[] hits;
//                        try {
//                            hits = get();
//                            numResults.setText("Hits found: " + hits.length);
//                            selectAll.setEnabled(hits.length > 0);
//                            resultModel.setResult(hits);
//                            
//                            //readList.addElements(hits);
//                            //readList.deselectAll();
//                        } catch (InterruptedException | ExecutionException ex) {
//                            Exceptions.printStackTrace(ex);
//                        } finally {
//                            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//                        }
//                        super.done();
//                    }
//                };
//                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//                numResults.setText("Searching..");
//                worker.execute();
//                break;
//        }
//    }
//
//    @Override
//    public void insertUpdate(DocumentEvent e) {
//        enableButton();
//    }
//
//    @Override
//    public void removeUpdate(DocumentEvent e) {
//        enableButton();
//    }
//
//    @Override
//    public void changedUpdate(DocumentEvent e) {
//        enableButton();
//    }
//}
