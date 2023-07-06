package de.cebitec.mgx.gui.tableview;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.vizfilter.ExcludeFilter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serial;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.table.AbstractTableModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;

/**
 *
 * @author sjaenick
 */
class TableViewCustomizer extends javax.swing.JPanel implements VisFilterI<DistributionI<Double>, DistributionI<Double>> {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates new form TableViewCustomizer
     */
    public TableViewCustomizer() {
        initComponents();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != super.isEnabled()) {
            super.setEnabled(enabled);
            includeHeaders.setEnabled(enabled);
            fractions.setEnabled(enabled);
            treeFilter1.setEnabled(enabled);
            filterField.setEnabled(enabled);
            export.setEnabled(model != null);
        }
    }

    private AttributeTypeI at;
    private AbstractTableModel model = null;

    void setModel(AbstractTableModel m) {
        model = m;
        export.setEnabled(m != null && m.getColumnCount() > 0 && m.getRowCount() > 0);
    }

    boolean includeHeaders() {
        return includeHeaders.isSelected();
    }

    boolean useFractions() {
        return fractions.isSelected();
    }

    void kronaMode() {
        fractions.setEnabled(false);
        filterField.setText("");
        filterField.setEnabled(false);
    }

    String getMatchText() {
        return filterField.getText();
    }

    void setAttributeType(final AttributeTypeI aType) {
        if (aType == null || aType.equals(at)) {
            return;
        }
        at = aType;
        model = null;
        treeFilter1.setAttributeType(at);
    }

    @Override
    public List<Pair<GroupI, DistributionI<Double>>> filter(List<Pair<GroupI, DistributionI<Double>>> dists) {

        Set<AttributeI> filterEntries = treeFilter1.getBlackList();

        if (filterEntries == null || filterEntries.isEmpty()) {
            return dists;
        }

        ExcludeFilter<Double> ef = new ExcludeFilter<>(filterEntries);
        dists = ef.filter(dists);

        return dists;
    }

    public Set<AttributeI> getFilterEntries() {
        return treeFilter1.getBlackList();
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
        jLabel4 = new javax.swing.JLabel();
        fractions = new javax.swing.JCheckBox();
        treeFilter1 = new de.cebitec.mgx.gui.swingutils.TreeFilterUI();
        filterField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        showLineage = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(export, org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.export.text")); // NOI18N
        export.setEnabled(false);
        export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportActionPerformed(evt);
            }
        });

        includeHeaders.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        includeHeaders.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(includeHeaders, org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.includeHeaders.text")); // NOI18N

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.jLabel4.text")); // NOI18N

        fractions.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(fractions, org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.fractions.text")); // NOI18N
        fractions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fractionsActionPerformed(evt);
            }
        });

        filterField.setText(org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.filterField.text")); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.jLabel1.text")); // NOI18N

        showLineage.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(showLineage, org.openide.util.NbBundle.getMessage(TableViewCustomizer.class, "TableViewCustomizer.showLineage.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(treeFilter1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fractions)
                            .addComponent(export)
                            .addComponent(includeHeaders)
                            .addComponent(jLabel4))
                        .addContainerGap(39, Short.MAX_VALUE))
                    .addComponent(filterField)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(showLineage))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(includeHeaders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fractions)
                .addGap(5, 5, 5)
                .addComponent(showLineage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(treeFilter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addComponent(export)
                .addContainerGap())
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
                    w.newLine();
                    w.newLine();
                }

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

    private void fractionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fractionsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fractionsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton export;
    private javax.swing.JTextField filterField;
    private javax.swing.JCheckBox fractions;
    private javax.swing.JCheckBox includeHeaders;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JCheckBox showLineage;
    private de.cebitec.mgx.gui.swingutils.TreeFilterUI treeFilter1;
    // End of variables declaration//GEN-END:variables

    boolean showLineage() {
        return showLineage.isEnabled() && showLineage.isSelected();
    }

    void enableLineageSelection(boolean enable) {
        showLineage.setEnabled(enable);
    }

}
