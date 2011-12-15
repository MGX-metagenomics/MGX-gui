package de.cebitec.mgx.gui.wizard.seqrun;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JPanel;

public final class SeqRunVisualPanel1 extends JPanel {

    /** Creates new form SeqRunVisualPanel1 */
    public SeqRunVisualPanel1() {
        initComponents();
        submitted.addItemListener(new CheckBoxListener());
    }

    @Override
    public String getName() {
        return "Step #1";
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        platform = new javax.swing.JComboBox();
        method = new javax.swing.JComboBox();
        submitted = new javax.swing.JCheckBox();
        accession = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.jLabel2.text")); // NOI18N

        platform.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        method.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(submitted, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.submitted.text")); // NOI18N

        accession.setEditable(false);
        accession.setText(org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.accession.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SeqRunVisualPanel1.class, "SeqRunVisualPanel1.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(submitted))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(method, 0, 191, Short.MAX_VALUE)
                            .addComponent(platform, 0, 191, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(accession, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(platform, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(method, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addComponent(submitted)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(accession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField accession;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox method;
    private javax.swing.JComboBox platform;
    private javax.swing.JCheckBox submitted;
    // End of variables declaration//GEN-END:variables

    private final class CheckBoxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent ie) {
            if (ie.getStateChange() == ItemEvent.DESELECTED) {
                accession.setEnabled(false);
                accession.setText("");
            } else {
                accession.setEnabled(true);
            }
        }
    }
}
