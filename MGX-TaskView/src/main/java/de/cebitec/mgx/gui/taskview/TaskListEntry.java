/*
 * TaskListEntry.java
 *
 * Created on Dec 21, 2011, 9:44:45 PM
 */
package de.cebitec.mgx.gui.taskview;

import java.awt.BorderLayout;
import javax.swing.JProgressBar;

/**
 *
 * @author sj
 */
public class TaskListEntry extends javax.swing.JPanel {

    /**
     * Creates new form TaskListEntry
     */
    public TaskListEntry() {
        initComponents();
    }

    public void setMainText(String t) {
        maintext.setText(t);
    }

    public void setDetailText(String t) {
        detailtext.setText(t);
    }

    public void finished() {
        jProgressBar1.setIndeterminate(false);
        jProgressBar1.setValue(jProgressBar1.getMaximum());
        imagepanel.add(new ImagePanel("de/cebitec/mgx/gui/taskview/ok.png"), BorderLayout.CENTER);
    }

    public JProgressBar getProgressBar() {
        return jProgressBar1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        maintext = new javax.swing.JLabel();
        detailtext = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        imagepanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        maintext.setText(org.openide.util.NbBundle.getMessage(TaskListEntry.class, "TaskListEntry.maintext.text")); // NOI18N

        detailtext.setFont(new java.awt.Font("Dialog", 0, 10));
        detailtext.setText(org.openide.util.NbBundle.getMessage(TaskListEntry.class, "TaskListEntry.detailtext.text")); // NOI18N

        jProgressBar1.setIndeterminate(true);

        imagepanel.setMaximumSize(new java.awt.Dimension(48, 48));
        imagepanel.setMinimumSize(new java.awt.Dimension(48, 48));
        imagepanel.setPreferredSize(new java.awt.Dimension(48, 48));
        imagepanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maintext, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                            .addComponent(detailtext, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imagepanel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(maintext)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(detailtext))
                    .addComponent(imagepanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel detailtext;
    private javax.swing.JPanel imagepanel;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel maintext;
    // End of variables declaration//GEN-END:variables
}
