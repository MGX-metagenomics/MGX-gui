package de.cebitec.mgx.gui.goldstandard.ui.charts.gscomparison;

/**
 *
 * @author pblumenk
 */
public class GSCQuantificationAccuracyViewCustomizer extends javax.swing.JPanel {

    private GSCPerformanceMetricsTableModel model;

    /**
     * Creates new form TableViewCustomizer
     */
    public GSCQuantificationAccuracyViewCustomizer() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logAxis = new javax.swing.JCheckBox();

        logAxis.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(logAxis, "Logarithmic axes");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logAxis)
                .addContainerGap(163, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logAxis)
                .addContainerGap(331, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox logAxis;
    // End of variables declaration//GEN-END:variables

    public void dispose() {
        model = null;
    }

    boolean useLogAxis() {
        return logAxis.isSelected();
    }
}
