/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.rarefaction.plot;

/**
 *
 * @author sj
 */
public class RarefactionCustomizer extends javax.swing.JPanel {

    /**
     * Creates new form RarefactionCustomizer
     */
    public RarefactionCustomizer() {
        initComponents();
    }

    int getNumberRepetitions() {
        int ret = numIterations.getValue();
        return ret == 0 ? 1 : ret; //at least one round required
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        numIterations = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();

        numIterations.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        numIterations.setMajorTickSpacing(50);
        numIterations.setMaximum(150);
        numIterations.setMinorTickSpacing(5);
        numIterations.setPaintLabels(true);
        numIterations.setPaintTicks(true);
        numIterations.setSnapToTicks(true);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(RarefactionCustomizer.class, "RarefactionCustomizer.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(numIterations, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(57, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numIterations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(212, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSlider numIterations;
    // End of variables declaration//GEN-END:variables
}
