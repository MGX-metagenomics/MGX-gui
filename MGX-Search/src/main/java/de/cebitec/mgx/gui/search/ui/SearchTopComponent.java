package de.cebitec.mgx.gui.search.ui;

import de.cebitec.mgx.gui.swingutils.SeqPanel;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.gui.search.util.ReadModel;
import de.cebitec.mgx.gui.search.util.TermModel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
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
        iconBase = "de/cebitec/mgx/gui/search/SequenceSearch.svg",
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
public final class SearchTopComponent extends TopComponent implements LookupListener, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup.Result<SeqRunI> result;
    private final TermModel termModel = new TermModel();
    private final ReadModel readModel = new ReadModel();
    private final ObservationView ov = new ObservationView();

    private SeqRunI currentRun = null;

    public SearchTopComponent() {
        initComponents();
        super.setName("Search");
        super.setToolTipText("Metagenome search");
        obsPanel.add(ov, BorderLayout.CENTER);
        result = Utilities.actionsGlobalContext().lookupResult(SeqRunI.class);

        termField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTermList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTermList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTermList();
            }
        });

        termList.setModel(termModel);
        termList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                updateReadList();
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
                    ov.clear();
                    showSeq.setEnabled(false);
                    return;
                }
                if (seq != curSeq) {
                    curSeq = seq;
                    SwingWorker<Iterator<ObservationI>, Void> sw = new SwingWorker<Iterator<ObservationI>, Void>() {

                        @Override
                        protected Iterator<ObservationI> doInBackground() throws Exception {
                            if (!currentRun.isDeleted()) {
                                return currentRun.getMaster().Observation().ByRead(curSeq);
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
                    ov.show(seq, obs.toArray(new ObservationI[]{}), termList.getSelectedValue());
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
                        if (seq != null && !currentRun.isDeleted()) {
                            return currentRun.getMaster().Sequence().fetch(seq.getId());
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

                                final SeqPanel sp = new SeqPanel();
                                sp.show(seq);

                                DialogDescriptor d = new DialogDescriptor(sp, "DNA sequence for " + seq.getName(), true, DialogDescriptor.DEFAULT_OPTION, opts[0], null);
                                d.setOptions(opts);
                                d.setClosingOptions(opts);
                                d.setAdditionalOptions(new Object[]{"Copy to clipboard"});
                                d.setMessage(sp);
                                d.addPropertyChangeListener(new PropertyChangeListener() {
                                    @Override
                                    public void propertyChange(PropertyChangeEvent evt) {
                                        if (evt.getPropertyName().equals(DialogDescriptor.PROP_VALUE) && evt.getNewValue().equals("Copy to clipboard")) {
                                            Toolkit.getDefaultToolkit()
                                                    .getSystemClipboard()
                                                    .setContents(sp.getSelection(), null);
                                        }

                                    }

                                });
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

    @Override
    public Image getIcon() {
        Image image = super.getIcon();
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        readList = new javax.swing.JComboBox<>();
        obsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        termList = new javax.swing.JList<>();
        termField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        hitNum = new javax.swing.JLabel();
        showSeq = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        runName = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel1.text")); // NOI18N

        readList.setEnabled(false);

        obsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        obsPanel.setLayout(new java.awt.BorderLayout());

        termList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        termList.setEnabled(false);
        jScrollPane2.setViewportView(termList);

        termField.setText(org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.termField.text")); // NOI18N
        termField.setEnabled(false);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel3.text")); // NOI18N

        hitNum.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(hitNum, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.hitNum.text")); // NOI18N

        showSeq.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(showSeq, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.showSeq.text")); // NOI18N
        showSeq.setEnabled(false);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(runName, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.runName.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(readList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(obsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(termField))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(showSeq))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(hitNum, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(137, 743, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(runName)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(runName))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(termField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hitNum, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(readList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(obsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showSeq)
                .addGap(18, 18, 18))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel hitNum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel obsPanel;
    private javax.swing.JComboBox<SequenceI> readList;
    private javax.swing.JLabel runName;
    private javax.swing.JButton showSeq;
    private javax.swing.JTextField termField;
    private javax.swing.JList<String> termList;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        result.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
    }

    private boolean isActivated = false;

    @Override
    protected void componentDeactivated() {
        super.componentDeactivated();
        isActivated = false;
    }

    @Override
    protected void componentActivated() {
        super.componentActivated();
        isActivated = true;
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public synchronized void resultChanged(LookupEvent le) {
        // avoid update when SearchTC is activated
        if (isActivated) {
            return;
        }

        if (currentRun != null) {
            currentRun.removePropertyChangeListener(this);
            currentRun = null;
        }

        for (SeqRunI run : result.allInstances()) {
            currentRun = run;
        }

        if (currentRun == null) {
            runName.setText("None");
            termField.setText("");
            termField.setEnabled(false);
            termModel.clear();
            termList.setEnabled(false);
            readModel.clear();
            readList.setEnabled(false);
        } else {
            currentRun.addPropertyChangeListener(this);
            runName.setText(currentRun.getName());
            termField.setEnabled(true);
            termList.setEnabled(true);
        }
    }

    private void updateTermList() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        termModel.setRun(currentRun);
        termModel.setTerm(termField.getText());
        termModel.update();
        termList.setEnabled(termModel.getSize() > 0);
        updateReadList();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void updateReadList() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        readModel.setRun(currentRun);
        readModel.setTerm(termList.getSelectedValue());
        readModel.update();
        if (readModel.getSize() > 0) {
            readList.setEnabled(true);
            readList.setSelectedIndex(0);
            hitNum.setText(readModel.getSize() + " hits");
        } else {
            readList.setEnabled(false);
            hitNum.setText("No hits");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof SeqRunI && evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
            SeqRunI run = (SeqRunI) evt.getSource();
            if (currentRun.equals(run)) {
                currentRun.removePropertyChangeListener(this);
                currentRun = null;
                updateTermList();
            }
        }
    }
}
