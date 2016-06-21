package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;

public final class TimeEvalJobVisualPanel1 extends JPanel {

    public static final String PROP_JOBS = "job";    

    /**
     * Creates new form SelectSingleJobVisualPanel1
     */
    public TimeEvalJobVisualPanel1(Map<JobI, Collection<AttributeTypeI>> jobs) {
        initComponents();
        DefaultListModel<JobI> jobModel = new DefaultListModel<>();
        for (JobI job : jobs.keySet()) {
            jobModel.addElement(job);
        }
        jobList.setModel(jobModel);
        jobList.setCellRenderer(new JobRenderer());        

        clearSelections();
    }

    public void addListSelectionListener(ListSelectionListener lsl) {
        jobList.getSelectionModel().addListSelectionListener(lsl);
    }

    public List<JobI> getSelectedJobs() {
        return jobList.getSelectedValuesList();
    }

    public void clearSelections() {
        jobList.getSelectionModel().clearSelection();
    }

    @Override
    public String getName() {
        return "Step #1";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jobList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();

        jScrollPane1.setViewportView(jobList);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TimeEvalJobVisualPanel1.class, "SelectMultipleJobVisualPanel1.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<JobI> jobList;
    // End of variables declaration//GEN-END:variables

}