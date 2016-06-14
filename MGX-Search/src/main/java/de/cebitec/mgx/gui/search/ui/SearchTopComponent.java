package de.cebitec.mgx.gui.search.ui;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.gui.search.util.ReadModel;
import de.cebitec.mgx.gui.search.util.SeqRunModel;
import de.cebitec.mgx.gui.search.util.TermModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
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
        displayName = "Search",
        preferredID = "SearchTopComponent"
)
public final class SearchTopComponent extends TopComponent implements LookupListener {

    private final Lookup.Result<MGXMasterI> result;
    private MGXMasterI currentMaster = null;
    private final SeqRunModel runListModel = new SeqRunModel();
    private final TermModel termModel = new TermModel();
    private final ReadModel readModel = new ReadModel();
    private final ObservationView ov = new ObservationView();

    public SearchTopComponent() {
        initComponents();
        obsPanel.add(ov, BorderLayout.CENTER);
        result = Utilities.actionsGlobalContext().lookupResult(MGXMasterI.class);

        setName("Search");
        setToolTipText("Metagenome search");
        runList.setModel(runListModel);
        runList.setSelectedIndex(-1);
        runList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                termModel.setRuns(getSelectedSeqRuns());
                termModel.update();
                termField.setEnabled(getSelectedSeqRuns().length > 0);
                termList.setEnabled(getSelectedSeqRuns().length > 0 && termModel.getSize() > 0);
                readModel.setRuns(getSelectedSeqRuns());
                readModel.update();
                readList.setEnabled(getSelectedSeqRuns().length > 0);
                hitNum.setText(readModel.getSize() + " hits");
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        runList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ((SeqRunI) value).getName(), index, isSelected, cellHasFocus);
            }

        });

        termField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTerm();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTerm();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTerm();
            }
        });

        termList.setModel(termModel);
        termList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                readModel.setTerm((String) termList.getSelectedValue());
                readModel.setRuns(getSelectedSeqRuns());
                readModel.setMaster(currentMaster);
                readModel.update();
                if (readModel.getSize() > 0 && !currentMaster.isDeleted()) {
                    readList.setEnabled(true);
                    readList.setSelectedIndex(0);
                    hitNum.setText(readModel.getSize() + " hits");
                } else {
                    readList.setEnabled(false);
                    hitNum.setText("No hits");
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

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
                            if (!seq.isDeleted()) {
                                return currentMaster.Observation().ByRead(seq);
                            }
                            return null;
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
                    ov.show(seq, obs.toArray(new ObservationI[]{}), (String) termList.getSelectedValue());
                    showSeq.setEnabled(true);
                }
            }

        });

        showSeq.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final SequenceI seq = readModel.getSelectedItem();
                SwingWorker<SequenceI, Void> sw = new SwingWorker<SequenceI, Void>() {

                    @Override
                    protected SequenceI doInBackground() throws Exception {
                        if (seq != null && !currentMaster.isDeleted()) {
                            return currentMaster.Sequence().fetch(seq.getId());
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        try {
                            SequenceI seq = get();
                            if (seq != null) {
                                String[] opts = new String[]{"Close"};
                                NotifyDescriptor d = new NotifyDescriptor(
                                        "Text",
                                        "DNA sequence for " + seq.getName(),
                                        NotifyDescriptor.DEFAULT_OPTION,
                                        NotifyDescriptor.PLAIN_MESSAGE,
                                        opts,
                                        opts[0]
                                );
                                SeqPanel sp = new SeqPanel();
                                sp.show(seq);
                                d.setMessage(sp);
                                DialogDisplayer.getDefault().notify(d);
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        super.done();
                    }

                };
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                sw.execute();
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        runList = new javax.swing.JList<SeqRunI>();
        jLabel1 = new javax.swing.JLabel();
        readList = new javax.swing.JComboBox<SequenceI>();
        obsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        termList = new javax.swing.JList<String>();
        termField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        hitNum = new javax.swing.JLabel();
        showSeq = new javax.swing.JButton();

        jScrollPane1.setViewportView(runList);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel1.text")); // NOI18N

        readList.setEnabled(false);

        obsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        obsPanel.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel2.text")); // NOI18N

        termList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        termList.setEnabled(false);
        jScrollPane2.setViewportView(termList);

        termField.setText(org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.termField.text")); // NOI18N
        termField.setEnabled(false);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel3.text")); // NOI18N

        hitNum.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(hitNum, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.hitNum.text")); // NOI18N

        showSeq.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(showSeq, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.showSeq.text")); // NOI18N
        showSeq.setEnabled(false);

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
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(termField))))
                    .addComponent(readList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(hitNum, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(showSeq)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(termField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(readList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hitNum, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(obsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showSeq)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel hitNum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel obsPanel;
    private javax.swing.JComboBox<SequenceI> readList;
    private javax.swing.JList<SeqRunI> runList;
    private javax.swing.JButton showSeq;
    private javax.swing.JTextField termField;
    private javax.swing.JList<String> termList;
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

                runListModel.setMaster(currentMaster);
                runListModel.update();

                termModel.setMaster(currentMaster);
                readModel.setMaster(currentMaster);
                /*
                 * we have a new MGXMaster set, return..
                 */
                return;
            }
        }
    }

    private void updateTerm() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //System.err.println("term is : " + termField.getText());
        termModel.setMaster(currentMaster);
        termModel.setRuns(getSelectedSeqRuns());
        termModel.setTerm(termField.getText());
        termModel.update();
        termList.setEnabled(termModel.getSize() > 0 && !currentMaster.isDeleted());
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
