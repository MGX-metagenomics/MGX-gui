package de.cebitec.mgx.gui.search.ui;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.gui.search.util.ObservationFetcher;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.search.ui//Search//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "SearchTopComponent",
        iconBase = "de/cebitec/mgx/gui/search/SequenceSearch.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.search.ui.SearchTopComponent")
@ActionReferences({
    @ActionReference(path = "Menu/Window", position = 332),
    @ActionReference(path = "Toolbars/UndoRedo", position = 525)
})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SearchAction",
        preferredID = "SearchTopComponent")
@Messages({
    "CTL_SearchAction=Search",
    "CTL_SearchTopComponent=Search Window",
    "HINT_SearchTopComponent=This is a Search window"
})
public final class SearchTopComponent extends TopComponent implements LookupListener, ActionListener, DocumentListener {

    private final Lookup.Result<MGXMasterI> result;
    private MGXMasterI currentMaster = null;
    private final DefaultListModel<SeqRunI> runListModel = new DefaultListModel<>();
    private RequestProcessor proc;
    private final List<String> autoCompletes = new ArrayList<>();

    public SearchTopComponent() {
        initComponents();
        setName(Bundle.CTL_SearchTopComponent());
        setToolTipText(Bundle.HINT_SearchTopComponent());
        result = Utilities.actionsGlobalContext().lookupResult(MGXMasterI.class);
        runList.setModel(runListModel);
        runList.setSelectedIndex(-1);

        runList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                autoCompletes.clear();
                enableButton();
                if (currentMaster != null && !searchTerm.getText().isEmpty()) {
                    SwingWorker<Iterator<String>, Void> sw = new SwingWorker<Iterator<String>, Void>() {

                        @Override
                        protected Iterator<String> doInBackground() throws Exception {
                            return currentMaster.Attribute().find(searchTerm.getText(), getSelectedSeqRuns());
                        }

                        @Override
                        protected void done() {
                            try {
                                Iterator<String> terms = get();
                                while (terms.hasNext()) {
                                    autoCompletes.add(terms.next());
                                }
                                Collections.sort(autoCompletes);
                            } catch (InterruptedException | ExecutionException ex) {
                            }
                            super.done();
                        }

                    };
                    sw.execute();
                }
            }
        });
        searchTerm.getDocument().addDocumentListener(this);
        searchTerm.addActionListener(this);

        // both list and table work based on the same data
        obsTable.setModel(new AbstractTableModel() {

            @Override
            public int getRowCount() {
                return readList.getModel().getSize();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return readList.getModel().getElementAt(rowIndex);
            }
        });

        /*
         * make sure result list is synced to read list whenever a read name gets
         * selected
         */
        readList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] idx = readList.getSelectedIndices();
                //obsList.setSelectedIndices(idx);
                //obsList.ensureIndexIsVisible(idx.length > 0 ? idx[0] : 0);
            }
        });

//        readList.addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//
//                if (evt.getPropertyName().equals(JCheckBoxList.selectionChange) && evt.getNewValue() == true) {
//                    deselectAll.setEnabled(true);
//                    fetchSeqButton.setEnabled(true);
//                } else if (evt.getPropertyName().equals(JCheckBoxList.selectionChange) && readList.getSelectedEntries().isEmpty()) {
//                    deselectAll.setEnabled(false);
//                    fetchSeqButton.setEnabled(false);
//                }
//            }
//        });

        /*
         * a custom renderer for the observation table
         */
        obsTable.getColumnModel().getColumn(0).setCellRenderer(new VariableRowHeightRenderer());
//        obsList.setCellRenderer(new ListCellRenderer<Sequence>() {
//            @Override
//            public Component getListCellRendererComponent(JList list, Sequence value, int index, boolean isSelected, boolean cellHasFocus) {
//            }
//        });


        /*
         * put progressbar in a "finished" state
         */
        searchProgress.setIndeterminate(false);
        searchProgress.setValue(0);

        // set up button listener
        executeSearch.addActionListener(this);
    }
    private final ConcurrentMap<SequenceI, WeakReference<ObservationI[]>> cache = new ConcurrentHashMap<>();
    private final Set<SequenceI> activeTasks = Collections.<SequenceI>synchronizedSet(new HashSet<SequenceI>());

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        runList = new javax.swing.JList();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        readList = new de.cebitec.mgx.gui.swingutils.JCheckBoxList<SequenceI>();
        jScrollPane4 = new javax.swing.JScrollPane();
        obsTable = new javax.swing.JTable();
        searchTerm = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        exact = new javax.swing.JCheckBox();
        executeSearch = new javax.swing.JButton();
        searchProgress = new javax.swing.JProgressBar();
        statusLine = new javax.swing.JLabel();

        jScrollPane1.setViewportView(runList);

        jSplitPane1.setResizeWeight(1.0);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(180, 131));

        jScrollPane2.setViewportView(readList);

        jSplitPane1.setRightComponent(jScrollPane2);

        obsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        obsTable.setFillsViewportHeight(true);
        obsTable.setTableHeader(null);
        jScrollPane4.setViewportView(obsTable);

        jSplitPane1.setLeftComponent(jScrollPane4);

        searchTerm.setText(org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.searchTerm.text")); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel1.text")); // NOI18N

        exact.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(exact, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.exact.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(executeSearch, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.executeSearch.text")); // NOI18N

        searchProgress.setIndeterminate(true);

        org.openide.awt.Mnemonics.setLocalizedText(statusLine, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.statusLine.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(searchTerm)
                            .addComponent(exact)
                            .addComponent(executeSearch)
                            .addComponent(searchProgress, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                            .addComponent(statusLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchTerm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(7, 7, 7)
                        .addComponent(exact)
                        .addGap(9, 9, 9)
                        .addComponent(executeSearch)
                        .addGap(10, 10, 10)
                        .addComponent(searchProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(statusLine, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox exact;
    private javax.swing.JButton executeSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable obsTable;
    private de.cebitec.mgx.gui.swingutils.JCheckBoxList<SequenceI> readList;
    private javax.swing.JList runList;
    private javax.swing.JProgressBar searchProgress;
    private javax.swing.JTextField searchTerm;
    private javax.swing.JLabel statusLine;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        result.addLookupListener(this);
        updateSeqRunList();
        proc = new RequestProcessor("MGX-ObservationFetch-Pool", 3 * Runtime.getRuntime().availableProcessors() + 10);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
        proc.shutdownNow();
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public void resultChanged(LookupEvent le) {
        updateSeqRunList();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("".equals(searchTerm.getText())) {
            return;
        }

        /*
         * search button has been pressed
         */
        readList.clear();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLine.setText("Searching..");
        searchProgress.setIndeterminate(true);

        SwingWorker<Iterator<SequenceI>, Void> worker = new SwingWorker<Iterator<SequenceI>, Void>() {
            @Override
            protected Iterator<SequenceI> doInBackground() throws Exception {
                //long start = System.currentTimeMillis();
                Iterator<SequenceI> ret = currentMaster.Attribute().search(searchTerm.getText(), exact.isSelected(), getSelectedSeqRuns());
                //start = System.currentTimeMillis() - start;
                //Logger.getGlobal().log(Level.INFO, "search for {0} took {1}ms, got {2} hits", new Object[]{searchTerm.getText(), start, ret.length});
                return ret;
            }

            @Override
            protected void done() {
                Iterator<SequenceI> hits;
                try {
                    hits = get();
                    int numHits = 0;
                    searchProgress.setIndeterminate(false);
                    searchProgress.setValue(100);
                    readList.clear();
                    while (hits.hasNext()) {
                        SequenceI s = hits.next();
                        readList.addElement(s);
                        numHits++;

                        // pre-start observation fetchers
                        Runnable r = new ObservationFetcher(activeTasks, currentMaster, s, cache);
                        proc.post(r);
                    }
                    statusLine.setText("Results found: " + numHits);
                    // notify table of updated data
                    AbstractTableModel model = (AbstractTableModel) obsTable.getModel();
                    model.fireTableDataChanged();

                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    searchProgress.setValue(searchProgress.getMaximum());
                }
                super.done();
            }
        };
        worker.execute();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        enableButton();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        enableButton();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        enableButton();
    }

    private void enableButton() {
        /* 
         * enable "search" button only when seqruns are selected and a 
         * search term is present
         */
        executeSearch.setEnabled(!"".equals(searchTerm.getText()) && getSelectedSeqRuns().length > 0);
    }

    private SeqRunI[] getSelectedSeqRuns() {
        List<SeqRunI> selected = runList.getSelectedValuesList();
        return selected.toArray(new SeqRunI[selected.size()]);
    }

    private void updateSeqRunList() {
        for (MGXMasterI newMaster : result.allInstances()) {
            if (currentMaster == null || !newMaster.equals(currentMaster)) {

                /*
                 * we have a new MGXMaster instance; remember it and start a
                 * worker to fetch the list of associated sequencing runs
                 */
                currentMaster = newMaster;

                SwingWorker worker = new SwingWorker<Iterator<SeqRunI>, Void>() {
                    @Override
                    protected Iterator<SeqRunI> doInBackground() throws Exception {
                        return currentMaster.SeqRun().fetchall();
                    }

                    @Override
                    protected void done() {
                        runListModel.removeAllElements();
                        try {
                            Iterator<SeqRunI> iter = get();
                            while (iter.hasNext()) {
                                runListModel.addElement(iter.next());
                            }
                            runList.setSelectedIndex(0);
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        super.done();
                    }
                };
                worker.execute();

                /*
                 * we have a new MGXMaster set, return..
                 */
                return;
            }
        }
    }

    class VariableRowHeightRenderer implements TableCellRenderer {

        private final ObservationView oview = new ObservationView();

        public VariableRowHeightRenderer() {
            super();
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {

            SequenceI value = (SequenceI) obj;
            if (cache.containsKey(value) && cache.get(value).get() != null) {
                oview.show(value, cache.get(value).get(), hasFocus, searchTerm.getText());
            } else {
                // check if worker for this sequence is already running
                if (!activeTasks.contains(value)) {
                    Runnable r = new ObservationFetcher(activeTasks, currentMaster, value, cache);
                    proc.post(r);
                    oview.show(value, "Data not yet loaded", hasFocus);
                } else {
                    oview.show(value, "Waiting for data..", hasFocus);
                }
            }
            table.setRowHeight(row, oview.getPreferredSize().height);

            return oview;
        }
    }
}
