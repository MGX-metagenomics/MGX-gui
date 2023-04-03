/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.treeview;

import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import java.io.Serial;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class TreeViewCustomizer extends javax.swing.JPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates new form TreeViewCustomizer
     */
    public TreeViewCustomizer() {
        initComponents();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != super.isEnabled()) {
            super.setEnabled(enabled);
            showUnclassified.setEnabled(enabled);
            hideTitle.setEnabled(enabled);
            treeFilter.setEnabled(enabled);
        }
    }

    boolean includeUnclassified() {
        return showUnclassified.isSelected();
    }

    boolean hideTitle() {
        return hideTitle.isSelected();
    }

    public Set<AttributeI> getBlackList() {
        return treeFilter.getBlackList();
    }

    private AttributeTypeI at;

    public void setAttributeType(final AttributeTypeI aType) {
        if (aType.equals(at)) {
            return;
        }
        at = aType;
        treeFilter.setAttributeType(at);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        showUnclassified = new javax.swing.JCheckBox();
        hideTitle = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        treeFilter = new de.cebitec.mgx.gui.swingutils.TreeFilterUI();

        showUnclassified.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(showUnclassified, org.openide.util.NbBundle.getMessage(TreeViewCustomizer.class, "TreeViewCustomizer.showUnclassified.text")); // NOI18N

        hideTitle.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(hideTitle, org.openide.util.NbBundle.getMessage(TreeViewCustomizer.class, "TreeViewCustomizer.hideTitle.text")); // NOI18N

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(TreeViewCustomizer.class, "TreeViewCustomizer.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showUnclassified)
                    .addComponent(hideTitle))
                .addContainerGap(16, Short.MAX_VALUE))
            .addComponent(treeFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showUnclassified)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hideTitle)
                .addGap(64, 64, 64)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(treeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox hideTitle;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JCheckBox showUnclassified;
    private de.cebitec.mgx.gui.swingutils.TreeFilterUI treeFilter;
    // End of variables declaration//GEN-END:variables
}
