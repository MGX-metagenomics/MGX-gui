/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.subdist;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author sj
 */
public class SubDistCustomizer extends javax.swing.JPanel implements ItemListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final DefaultComboBoxModel<AttributeI> baseModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<Pair<AttributeTypeI, JobI>> selectModel = new DefaultComboBoxModel<>();
    private AttributeTypeI currentAttrType = null;
    private GroupI<SeqRunI> currentGroup = null;
    private SeqRunI currentRun = null;

    /**
     * Creates new form SubDistCustomizer
     */
    @SuppressWarnings("unchecked")
    public SubDistCustomizer() {
        initComponents();
        baseDistribution.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, value == null ? value : ((AttributeI) value).getValue(), index, isSelected, cellHasFocus);
            }

        });
        baseDistribution.addItemListener(this);

        selectBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
                Pair<AttributeTypeI, JobI> p = (Pair<AttributeTypeI, JobI>) value;
                String displayName = p.getFirst().getName()
                        + " ("
                        + p.getSecond().getTool().getName()
                        + ")";
                return super.getListCellRendererComponent(list, displayName, index, isSelected, cellHasFocus);
            }

        });
    }

    public void setAttributeType(AttributeTypeI aType) {
        currentAttrType = aType;
        baseModel.removeAllElements();
        selectModel.removeAllElements();

        try {
            Pair<GroupI, DistributionI<Long>> p = VGroupManager.getInstance().getDistributions().get(0);
            DistributionI<Long> dist = p.getSecond();

            List<AttributeI> sorted = new ArrayList<>(dist.keySet());
            Collections.sort(sorted);

            baseModel.addAll(sorted);
            baseDistribution.setSelectedIndex(0);
        } catch (ConflictingJobsException ex) {
            Logger.getLogger(SubDistCustomizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AttributeI getFilterAttribute() {
        return baseModel.getElementAt(baseDistribution.getSelectedIndex());
    }

    public Pair<AttributeTypeI, JobI> getSelectedCriteria() {
        return selectModel.getElementAt(selectBox.getSelectedIndex());
    }

    public SeqRunI getSeqRun() {
        return currentRun;
    }

    public GroupI<SeqRunI> getGroup() {
        return currentGroup;
    }

    public boolean useFractions() {
        return fractionsBox.isSelected();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == ItemEvent.SELECTED) {

            selectModel.removeAllElements();

            Pair<GroupI, DistributionI<Long>> p;
            try {
                p = VGroupManager.getInstance().getDistributions().get(0);
                GroupI group = p.getFirst();
                assert group.getNumberOfSeqRuns() == 1;

                if (group.getContentClass() == SeqRunI.class) {
                    currentGroup = group;
                    currentRun = currentGroup.getSeqRuns().get(0);
                    MGXMasterI master = currentRun.getMaster();

                    List<Pair<AttributeTypeI, JobI>> newData = new ArrayList<>();
                    Map<JobI, Set<AttributeTypeI>> map = master.SeqRun().getJobsAndAttributeTypes(currentRun);
                    for (Map.Entry<JobI, Set<AttributeTypeI>> me : map.entrySet()) {
                        JobI job = me.getKey();
                        master.Tool().ByJob(job); // trigger tool fetch

                        for (AttributeTypeI atype : me.getValue()) {
                            if (currentAttrType == null || !currentAttrType.getName().equals(atype.getName())) {
                                Pair<AttributeTypeI, JobI> newPair = new Pair<>(atype, job);
                                newData.add(newPair);
                            }
                        }
                    }

                    Collections.sort(newData, new Comparator<Pair<AttributeTypeI, JobI>>() {

                        @Override
                        public int compare(Pair<AttributeTypeI, JobI> p1, Pair<AttributeTypeI, JobI> p2) {
                            int ret = p1.getFirst().getName().compareTo(p2.getFirst().getName());
                            if (ret != 0) {
                                return ret;
                            }
                            return p1.getSecond().getTool().getName().compareTo(p2.getSecond().getTool().getName());
                        }

                    });
                    selectModel.addAll(newData);
                    selectBox.setSelectedIndex(0);
                }
            } catch (MGXException | ConflictingJobsException ex) {
                Logger.getLogger(SubDistCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        baseDistribution = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        selectBox = new javax.swing.JComboBox<>();
        fractionsBox = new javax.swing.JCheckBox();

        baseDistribution.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        baseDistribution.setModel(baseModel);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel1.setText("For sequences annotated as");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setText("Show distribution for");

        selectBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        selectBox.setModel(selectModel);

        fractionsBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        fractionsBox.setText(" Use fractions");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(baseDistribution, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(selectBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(fractionsBox))
                .addGap(0, 67, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(baseDistribution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(fractionsBox)
                .addContainerGap(356, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<AttributeI> baseDistribution;
    private javax.swing.JCheckBox fractionsBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox<Pair<AttributeTypeI, JobI>> selectBox;
    // End of variables declaration//GEN-END:variables
}
