package de.cebitec.mgx.gui.charts.basic.customizer;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.visualization.filter.ReplicateVisFilterI;
import de.cebitec.mgx.gui.vizfilter.ExcludeFilter;
import de.cebitec.mgx.gui.vizfilter.LimitFilter;
import de.cebitec.mgx.gui.vizfilter.SortOrder;
import de.cebitec.mgx.gui.vizfilter.ToFractionFilter;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import de.cebitec.mgx.gui.vizfilter.ReplicateSortOrder;
import de.cebitec.mgx.gui.vizfilter.ReplicateToFractionFilter;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class BarChartCustomizer extends javax.swing.JPanel implements VisFilterI<DistributionI<Long>, DistributionI<Double>> {

    private AttributeTypeI at;

    /**
     * Creates new form BasicCustomizer
     */
    public BarChartCustomizer() {
        initComponents();
    }

    public void setAttributeType(final AttributeTypeI aType) {
        if (aType.equals(at)) {
            return;
        }
        at = aType;
        treeFilter.setAttributeType(at);
    }

    public boolean logY() {
        return useLogY.isSelected();
    }

    public boolean useFractions() {
        return useFractions.isSelected();
    }

    public boolean getSortAscending() {
        return sortAscending.isSelected();
    }

    public double getItemMargin() {
        return 1D * itemMargin.getValue() / 100;
    }

    public double getCategoryMargin() {
        return 1D * categoryMargin.getValue() / 100;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sortOrderGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        sortAscending = new javax.swing.JRadioButton();
        sortDescending = new javax.swing.JRadioButton();
        useFractions = new javax.swing.JCheckBox();
        limit = new javax.swing.JComboBox();
        useLogY = new javax.swing.JCheckBox();
        categoryMargin = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        itemMargin = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        treeFilter = new de.cebitec.mgx.gui.swingutils.TreeFilter();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.jLabel1.text")); // NOI18N

        sortOrderGroup.add(sortAscending);
        sortAscending.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sortAscending.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.sortAscending.text")); // NOI18N

        sortOrderGroup.add(sortDescending);
        sortDescending.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sortDescending.setSelected(true);
        sortDescending.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.sortDescending.text")); // NOI18N

        useFractions.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        useFractions.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.useFractions.text")); // NOI18N

        limit.setModel(new javax.swing.DefaultComboBoxModel(LimitFilter.LIMITS.values()));
        limit.setSelectedItem(LimitFilter.LIMITS.ALL);
        limit.setMinimumSize(new java.awt.Dimension(16, 24));

        useLogY.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        useLogY.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.useLogY.text")); // NOI18N

        categoryMargin.setValue(5);
        categoryMargin.setMinimumSize(new java.awt.Dimension(16, 16));
        categoryMargin.setPreferredSize(new java.awt.Dimension(25, 16));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.jLabel2.text")); // NOI18N

        itemMargin.setValue(5);
        itemMargin.setMinimumSize(new java.awt.Dimension(15, 16));
        itemMargin.setPreferredSize(new java.awt.Dimension(15, 16));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.jLabel3.text")); // NOI18N

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel4.setText(org.openide.util.NbBundle.getMessage(BarChartCustomizer.class, "BarChartCustomizer.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(limit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(categoryMargin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sortAscending)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sortDescending))
                    .addComponent(useFractions)
                    .addComponent(useLogY)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(treeFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(itemMargin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sortAscending)
                    .addComponent(sortDescending))
                .addGap(18, 18, 18)
                .addComponent(useFractions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(useLogY)
                .addGap(12, 12, 12)
                .addComponent(limit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(treeFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(itemMargin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryMargin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider categoryMargin;
    private javax.swing.JSlider itemMargin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox limit;
    private javax.swing.JRadioButton sortAscending;
    private javax.swing.JRadioButton sortDescending;
    private javax.swing.ButtonGroup sortOrderGroup;
    private de.cebitec.mgx.gui.swingutils.TreeFilter treeFilter;
    private javax.swing.JCheckBox useFractions;
    private javax.swing.JCheckBox useLogY;
    // End of variables declaration//GEN-END:variables

    @Override
    public List<Pair<VisualizationGroupI, DistributionI<Double>>> filter(List<Pair<VisualizationGroupI, DistributionI<Long>>> dists) {

        List<Pair<VisualizationGroupI, DistributionI<Double>>> ret = null;
        if (useFractions()) {
            VisFilterI<DistributionI<Long>, DistributionI<Double>> fracFilter = new ToFractionFilter();
            ret = fracFilter.filter(dists);
        } else {
            ret = new LongToDouble().filter(dists);
        }

        if (at.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL) {
            Set<AttributeI> blackList = treeFilter.getBlackList();
            if (blackList.size() > 0) {
                ExcludeFilter<Double> ef = new ExcludeFilter<>(blackList);
                ret = ef.filter(ret);
            }
        }

        LimitFilter<Double> lf = new LimitFilter<>(LimitFilter.LIMITS.values()[limit.getSelectedIndex()]);
        ret = lf.filter(ret);

        SortOrder<Double> sorter = new SortOrder<>(at, sortAscending.isSelected() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
        ret = sorter.filter(ret);

        return ret;
    }
    
    public List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> filterRep(List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> dists) {

        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> ret = dists;
        if (useFractions()) {
            ReplicateToFractionFilter fracFilter = new ReplicateToFractionFilter();
            ret = fracFilter.filter(dists);
        }
//
//        if (at.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL) {
//            Set<AttributeI> blackList = treeFilter.getBlackList();
//            if (blackList.size() > 0) {
//                ExcludeFilter<Double> ef = new ExcludeFilter<>(blackList);
//                ret = ef.filter(ret);
//            }
//        }
//
//        LimitFilter<Double> lf = new LimitFilter<>(LimitFilter.LIMITS.values()[limit.getSelectedIndex()]);
//        ret = lf.filter(ret);

        ReplicateSortOrder<Double> sorter = new ReplicateSortOrder<>(at, sortAscending.isSelected() ? ReplicateSortOrder.ASCENDING : ReplicateSortOrder.DESCENDING);
        ret = sorter.filter(ret);

        return ret;
    }
}
