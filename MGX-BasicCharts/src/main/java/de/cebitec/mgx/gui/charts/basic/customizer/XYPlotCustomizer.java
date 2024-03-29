package de.cebitec.mgx.gui.charts.basic.customizer;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.gui.vizfilter.SortOrder;
import de.cebitec.mgx.gui.vizfilter.ToFractionFilter;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import java.io.Serial;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class XYPlotCustomizer extends javax.swing.JPanel implements VisFilterI<DistributionI<Long>, DistributionI<Double>> {

    @Serial
    private static final long serialVersionUID = 1L;

    private AttributeTypeI at;

    /**
     * Creates new form XYPlotCustomizer
     */
    public XYPlotCustomizer() {
        initComponents();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != super.isEnabled()) {
            super.setEnabled(enabled);
            useFractions.setEnabled(enabled);
            sortAscending.setEnabled(enabled);
            sortDescending.setEnabled(enabled);
            logX.setEnabled(enabled);
            logY.setEnabled(enabled);
        }
    }

    public void setAttributeType(AttributeTypeI aType) {
        at = aType;
    }

    public boolean useFractions() {
        return useFractions.isSelected();
    }

    public boolean getSortAscending() {
        return sortAscending.isSelected();
    }

    public boolean logX() {
        return logX.isSelected();
    }

    public boolean logY() {
        return logY.isSelected();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sortAscending = new javax.swing.JRadioButton();
        sortDescending = new javax.swing.JRadioButton();
        useFractions = new javax.swing.JCheckBox();
        logY = new javax.swing.JCheckBox();
        logX = new javax.swing.JCheckBox();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText(org.openide.util.NbBundle.getMessage(XYPlotCustomizer.class, "XYPlotCustomizer.jLabel1.text")); // NOI18N

        buttonGroup1.add(sortAscending);
        sortAscending.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sortAscending.setSelected(true);
        sortAscending.setText(org.openide.util.NbBundle.getMessage(XYPlotCustomizer.class, "XYPlotCustomizer.sortAscending.text")); // NOI18N

        buttonGroup1.add(sortDescending);
        sortDescending.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        sortDescending.setText(org.openide.util.NbBundle.getMessage(XYPlotCustomizer.class, "XYPlotCustomizer.sortDescending.text")); // NOI18N

        useFractions.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        useFractions.setText(org.openide.util.NbBundle.getMessage(XYPlotCustomizer.class, "XYPlotCustomizer.useFractions.text")); // NOI18N

        logY.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        logY.setText(org.openide.util.NbBundle.getMessage(XYPlotCustomizer.class, "XYPlotCustomizer.logY.text")); // NOI18N

        logX.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        logX.setText(org.openide.util.NbBundle.getMessage(XYPlotCustomizer.class, "XYPlotCustomizer.logX.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(sortAscending)
                    .addComponent(useFractions)
                    .addComponent(logY)
                    .addComponent(logX)
                    .addComponent(sortDescending))
                .addGap(0, 69, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sortAscending)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sortDescending)
                .addGap(19, 19, 19)
                .addComponent(useFractions)
                .addGap(18, 18, 18)
                .addComponent(logX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logY)
                .addContainerGap(106, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox logX;
    private javax.swing.JCheckBox logY;
    private javax.swing.JRadioButton sortAscending;
    private javax.swing.JRadioButton sortDescending;
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

        SortOrder<Double> sorter = new SortOrder<>(sortAscending.isSelected() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
        List<Pair<GroupI, DistributionI<Double>>> filter = sorter.filter(ret);
        return filter;
    }
}
