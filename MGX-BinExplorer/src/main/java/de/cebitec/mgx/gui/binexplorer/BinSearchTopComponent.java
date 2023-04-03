/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/templateTopComponent637.java to edit this template
 */
package de.cebitec.mgx.gui.binexplorer;

import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.api.model.assembly.BinSearchResultI;
import de.cebitec.mgx.gui.binexplorer.util.BinSearchTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.Collections;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.binexplorer//BinSearch//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "BinSearchTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "satellite", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.binexplorer.BinSearchTopComponent")
@ActionReference(path = "Menu/Window", position = 341)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BinSearchAction",
        preferredID = "BinSearchTopComponent"
)
@Messages({
    "CTL_BinSearchAction=Bin Search",
    "CTL_BinSearchTopComponent=Bin Search",
    "HINT_BinSearchTopComponent=Search gene annotations"
})
public final class BinSearchTopComponent extends TopComponent implements LookupListener, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup.Result<BinI> binResult;
    private BinI currentBin = null;
    private boolean isActivated = false;
    private final BinSearchTableModel tableModel = new BinSearchTableModel();
    //
    private final InstanceContent content = new InstanceContent();

    public BinSearchTopComponent() {
        initComponents();
        setName(Bundle.CTL_BinSearchTopComponent());
        setToolTipText(Bundle.HINT_BinSearchTopComponent());

        binResult = Utilities.actionsGlobalContext().lookupResult(BinI.class);

        Lookup lookup = new AbstractLookup(content);
        associateLookup(lookup);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                String searchText = searchField.getText();
                if (searchText.length() >= 2) {
                    tableModel.update(currentBin, searchText);
                } else {
                    tableModel.clear();
                }
            }

        });

        jXTable1.getColumn(0).setPreferredWidth(90);
        jXTable1.getColumn(1).setPreferredWidth(90);
        jXTable1.getColumn(2).setPreferredWidth(90);
        jXTable1.getColumn(3).setPreferredWidth(140);

        jXTable1.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});
        jXTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel selectionModel = jXTable1.getSelectionModel();

        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                int rowIdx = jXTable1.getSelectedRow();
                if (rowIdx != -1) {
                    BinSearchResultI value = tableModel.getValue(rowIdx);
                    content.set(Collections.emptyList(), null);
                    if (value != null) {
                        content.add(value);
                    }
                }
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

        searchField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jLabel1 = new javax.swing.JLabel();

        searchField.setText(org.openide.util.NbBundle.getMessage(BinSearchTopComponent.class, "BinSearchTopComponent.searchField.text")); // NOI18N

        jXTable1.setModel(tableModel);
        jScrollPane1.setViewportView(jXTable1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(BinSearchTopComponent.class, "BinSearchTopComponent.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        binResult.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public void componentClosed() {
        binResult.removeLookupListener(this);
    }

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

    @Override
    public void resultChanged(LookupEvent le) {
        // avoid update when component is activated
        if (isActivated && currentBin != null) {
            return;
        }

        BinI newBin = null;
        for (BinI bin : binResult.allInstances()) {
            newBin = bin;
        }
        if (newBin != null && !newBin.equals(currentBin)) {

            if (currentBin != null) {
                currentBin.removePropertyChangeListener(this);
            }
            currentBin = newBin;
            currentBin.addPropertyChangeListener(this);

            tableModel.clear();

            // repeat search for new bin
            String searchText = searchField.getText();
            if (searchText.length() >= 2) {
                tableModel.update(currentBin, searchText);
            }

            repaint();
        }

    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof BinI && evt.getPropertyName().equals(ModelBaseI.OBJECT_DELETED)) {
            BinI bin = (BinI) evt.getSource();
            if (currentBin.equals(bin)) {
                currentBin.removePropertyChangeListener(this);
                currentBin = null;

                tableModel.clear();

            }
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
}
