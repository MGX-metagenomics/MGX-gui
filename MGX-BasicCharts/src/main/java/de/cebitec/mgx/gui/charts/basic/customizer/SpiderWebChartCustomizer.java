package de.cebitec.mgx.gui.charts.basic.customizer;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.gui.vizfilter.SortOrder;
import de.cebitec.mgx.gui.vizfilter.ToFractionFilter;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class SpiderWebChartCustomizer extends javax.swing.JPanel implements VisFilterI<DistributionI<Long>, DistributionI<Double>> {

    private AttributeTypeI at;

    /**
     * Creates new form BasicCustomizer
     */
    public SpiderWebChartCustomizer() {
        initComponents();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != super.isEnabled()) {
            super.setEnabled(enabled);
            useFractions.setEnabled(enabled);
        }
    }

    public void setAttributeType(final AttributeTypeI aType) {
        if (aType.equals(at)) {
            return;
        }
        at = aType;
    }

    private boolean useFractions() {
        return useFractions.isSelected();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        useFractions = new javax.swing.JCheckBox();

        useFractions.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        useFractions.setText(org.openide.util.NbBundle.getMessage(SpiderWebChartCustomizer.class, "SpiderWebChartCustomizer.useFractions.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(useFractions)
                .addGap(0, 67, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useFractions)
                .addContainerGap(242, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox useFractions;
    // End of variables declaration//GEN-END:variables

    @Override
    public List<Pair<GroupI, DistributionI<Double>>> filter(List<Pair<GroupI, DistributionI<Long>>> dists) {

        List<Pair<GroupI, DistributionI<Double>>> ret;
        if (useFractions()) {
            VisFilterI<DistributionI<Long>, DistributionI<Double>> fracFilter = new ToFractionFilter();
            ret = fracFilter.filter(dists);
        } else {
            ret = new LongToDouble().filter(dists);
        }

        SortOrder<Double> sorter = new SortOrder<>(at, SortOrder.DESCENDING);
        ret = sorter.filter(ret);

        return ret;
    }
}
