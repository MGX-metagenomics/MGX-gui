package de.cebitec.mgx.gui.search.ui;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Sequence;
import de.cebitec.mgx.gui.search.util.ResultListModel;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.search.ui//Search//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "SearchTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.search.ui.SearchTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SearchAction",
        preferredID = "SearchTopComponent")
@Messages({
    "CTL_SearchAction=Search",
    "CTL_SearchTopComponent=Search Window",
    "HINT_SearchTopComponent=This is a Search window"
})
public final class SearchTopComponent extends TopComponent implements LookupListener, ActionListener, DocumentListener {

    private final Lookup.Result<MGXMaster> result;
    private MGXMaster currentMaster = null;
    private final DefaultListModel<SeqRun> runListModel = new DefaultListModel<>();
    private ResultListModel resultModel = new ResultListModel();

    public SearchTopComponent() {
        initComponents();
        setName(Bundle.CTL_SearchTopComponent());
        setToolTipText(Bundle.HINT_SearchTopComponent());
        result = Utilities.actionsGlobalContext().lookupResult(MGXMaster.class);
        runList.setModel(runListModel);

        runList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                enableButton();
            }
        });
        searchTerm.getDocument().addDocumentListener(this);

        // both lists work based on the same model
        obsList.setModel(resultModel);
        readList.setModel(resultModel);

        /*
         * make sure result list is synced to read list whenever a read name gets
         * selected
         */

        readList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] idx = readList.getSelectedIndices();
                obsList.setSelectedIndices(idx);
                obsList.ensureIndexIsVisible(idx.length > 0 ? idx[0] : 0);
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
         * a custom renderer for the observation list
         */
        obsList.setCellRenderer(new ListCellRenderer<Sequence>() {
        
            private ObservationView oview = new ObservationView();

            @Override
            public Component getListCellRendererComponent(JList list, Sequence value, int index, boolean isSelected, boolean cellHasFocus) {
                oview.show(value, null, cellHasFocus);
                return oview;
            }
        });

        /*
         * put progressbar in a "finished" state
         */
        searchProgress.setIndeterminate(false);
        searchProgress.setValue(0);

        // set up button listener
        executeSearch.addActionListener(this);
    }

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
        readList = new de.cebitec.mgx.gui.search.JCheckBoxList();
        jScrollPane3 = new javax.swing.JScrollPane();
        obsList = new javax.swing.JList();
        searchTerm = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        exact = new javax.swing.JCheckBox();
        executeSearch = new javax.swing.JButton();
        searchProgress = new javax.swing.JProgressBar();
        statusLine = new javax.swing.JLabel();

        runList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(runList);

        jSplitPane1.setDividerLocation(700);

        readList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(readList);

        jSplitPane1.setRightComponent(jScrollPane2);

        obsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        obsList.setMinimumSize(new java.awt.Dimension(500, 85));
        obsList.setPreferredSize(new java.awt.Dimension(500, 85));
        jScrollPane3.setViewportView(obsList);

        jSplitPane1.setLeftComponent(jScrollPane3);

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
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox exact;
    private javax.swing.JButton executeSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JList obsList;
    private de.cebitec.mgx.gui.search.JCheckBoxList readList;
    private javax.swing.JList runList;
    private javax.swing.JProgressBar searchProgress;
    private javax.swing.JTextField searchTerm;
    private javax.swing.JLabel statusLine;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        result.addLookupListener(this);
        updateSeqRunList();
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
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

        /*
         * search button has been pressed
         */
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLine.setText("Searching..");
        searchProgress.setIndeterminate(true);

        SwingWorker<Sequence[], Void> worker = new SwingWorker<Sequence[], Void>() {
            @Override
            protected Sequence[] doInBackground() throws Exception {
                long start = System.currentTimeMillis();
                Sequence[] ret = currentMaster.Attribute().search(getSelectedSeqRuns(), searchTerm.getText(), exact.isSelected());
                start = System.currentTimeMillis() - start;
                Logger.getGlobal().log(Level.INFO, "search for {0} took {1}ms, got {2} hits", new Object[]{searchTerm.getText(), start, ret.length});
                return ret;
            }

            @Override
            protected void done() {
                Sequence[] hits;
                try {
                    hits = get();
                    statusLine.setText("Results found: " + hits.length);
                    searchProgress.setIndeterminate(false);
                    searchProgress.setValue(100);

                    resultModel.setResult(hits);
                    readList.setModel(resultModel);
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

    private SeqRun[] getSelectedSeqRuns() {
        List<SeqRun> selected = runList.getSelectedValuesList();
        return selected.toArray(new SeqRun[selected.size()]);
    }

    private void updateSeqRunList() {
        for (MGXMaster newMaster : result.allInstances()) {
            if (currentMaster == null || !newMaster.equals(currentMaster)) {

                /*
                 * we have a new MGXMaster instance; remember it and start a
                 * worker to fetch the list of associated sequencing runs
                 */
                currentMaster = newMaster;

                SwingWorker worker = new SwingWorker<List<SeqRun>, Void>() {
                    @Override
                    protected List<SeqRun> doInBackground() throws Exception {
                        return currentMaster.SeqRun().fetchall();
                    }

                    @Override
                    protected void done() {
                        runListModel.removeAllElements();
                        try {
                            for (SeqRun sr : get()) {
                                runListModel.addElement(sr);
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
}
