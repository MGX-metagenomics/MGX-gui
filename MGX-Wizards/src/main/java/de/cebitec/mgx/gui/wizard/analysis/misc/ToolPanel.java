
package de.cebitec.mgx.gui.wizard.analysis.misc;


/**
 *
 * @author sjaenick
 */
public class ToolPanel extends javax.swing.JPanel {

    /**
     * Creates new form ToolPanel
     */
    public ToolPanel() {
        initComponents();
    }
    
    public void setToolName(String toolName) {
        name.setText(toolName);
    }
    
    public void setAuthor(String toolAuthor) {
        author.setText(toolAuthor);
    }
    
    public void setDescription(String toolDesc) {
        description.setText(toolDesc);
    }
    
    public void setVersion(String toolVersion) {
        version.setText(toolVersion);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        name = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        author = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        version = new javax.swing.JLabel();

        name.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(name, org.openide.util.NbBundle.getMessage(ToolPanel.class, "ToolPanel.name.text")); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ToolPanel.class, "ToolPanel.jLabel1.text")); // NOI18N

        description.setEditable(false);
        description.setColumns(20);
        description.setRows(5);
        jScrollPane1.setViewportView(description);

        author.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(author, org.openide.util.NbBundle.getMessage(ToolPanel.class, "ToolPanel.author.text")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ToolPanel.class, "ToolPanel.jLabel2.text")); // NOI18N

        version.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(version, org.openide.util.NbBundle.getMessage(ToolPanel.class, "ToolPanel.version.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(author, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(version, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(name)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(author))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(version)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel author;
    private javax.swing.JTextArea description;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel name;
    private javax.swing.JLabel version;
    // End of variables declaration//GEN-END:variables
}
