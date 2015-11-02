package de.cebitec.mgx.gui.wizard.analysis.misc;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.swingutils.util.ServerFS;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author sjaenick
 */
public class FileChooserPanel extends ValueHolderI {

    private final MGXMasterI master;
    private String fileName = null;

    /**
     * Creates new form FileChooserPanel
     */
    public FileChooserPanel(MGXMasterI master) {
        this.master = master;
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

        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        jTextField1.setEditable(false);
        jTextField1.setText(org.openide.util.NbBundle.getMessage(FileChooserPanel.class, "FileChooserPanel.jTextField1.text")); // NOI18N

        jButton1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(FileChooserPanel.class, "FileChooserPanel.jButton1.text")); // NOI18N
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
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton1))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser(new ServerFS(master));
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        } else {
            final File target = chooser.getSelectedFile();
            fileName = target.getPath();
            String displayName = fileName;
            if (displayName.startsWith(MGXFileI.ROOT_PATH + MGXFileI.separator)) {
                displayName = displayName.substring(2);
            }
            displayName = displayName.replace(MGXFileI.separator, File.separator);
            jTextField1.setText(displayName);
            
            fileName = fileName.replace(File.separator, MGXFileI.separator);
            firePropertyChange("input", "", fileName);
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getValue() {
        return fileName;
    }

    @Override
    public void setValue(String value) {
        jTextField1.setText(value);
    }
}
