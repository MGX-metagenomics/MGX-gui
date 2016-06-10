package de.cebitec.mgx.gui.goldstandard.ui.charts;

import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.gui.goldstandard.ui.charts.GSComparisonViewer.ComparisonViews;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.table.AbstractTableModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author pblumenk
 */
public class GSComparisonViewCustomizer extends javax.swing.JPanel implements PropertyChangeListener {

    /**
     * Creates new form TableViewCustomizer
     */
    public GSComparisonViewCustomizer() {
        initComponents();
        viewTypeBox.setModel(new DefaultComboBoxModel<>(ComparisonViews.values()));
        viewTypeBox.setSelectedIndex(0);
    }

    private AttributeTypeI at;
    private AbstractTableModel model = null;

    public void setModel(AbstractTableModel m) {
        model = m;
        export.setEnabled(m != null && m.getColumnCount() > 0 && m.getRowCount() > 0);
    }

    public boolean includeHeaders() {
        return includeHeaders.isSelected();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        export = new javax.swing.JButton();
        includeHeaders = new javax.swing.JCheckBox();
        viewTypeBox = new javax.swing.JComboBox<>();

        org.openide.awt.Mnemonics.setLocalizedText(export, "Export TSV");
        export.setEnabled(false);
        export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportActionPerformed(evt);
            }
        });

        includeHeaders.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        includeHeaders.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(includeHeaders, "Include column headers");
        includeHeaders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                includeHeadersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(export)
                            .addComponent(includeHeaders))
                        .addContainerGap(94, Short.MAX_VALUE))
                    .addComponent(viewTypeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(viewTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(includeHeaders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 271, Short.MAX_VALUE)
                .addComponent(export))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportActionPerformed
        final JFileChooser fc = new JFileChooser();

        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory()) {
                fc.setCurrentDirectory(f);
            }
        }

        File f = new File("MGX_export.tsv");
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
                BufferedWriter w = new BufferedWriter(new FileWriter(f));

                if (includeHeaders()) {
                    for (int col = 0; col < model.getColumnCount() - 1; col++) {
                        w.write(model.getColumnName(col));
                        w.write("\t");
                    }
                    w.write(model.getColumnName(model.getColumnCount() - 1));
                    w.write(System.lineSeparator());
                    w.write(System.lineSeparator());
                }

                // export data
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col <= model.getColumnCount() - 1; col++) {
                        Object val = model.getValueAt(row, col);
                        if (val != null) {
                            w.write(val.toString());
                        }
                        if (col <= model.getColumnCount() - 2 && model.getValueAt(row, col + 1) != null) {
                            w.write("\t");
                        }
                    }
//                    Object val = model.getValueAt(row, model.getColumnCount() - 1);
//                    val = val != null ? val : "";
                    //                  w.write(val.toString());
                    w.write(System.lineSeparator());
                }
                w.flush();
                w.close();

                // report success
                NotifyDescriptor nd = new NotifyDescriptor.Message("Data exported to " + f.getName());
                DialogDisplayer.getDefault().notify(nd);
            } catch (IOException ex) {
                // some error occured, notify user
                NotifyDescriptor nd = new NotifyDescriptor("Export failed: " + ex.getMessage(), "Error",
                        NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
                DialogDisplayer.getDefault().notify(nd);
            }
        }
    }//GEN-LAST:event_exportActionPerformed

    private void includeHeadersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_includeHeadersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_includeHeadersActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton export;
    private javax.swing.JCheckBox includeHeaders;
    private javax.swing.JComboBox<ComparisonViews> viewTypeBox;
    // End of variables declaration//GEN-END:variables

    private class InvisibleRoot extends AbstractNode {

        public InvisibleRoot(Children children, Lookup lookup) {
            super(children, lookup);
        }

        public InvisibleRoot(Children children) {
            super(children);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {}
    
    public ComparisonViews getCurrentComparisonView(){
        return (ComparisonViews)viewTypeBox.getSelectedItem();
    }

}
