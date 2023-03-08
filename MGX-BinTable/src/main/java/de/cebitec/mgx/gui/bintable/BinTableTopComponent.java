package de.cebitec.mgx.gui.bintable;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.api.model.assembly.BinI;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.ToolTipManager;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
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
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//de.cebitec.mgx.gui.bintable//BinTable//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "BinTableTopComponent",
        iconBase = "de/cebitec/mgx/gui/bintable/bintable.svg",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.bintable.BinTableTopComponent")

@ActionReferences({
    @ActionReference(path = "Menu/Window", position = 341)
})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BinTableAction",
        preferredID = "BinTableTopComponent"
)
@Messages({
    "CTL_BinTableAction=Bin Table",
    "CTL_BinTableTopComponent=Bin Table",
    "HINT_BinTableTopComponent=Bin Table"
})
public final class BinTableTopComponent extends TopComponent implements LookupListener {

    private final Lookup.Result<BinI> binResult;
    private final Lookup.Result<AssemblyI> asmResult;
    private final BinTableModel model = new BinTableModel();

    private boolean isActivated = false;

    public BinTableTopComponent() {
        initComponents();
        setName(Bundle.CTL_BinTableTopComponent());
        setToolTipText(Bundle.HINT_BinTableTopComponent());
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);

        binResult = Utilities.actionsGlobalContext().lookupResult(BinI.class);
        asmResult = Utilities.actionsGlobalContext().lookupResult(AssemblyI.class);

        jXTable1.setModel(model);
        jXTable1.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});

        jXTable1.getColumn(0).setMaxWidth(95); //name
        jXTable1.getColumn(1).setWidth(130); // taxonomy
        jXTable1.getColumn(2).setMaxWidth(100); // #contigs
        jXTable1.getColumn(3).setMaxWidth(100); // assembled bp
        jXTable1.getColumn(4).setMaxWidth(75); // n50
        jXTable1.getColumn(5).setMaxWidth(80); // cds
        jXTable1.getColumn(6).setMaxWidth(100); // completeness
        jXTable1.getColumn(7).setMaxWidth(100); // contamination

    }

    @Override
    public Image getIcon() {
        Image image = super.getIcon();
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    @Override
    public void resultChanged(LookupEvent le) {
        // no update when component itself is activate
        if (isActivated) {
            return;
        }

        model.clear();
        model.addAll(binResult.allInstances());

        for (AssemblyI asm : asmResult.allInstances()) {
            List<BinI> additional = new ArrayList<>();
            MGXMasterI master = asm.getMaster();
            try {
                Iterator<BinI> iter = master.Bin().ByAssembly(asm);
                while (iter.hasNext()) {
                    BinI bin = iter.next();
                    additional.add(bin);
                }
            } catch (MGXException ex) {
                model.clear();
                saveTSV.setEnabled(false);
                Exceptions.printStackTrace(ex);
                return;
            }
            Collections.sort(additional, new Comparator<BinI>() {
                @Override
                public int compare(BinI o1, BinI o2) {
                    return Long.compare(o1.getId(), o2.getId());
                }

            });
            model.addAll(additional);
        }

        saveTSV.setEnabled(model.getRowCount() > 0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        saveTSV = new javax.swing.JButton();

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jXTable1);

        org.openide.awt.Mnemonics.setLocalizedText(saveTSV, org.openide.util.NbBundle.getMessage(BinTableTopComponent.class, "BinTableTopComponent.saveTSV.text")); // NOI18N
        saveTSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTSVActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveTSV)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveTSV)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void saveTSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTSVActionPerformed

        final JFileChooser fc = new JFileChooser();

        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory()) {
                fc.setCurrentDirectory(f);
            }
        }

        File f = new File("MGX_bintable.tsv");
        fc.setSelectedFile(f);
        fc.setVisible(true);

        fc.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("SelectedFileChangedProperty".equals(evt.getPropertyName())) {
                    NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fc.getCurrentDirectory().getAbsolutePath());
                }
            }
        });

        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            f = fc.getSelectedFile();
            try {
                if (f.exists()) {
                    throw new IOException(f.getName() + " already exists.");
                }

                try ( BufferedWriter w = new BufferedWriter(new FileWriter(f))) {
                    // write the header line
                    for (int col = 0; col < model.getColumnCount() - 1; col++) {
                        w.write(model.getColumnName(col));
                        w.write("\t");
                    }
                    w.write(model.getColumnName(model.getColumnCount() - 1));
                    w.newLine();

                    DecimalFormat FMT = new DecimalFormat("#.######");

                    // export data
                    for (int row = 0; row < model.getRowCount(); row++) {
                        for (int col = 0; col <= model.getColumnCount() - 1; col++) {
                            Object value = model.getValueAt(row, col);
                            if (value != null) {
                                if (value instanceof Double) {
                                    value = FMT.format((Double) value);
                                }
                                w.write(value.toString());
                            }
                            if (col <= model.getColumnCount() - 2 && model.getValueAt(row, col + 1) != null) {
                                w.write("\t");
                            }
                        }
                        w.write(System.lineSeparator());
                    }
                    w.flush();
                }

            } catch (IOException ex) {
                // some error occured, notify user
                NotifyDescriptor nd = new NotifyDescriptor("Export failed: " + ex.getMessage(), "Error",
                        NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
                DialogDisplayer.getDefault().notify(nd);
            }

            // report success
            NotifyDescriptor nd = new NotifyDescriptor.Message("Data exported to " + f.getName());
            DialogDisplayer.getDefault().notify(nd);
        }

    }//GEN-LAST:event_saveTSVActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JButton saveTSV;
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
}
