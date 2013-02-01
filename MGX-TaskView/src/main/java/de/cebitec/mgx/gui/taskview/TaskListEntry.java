/*
 * TaskListEntry.java
 *
 * Created on Dec 21, 2011, 9:44:45 PM
 */
package de.cebitec.mgx.gui.taskview;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JProgressBar;

/**
 *
 * @author sj
 */
public class TaskListEntry extends javax.swing.JPanel implements PropertyChangeListener {

    /**
     * Creates new form TaskListEntry
     */
//    private TaskListEntry() {
//        initComponents();
//    }
    private final MGXTask task;

    public TaskListEntry(MGXTask task) {
        this.task = task;
        initComponents();
        setMainText(task.getName());
        setDetailText(task.getStatus());
        task.addPropertyChangeListener(this);
        jProgressBar1.setIndeterminate(!task.isDeterminate());
        if (task.isDeterminate()) {
            jProgressBar1.setMinimum(0);
            jProgressBar1.setMaximum(100);
            jProgressBar1.setValue(task.getProgress());
        }
    }

    private void setMainText(String t) {
        maintext.setText(t);
        repaint();
    }

    private void setDetailText(String t) {
        detailtext.setText(t);
        repaint();
    }
    private ImagePanel ip = new ImagePanel();

    private void finished() {
        setDetailText(task.getStatus());
        task.removePropertyChangeListener(this);
        jProgressBar1.setIndeterminate(false);
        jProgressBar1.setValue(jProgressBar1.getMaximum());
        imagepanel.add(ip, BorderLayout.CENTER);
        ip.setImage("de/cebitec/mgx/gui/taskview/ok.png");
        revalidate();
        repaint();
    }

    private void failed() {
        setDetailText(task.getStatus());
        task.removePropertyChangeListener(this);
        jProgressBar1.setIndeterminate(false);
        jProgressBar1.setValue(jProgressBar1.getMinimum());
        imagepanel.add(ip, BorderLayout.CENTER);
        ip.setImage("de/cebitec/mgx/gui/taskview/fail.png");
        revalidate();
        repaint();
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
        maintext.setMaximumSize(new java.awt.Dimension(5000, 15));
        maintext.setMinimumSize(new java.awt.Dimension(25, 15));

        detailtext.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        detailtext.setText(org.openide.util.NbBundle.getMessage(TaskListEntry.class, "TaskListEntry.detailtext.text")); // NOI18N
        detailtext.setMaximumSize(new java.awt.Dimension(5000, 13));
        detailtext.setMinimumSize(new java.awt.Dimension(25, 13));

        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setPreferredSize(new java.awt.Dimension(50, 14));

        imagepanel.setMaximumSize(new java.awt.Dimension(48, 48));
        imagepanel.setMinimumSize(new java.awt.Dimension(48, 48));
        imagepanel.setPreferredSize(new java.awt.Dimension(48, 48));
        imagepanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maintext, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                            .addComponent(detailtext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imagepanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(maintext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(detailtext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    @Override
    public void propertyChange(final PropertyChangeEvent pce) {
        switch (pce.getPropertyName()) {
            case MGXTask.TASK_CHANGED:
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setDetailText(task.getStatus());
                        if (task.isDeterminate()) {
                            jProgressBar1.setValue(task.getProgress());
                        }
                    }
                });
                break;
            case MGXTask.TASK_FINISHED:
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        finished();
                    }
                });
                break;
            case MGXTask.TASK_FAILED:
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        failed();
                    }
                });
                break;
            default:
                System.err.println("unhandled event in TaskListEntry: " + pce.getPropertyName());
                assert false;
                break;
        }
    }
}
