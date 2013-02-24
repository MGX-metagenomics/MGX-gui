package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.gui.wizard.analysis.misc.ValueHolderI;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

public final class AnalysisVisualPanel2 extends JPanel implements PropertyChangeListener {

    /**
     * Creates new form AnalysisVisualPanel2
     */
    public AnalysisVisualPanel2() {
        initComponents();
    }

    @Override
    public String getName() {
        return "Job parameters";
    }

    public void setTitle(String title) {
        setBorder(javax.swing.BorderFactory.createTitledBorder(null, title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Dialog", 1, 12)));
    }

    public void setDescription(String desc) {
        description.setText(desc);
    }

    public void setOptional(boolean optional) {
        if (optional) {
            requiredOptional.setText("Optional parameter:");
        } else {
            requiredOptional.setText("Required parameter:");
        }
    }
    
    public void setInputComponent(ValueHolderI vh) {
        if (valueholder != null) {
            valuePanel.remove(valueholder);
            valueholder.removePropertyChangeListener(this);
        }
        valueholder = vh;
        vh.addPropertyChangeListener(this);
        valuePanel.add(vh, BorderLayout.CENTER);
    }
    
    public String getValue() {
        return valueholder.getValue();
    }
    
    private ValueHolderI valueholder = null;

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        valuePanel = new javax.swing.JPanel();
        requiredOptional = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "title", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        description.setEditable(false);
        description.setColumns(20);
        description.setRows(5);
        jScrollPane1.setViewportView(description);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AnalysisVisualPanel2.class, "AnalysisVisualPanel2.jLabel1.text")); // NOI18N

        valuePanel.setLayout(new java.awt.BorderLayout());

        requiredOptional.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(requiredOptional, org.openide.util.NbBundle.getMessage(AnalysisVisualPanel2.class, "AnalysisVisualPanel2.requiredOptional.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addComponent(valuePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(requiredOptional))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(requiredOptional)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valuePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea description;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel requiredOptional;
    private javax.swing.JPanel valuePanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChange("updated", evt.getOldValue(), evt.getNewValue());
    }
}