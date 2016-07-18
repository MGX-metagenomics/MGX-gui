package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;

public final class SelectSingleJobWithGSVisualPanel1 extends JPanel {

    public static final String PROP_JOB = "job";
    public static final String PROP_ATTRIBUTETYPE = "attribute_type";
    public static final String PROP_GOLDSTANDARD = "goldstandard";

    Collection<JobI> jobs;
    DefaultListModel<JobI> jobModel;

    /**
     * Creates new form SelectSingleJobVisualPanel1
     */
    public SelectSingleJobWithGSVisualPanel1(Map<JobI, Collection<AttributeTypeI>> jobs) {
        initComponents();
        this.jobs = jobs.keySet();

        jobModel = new DefaultListModel<>();
//        for (JobI job : this.jobs) {
//            jobModel.addElement(job);
//        }
        jobList.setModel(jobModel);
        jobList.setCellRenderer(new JobRenderer());

        JobI gsJob = jobs.keySet().iterator().next();
        for (JobI job : this.jobs) {
            gsAttributeBox.addItem(job);
            if (job.getTool().getName().equals(AddGoldstandard.TOOL_NAME)) {
                gsJob = job;
            }
        }
        gsAttributeBox.setRenderer(new JobRenderer());
        if (gsAttributeBox.getItemCount() > 0) {
            gsAttributeBox.setSelectedItem(gsJob);            
            gsAttributeBox.setEnabled(true);
        }

        attributeTypeBox.setRenderer(new AttributeTypeRenderer());

        clearSelections();
    }

    public void addListSelectionListener(ListSelectionListener lsl) {
        jobList.getSelectionModel().addListSelectionListener(lsl);
    }

    public void addGSComboBoxSelectionListener(ActionListener al) {
        gsAttributeBox.addActionListener(al);
    }

    public void removeListSelectionListener(ListSelectionListener lsl) {
        jobList.getSelectionModel().removeListSelectionListener(lsl);
    }

    public void removeGSComboBoxSelectionListener(ActionListener al) {
        gsAttributeBox.removeActionListener(al);
    }

    public void setJobList(Collection<JobI> jobs) {
        jobModel.clear();
        for (JobI job : jobs) {
            jobModel.addElement(job);
        }
    }

    public JobI getSelectedJob() {
        return jobList.getSelectedValue();
    }

    public AttributeTypeI getSelectedAttributeType() {
        return (AttributeTypeI) attributeTypeBox.getSelectedItem();
    }

    public int getAttributeTypeListCount() {
        return attributeTypeBox.getItemCount();
    }

    public JobI getGoldstandard() {
        return (JobI) gsAttributeBox.getSelectedItem();
    }

    public void setAttributeTypeList(Collection<AttributeTypeI> attrTypes) {
        attributeTypeBox.removeAllItems();
        for (AttributeTypeI at : attrTypes) {
            attributeTypeBox.addItem(at);
        }
        if (!attrTypes.isEmpty()) {
            attributeTypeBox.setSelectedIndex(0);
        } else {
            attributeTypeBox.setSelectedIndex(-1);
        }
    }
    
    public void deactivateAttributeTypeList(){
        attributeTypeBox.removeAllItems();
        attributeTypeBox.setEnabled(false);
    }

    public void enableAttributeTypeBox(boolean enable) {
        attributeTypeBox.setEnabled(enable);
    }

    public void clearSelections() {
        jobList.getSelectionModel().clearSelection();
    }

    @Override
    public String getName() {
        return "Select goldstandard and job";
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
        attributeTypeBox = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        gsAttributeBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();

        jobList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jobList);

        attributeTypeBox.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SelectSingleJobWithGSVisualPanel1.class, "SelectSingleJobVisualPanel1.jLabel1.text")); // NOI18N

        gsAttributeBox.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SelectSingleJobWithGSVisualPanel1.class, "SelectSingleJobWithGSVisualPanel1.jLabel2.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(attributeTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gsAttributeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gsAttributeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributeTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<AttributeTypeI> attributeTypeBox;
    private javax.swing.JComboBox<JobI> gsAttributeBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<JobI> jobList;
    // End of variables declaration//GEN-END:variables

}
