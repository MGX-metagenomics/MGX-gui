package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.misc.SearchResult;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.*;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.cebitec.mgx.gui.search//Search//EN",
autostore = false)
@TopComponent.Description(preferredID = "SearchTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.search.SearchTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_SearchAction",
preferredID = "SearchTopComponent")
@Messages({
    "CTL_SearchAction=Search",
    "CTL_SearchTopComponent=Search Window",
    "HINT_SearchTopComponent=This is a Search window"
})
public final class SearchTopComponent extends TopComponent implements LookupListener, ActionListener {

    private Lookup.Result<MGXMaster> result;
    private MGXMaster currentMaster = null;
    DefaultListModel<SeqRun> runListModel = new DefaultListModel<>();
    private ResultListModel resultModel = new ResultListModel();

    public SearchTopComponent() {
        initComponents();
        setName(Bundle.CTL_SearchTopComponent());
        setToolTipText(Bundle.HINT_SearchTopComponent());

        // both working on same model - FIXME: renderers
        resultList.setModel(resultModel);
        readList.setModel(resultModel);
        
        readList.setCellRenderer(new ReadListListCellRenderer());

        runList.setModel(runListModel);
        runList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                enableButton();
            }
        });
        runList.setCellRenderer(new ListCellRenderer<SeqRun>() {

            private JLabel label = new JLabel();

            @Override
            public Component getListCellRendererComponent(JList list, SeqRun value, int index, boolean isSelected, boolean cellHasFocus) {
                label.setText(value.getName());
                label.setBackground(isSelected ? Color.BLUE : Color.WHITE);
                return label;
            }
        });

        searchTerm.getDocument().addDocumentListener(new DocumentListener() {

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
        });

        button.addActionListener(this);
    }

    private void enableButton() {
        button.setEnabled(false);
        if (!"".equals(searchTerm.getText()) && getSelectedSeqRuns().size() > 0) {
            button.setEnabled(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        resultList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        readList = new javax.swing.JList();
        topPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        runList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        searchTerm = new javax.swing.JTextField();
        button = new javax.swing.JButton();
        exact = new javax.swing.JCheckBox();

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList2);

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setBottomComponent(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(jPanel2);

        jSplitPane2.setDividerLocation(540);

        resultList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(resultList);

        jSplitPane2.setLeftComponent(jScrollPane3);

        readList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(readList);

        jSplitPane2.setRightComponent(jScrollPane4);

        jSplitPane1.setBottomComponent(jSplitPane2);

        runList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(runList);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.jLabel1.text")); // NOI18N

        searchTerm.setText(org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.searchTerm.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(button, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.button.text")); // NOI18N
        button.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(exact, org.openide.util.NbBundle.getMessage(SearchTopComponent.class, "SearchTopComponent.exact.text")); // NOI18N

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 230, Short.MAX_VALUE)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exact)
                            .addComponent(searchTerm, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(topPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(searchTerm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(exact)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button))
                    .addGroup(topPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(topPanel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button;
    private javax.swing.JCheckBox exact;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JList readList;
    private javax.swing.JList resultList;
    private javax.swing.JList runList;
    private javax.swing.JTextField searchTerm;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(MGXMaster.class);
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends MGXMaster> m = result.allInstances();
        boolean needUpdate = false;
        for (MGXMaster newMaster : m) {
            if (currentMaster == null || !newMaster.equals(currentMaster)) {
                currentMaster = newMaster;
                needUpdate = true;
            }
        }
        if (needUpdate) {
            updateRunList();
        }
    }

    private void updateRunList() {
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
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                super.done();
            }
        };
        worker.execute();
    }

    private List<SeqRun> getSelectedSeqRuns() {
        return runList.getSelectedValuesList();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingWorker worker = new SwingWorker<List<SearchResult>, Void>() {

            @Override
            protected List<SearchResult> doInBackground() throws Exception {
                return currentMaster.Attribute().search(getSelectedSeqRuns(), searchTerm.getText(), exact.isSelected());
            }

            @Override
            protected void done() {
                try {
                    List<SearchResult> result = get();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        worker.execute();
    }

    private final class ResultListModel extends AbstractListModel<Void> {

        @Override
        public int getSize() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Void getElementAt(int index) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private final class ReadListListCellRenderer implements ListCellRenderer<SearchResult> {

        private JLabel label = new JLabel();

        @Override
        public Component getListCellRendererComponent(JList<? extends SearchResult> list, SearchResult value, int index, boolean isSelected, boolean cellHasFocus) {
            label.setText(value.getSequenceName());
            label.setBackground(isSelected ? Color.BLUE : Color.WHITE);
            return label;
        }
    }
}
