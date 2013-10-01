
package de.cebitec.mgx.gui.wizard.reference;

import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class InstallReferenceVisualPanel1 extends JPanel implements ListSelectionListener {

    /**
     * Creates new form InstallReferenceVisualPanel1
     */
    public InstallReferenceVisualPanel1() {
        initComponents();
        jList1.addListSelectionListener(this);
    }

    @Override
    public String getName() {
        return "Step #1";
    }
    
    public Reference getReference() {
        return jList1.getSelectedValue();
    }
    
    public void setReferences(Set<Reference> refs) {
        jList1.setListData(refs.toArray(new Reference[]{}));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<Reference>();

        jList1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<Reference> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(ListSelectionEvent e) {
        firePropertyChange("refSelected", null, jList1.getSelectedValue());
    }
}
