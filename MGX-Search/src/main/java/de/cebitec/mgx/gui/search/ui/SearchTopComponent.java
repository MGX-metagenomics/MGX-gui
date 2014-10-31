package de.cebitec.mgx.gui.search.ui;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.gui.search.util.ReadModel;
import de.cebitec.mgx.gui.search.util.TermModel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
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
    "HINT_SearchTopComponent=Metagenome search"
})
public final class SearchTopComponent extends TopComponent implements LookupListener, ActionListener {

    private final Lookup.Result<MGXMasterI> result;
    private MGXMasterI currentMaster = null;
    private final DefaultListModel<SeqRunI> runListModel = new DefaultListModel<>();
    private final TermModel termModel = new TermModel();
    private final ReadModel readModel = new ReadModel();
    private final ObservationView ov = new ObservationView();

    public SearchTopComponent() {
        initComponents();
        obsPanel.add(ov, BorderLayout.CENTER);
        result = Utilities.actionsGlobalContext().lookupResult(MGXMasterI.class);

        // search field
        searchTerm.setModel(termModel);
        AutoCompleteDecorator.decorate(searchTerm); //, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);
        searchTerm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object o = searchTerm.getSelectedItem();
                if (o == null) {
                    return;
                }
                termModel.setTerm(o.toString());
                termModel.update();
            }
        });

        final JTextComponent tc = (JTextComponent) searchTerm.getEditor().getEditorComponent();
        tc.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = null;
                try {
                    text = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                termModel.setTerm(text);
                updateTerm();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = null;
                try {
                    text = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                termModel.setTerm(text);
                updateTerm();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String text = null;
                try {
                    text = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                termModel.setTerm(text);
                updateTerm();
            }
        });
//        tc.addKeyListener(new KeyAdapter() {
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                super.keyReleased(e);
//                termModel.setTerm(tc.getText());
//                updateTerm();
//            }
//
//        });

        // read list
        readList.setModel(readModel);
        readList.addActionListener(new ActionListener() {

            private SequenceI curSeq = null;

            @Override
            public void actionPerformed(ActionEvent e) {
                final SequenceI seq = readModel.getSelectedItem();
                if (seq == null) {
                    return;
                }
                if (seq != curSeq) {
                    curSeq = seq;
                    SwingWorker<Iterator<ObservationI>, Void> sw = new SwingWorker<Iterator<ObservationI>, Void>() {

                        @Override
                        protected Iterator<ObservationI> doInBackground() throws Exception {
                            return currentMaster.Observation().ByRead(seq);
                        }
                    };
                    sw.execute();
                    Iterator<ObservationI> iter = null;
                    try {
                        iter = sw.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    List<ObservationI> obs = new ArrayList<>();
                    while (iter != null && iter.hasNext()) {
                        obs.add(iter.next());
                    }
                    //Collections.sort(obs, comp);
                    ov.show(seq, obs.toArray(new ObservationI[]{}), false, "");
                }
            }

            private Iterator<ObservationI> get() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        setName(Bundle.CTL_SearchTopComponent());
        setToolTipText(Bundle.HINT_SearchTopComponent());
        runList.setModel(runListModel);
        runList.setSelectedIndex(-1);

        runList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                enableButton();
                termModel.setRuns(getSelectedSeqRuns());
                termModel.update();
            }
        });

        // set up listener to perform the actual search
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
        jLabel1 = new javax.swing.JLabel();
        executeSearch = new javax.swing.JButton();
        searchTerm = new javax.swing.JComboBox<String>();
        readList = new javax.swing.JComboBox<SequenceI>();
        obsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        jScrollPane1.setViewportView(runList);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(executeSearch, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.executeSearch.text")); // NOI18N
        executeSearch.setEnabled(false);

        searchTerm.setEditable(true);
        searchTerm.setEnabled(false);

        readList.setEnabled(false);

        obsPanel.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel2.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(obsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(readList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(searchTerm, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(executeSearch))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(jLabel2))
                        .addGap(0, 549, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(searchTerm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(executeSearch))
                .addGap(7, 7, 7)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(readList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(obsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton executeSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel obsPanel;
    private javax.swing.JComboBox<SequenceI> readList;
    private javax.swing.JList runList;
    private javax.swing.JComboBox<String> searchTerm;
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
        String term = termModel.getSelectedItem();
        if (currentMaster == null || term == null || "".equals(term)) {
            return;
        }

        /*
         * search button has been pressed
         */
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        readModel.setMaster(currentMaster);
        readModel.setRuns(getSelectedSeqRuns());
        readModel.setTerm(termModel.getSelectedItem());
        readModel.update();
        if (readModel.getSize() > 0) {
            readList.setEnabled(true);
            readList.setSelectedIndex(0);
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void enableButton() {
        /* 
         * enable "search" button only when seqruns are selected and a 
         * search term is present
         */
        executeSearch.setEnabled(!"".equals(termModel.getSelectedItem()) && getSelectedSeqRuns().length > 0);
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
                termModel.setMaster(currentMaster);

                searchTerm.setEnabled(true);
                executeSearch.setEnabled(true);

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
                        Object o = runList.getSelectedValue();
                        if (o != null) {
                            termModel.setRuns(new SeqRunI[]{(SeqRunI) o});
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

//    class VariableRowHeightRenderer implements TableCellRenderer {
//
//        private final ObservationView oview = new ObservationView();
//
//        public VariableRowHeightRenderer() {
//            super();
//            setOpaque(true);
//        }
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
//
//            SequenceI value = (SequenceI) obj;
//            if (cache.containsKey(value) && cache.get(value).get() != null) {
//                oview.show(value, cache.get(value).get(), hasFocus, termModel.getSelectedItem());
//            } else {
//                // check if worker for this sequence is already running
//                if (!activeTasks.contains(value)) {
//                    Runnable r = new ObservationFetcher(activeTasks, currentMaster, value, cache);
//                    //proc.post(r);
//                    oview.show(value, "Data not yet loaded", hasFocus);
//                } else {
//                    oview.show(value, "Waiting for data..", hasFocus);
//                }
//            }
//            table.setRowHeight(row, oview.getPreferredSize().height);
//
//            return oview;
//        }
//    }
    private void updateTerm() {
        termModel.update();
    }
}
