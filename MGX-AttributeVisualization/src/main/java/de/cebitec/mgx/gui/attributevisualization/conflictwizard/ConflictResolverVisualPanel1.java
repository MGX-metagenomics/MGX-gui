package de.cebitec.mgx.gui.attributevisualization.conflictwizard;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class ConflictResolverVisualPanel1 extends JPanel implements ListSelectionListener {

    private VisualizationGroup vg;
    private SeqRun run;

    /**
     * Creates new form ConflictResolverVisualPanel1
     */
    public ConflictResolverVisualPanel1() {
        initComponents();

        jobList.setCellRenderer(new CellRenderer());
        jobList.addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.firePropertyChange("toolSelected", 0, 1);
    }

    private class CellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Job job = (Job) value;
            assert job.getTool() != null; // fails
            StringBuilder sb = new StringBuilder(job.getTool().getName())
                    .append(" (")
                    .append(joinParameters(job.getParameters(), ", "))
                    .append(")");
            return super.getListCellRendererComponent(list, sb.toString(), index, isSelected, cellHasFocus);
        }
    }

    public void setJobs(Collection<Job> jobs) {
        jobList.setListData(jobs.toArray());
    }

    public void setSeqRun(SeqRun run) {
        this.run = run;
        String projName = ((MGXMaster) run.getMaster()).getProject();
        seqRunLabel.setText(projName + ": " + run.getName());
    }

    public void setVisualizationGroup(VisualizationGroup vg) {
        this.vg = vg;
        groupLabel.setText(vg.getName());
    }

    public Job getSelectedJob() {
        return (Job) jobList.getSelectedValue();
    }

    @Override
    public String getName() {
        return vg.getName() + " " + run.getName();
    }

    protected static String joinParameters(Iterable<JobParameter> pColl, String separator) {
        Iterator<JobParameter> oIter;
        if (pColl == null || (!(oIter = pColl.iterator()).hasNext())) {
            return "";
        }
        StringBuilder oBuilder = new StringBuilder(toParameterString(oIter.next()));
        while (oIter.hasNext()) {
            oBuilder.append(separator).append(toParameterString(oIter.next()));
        }
        return oBuilder.toString();
    }

    private static String toParameterString(JobParameter jp) {
        return new StringBuilder(Long.toString(jp.getNodeId()))
                .append(".")
                .append(jp.getParameterName())
                .append("=")
                .append(jp.getParameterValue())
                .toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jobList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        groupLabel = new javax.swing.JLabel();
        seqRunLabel = new javax.swing.JLabel();

        jobList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jobList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jobList);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(5);
        jTextArea1.setText(org.openide.util.NbBundle.getMessage(ConflictResolverVisualPanel1.class, "ConflictResolverVisualPanel1.jTextArea1.text")); // NOI18N
        jScrollPane2.setViewportView(jTextArea1);

        org.openide.awt.Mnemonics.setLocalizedText(groupLabel, org.openide.util.NbBundle.getMessage(ConflictResolverVisualPanel1.class, "ConflictResolverVisualPanel1.groupLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(seqRunLabel, org.openide.util.NbBundle.getMessage(ConflictResolverVisualPanel1.class, "ConflictResolverVisualPanel1.seqRunLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(groupLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(seqRunLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(groupLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(seqRunLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel groupLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JList jobList;
    private javax.swing.JLabel seqRunLabel;
    // End of variables declaration//GEN-END:variables
}
