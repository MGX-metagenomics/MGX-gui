/*
 * AmpliconDetailPanel.java
 *
 * Created on 13.12.2011, 12:07:02
 */
package de.cebitec.mgx.gui.wizard.extract;

import java.io.Serial;
import javax.swing.JTextField;

/**
 *
 * @author sjaenick
 */
public class AmpliconDetailPanel extends javax.swing.JPanel {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates new form AmpliconDetailPanel
     */
    public AmpliconDetailPanel() {
        super();
        initComponents();
        super.setForeground(new java.awt.Color(51, 51, 51));
        super.setBackground(new java.awt.Color(238, 238, 238));
    }

    public JTextField getFiveprimer() {
        return fiveprimer;
    }

    public JTextField getTargetfragment() {
        return targetfragment;
    }

    public JTextField getTargetgene() {
        return targetgene;
    }

    public JTextField getThreeprimer() {
        return threeprimer;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fiveprimer = new javax.swing.JTextField();
        threeprimer = new javax.swing.JTextField();
        targetgene = new javax.swing.JTextField();
        targetfragment = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(AmpliconDetailPanel.class, "AmpliconDetailPanel.border.title"))); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(AmpliconDetailPanel.class, "AmpliconDetailPanel.jLabel3.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(AmpliconDetailPanel.class, "AmpliconDetailPanel.jLabel4.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(AmpliconDetailPanel.class, "AmpliconDetailPanel.jLabel5.text")); // NOI18N

        jLabel6.setText(org.openide.util.NbBundle.getMessage(AmpliconDetailPanel.class, "AmpliconDetailPanel.jLabel6.text")); // NOI18N

        fiveprimer.setText(org.openide.util.NbBundle.getMessage(AmpliconDetailPanel.class, "AmpliconDetailPanel.fiveprimer.text")); // NOI18N

        threeprimer.setText(org.openide.util.NbBundle.getMessage(AmpliconDetailPanel.class, "AmpliconDetailPanel.threeprimer.text")); // NOI18N

        targetgene.setText(org.openide.util.NbBundle.getMessage(AmpliconDetailPanel.class, "AmpliconDetailPanel.targetgene.text")); // NOI18N

        targetfragment.setText(org.openide.util.NbBundle.getMessage(AmpliconDetailPanel.class, "AmpliconDetailPanel.targetfragment.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fiveprimer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                    .addComponent(threeprimer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                    .addComponent(targetgene, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                    .addComponent(targetfragment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fiveprimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(threeprimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(targetgene, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetfragment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(21, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fiveprimer;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField targetfragment;
    private javax.swing.JTextField targetgene;
    private javax.swing.JTextField threeprimer;
    // End of variables declaration//GEN-END:variables
}
