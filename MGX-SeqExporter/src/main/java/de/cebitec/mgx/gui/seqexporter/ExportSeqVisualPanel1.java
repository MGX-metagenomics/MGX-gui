package de.cebitec.mgx.gui.seqexporter;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.swingutils.JCheckBoxList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

public final class ExportSeqVisualPanel1<T extends Number> extends JPanel implements PropertyChangeListener {

    private final JCheckBoxList<AttributeI> list = new JCheckBoxList<>();
    private boolean all_selected = true;

    /**
     * Creates new form ExportSeqVisualPanel1
     */
    public ExportSeqVisualPanel1() {
        initComponents();
        scrollpane.setViewportView(list);
        list.addPropertyChangeListener(this);
    }

    public void setDistribution(DistributionI<T> d) {
        List<AttributeI> elements = new ArrayList<>();
        elements.addAll(d.keySet());
        Collections.sort(elements);
        list.clear();
        for (AttributeI attr : elements) {
            list.addElement(attr);
        }
        jButton1ActionPerformed(null);
    }

    public Set<AttributeI> getSelectedAttributes() {
        return list.getSelectedEntries();
    }

    @Override
    public String getName() {
        return "Select attributes";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        scrollpane = new javax.swing.JScrollPane();
        jButton1 = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ExportSeqVisualPanel1.class, "ExportSeqVisualPanel1.jLabel1.text")); // NOI18N

        jButton1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(ExportSeqVisualPanel1.class, "ExportSeqVisualPanel1.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(scrollpane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (all_selected) {
            list.deselectAll();
            all_selected = false;
            jButton1.setText("Select all");
        } else {
            list.selectAll();
            all_selected = true;
            jButton1.setText("Deselect all");
        }
        validateInput();
        repaint();
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane scrollpane;
    // End of variables declaration//GEN-END:variables

    private void validateInput() {
        firePropertyChange("REVALIDATE", 0, 1);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        validateInput();
    }
}