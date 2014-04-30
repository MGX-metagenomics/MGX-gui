package de.cebitec.mgx.gui.pca;

import de.cebitec.mgx.gui.datamodel.misc.PCAResult;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import java.util.List;
import java.util.Map;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author sjaenick
 */
public class PCACustomizer extends javax.swing.JPanel {

    private static final Pair<PC, PC> pc12 = new Pair<>(PC.PC1, PC.PC2);
    private static final Pair<PC, PC> pc23 = new Pair<>(PC.PC2, PC.PC3);
    private static final Pair<PC, PC> pc13 = new Pair<>(PC.PC1, PC.PC3);
    private XYSeries series = null;
    private PCAResult pca = null;
    private Map<XYDataItem, String> toolTips = null;

    /**
     * Creates new form PCACustomizer
     */
    public PCACustomizer() {
        initComponents();
        pcSel.addItem("PC1 / PC2");
        pcSel.addItem("PC2 / PC3");
        pcSel.addItem("PC1 / PC3");
        pcSel.setSelectedIndex(0);
    }

    public void setLoadings(PCAResult pca, Map<XYDataItem, String> toolTips) {
        this.pca = pca;
        this.toolTips = toolTips;
        series = new XYSeries("");
        numLoadings.setMinimum(0);
        numLoadings.setMaximum(pca.getLoadings().size());
        numLoadings.setValue(pca.getLoadings().size());
        int num = pca.getLoadings().size();
        //while (num > 10) { num = num / 10; }
        numLoadings.setMajorTickSpacing(num);
        numLoadings.setEnabled(true);
    }

    public XYSeries getLoadings() {
//        if (series == null) {
//            series = new XYSeries("");
//        }
//        series.clear();
//        int numToShow = numLoadings.getValue();
//        List<Point> pts = pca.getDatapoints().subList(pca.getDatapoints().size() - numToShow, pca.getDatapoints().size());
//
//        for (Point p : pts) {
//            XYDataItem item = new XYDataItem(p.getX(), p.getY());
//            toolTips.put(item, p.getName());
//            series.add(item);
//        }
        return series;
    }

    public Pair<PC, PC> getPCs() {
        String s = (String) pcSel.getSelectedItem();
        switch (s) {
            case "PC1 / PC2":
                return pc12;
            case "PC2 / PC3":
                return pc23;
            case "PC1 / PC3":
                return pc13;
        };
        return pc12;
    }

    public boolean useFractions() {
        return fractions.isSelected();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pcSel = new javax.swing.JComboBox();
        fractions = new javax.swing.JCheckBox();
        numLoadings = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();

        fractions.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(fractions, org.openide.util.NbBundle.getMessage(PCACustomizer.class, "PCACustomizer.fractions.text")); // NOI18N

        numLoadings.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        numLoadings.setMajorTickSpacing(5);
        numLoadings.setMaximum(10);
        numLoadings.setPaintLabels(true);
        numLoadings.setPaintTicks(true);
        numLoadings.setSnapToTicks(true);
        numLoadings.setEnabled(false);
        numLoadings.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                numLoadingsStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PCACustomizer.class, "PCACustomizer.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pcSel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(fractions, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
            .addComponent(numLoadings, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pcSel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(fractions)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(3, 3, 3)
                .addComponent(numLoadings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(151, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void numLoadingsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_numLoadingsStateChanged
        if (series == null || pca == null) {
            return;
        }
        numLoadings.setToolTipText("Show "+numLoadings.getValue()+ " of "+ numLoadings.getMaximum()+" loadings");
        series.clear();
        int numToShow = numLoadings.getValue();
        if (numToShow == 0) {
            return;
        }
        List<Point> pts = pca.getLoadings().subList(pca.getLoadings().size() - numToShow, pca.getLoadings().size());

        for (Point p : pts) {
            XYDataItem item = new XYDataItem(p.getX(), p.getY());
            toolTips.put(item, p.getName());
            series.add(item);
        }
    }//GEN-LAST:event_numLoadingsStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox fractions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSlider numLoadings;
    private javax.swing.JComboBox pcSel;
    // End of variables declaration//GEN-END:variables
}
