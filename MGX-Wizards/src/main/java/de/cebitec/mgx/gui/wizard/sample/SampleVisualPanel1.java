package de.cebitec.mgx.gui.wizard.sample;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JPanel;

public final class SampleVisualPanel1 extends JPanel {

    public static final String PROP_COLLECTIONDATE = "collection_date";

    /** Creates new form SampleVisualPanel1 */
    public SampleVisualPanel1() {
        initComponents();
        //jXMonthView1.setFirstDayOfWeek(Calendar.MONDAY);
        //jXMonthView1.setSelectionMode(SelectionMode.SINGLE_SELECTION);
        jXMonthView1.setUpperBound(new Date(System.currentTimeMillis()));
        jXMonthView1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Date selectionDate = ((JXMonthView) e.getSource()).getSelectionDate();
                firePropertyChange(PROP_COLLECTIONDATE, 0, 1);
            }
        });
    }

    @Override
    public String getName() {
        return "Collection date";
    }

    public Date getCollectionDate() {
        return jXMonthView1.getSelectionDate();
    }

    public void setCollectionDate(Date d) {
        jXMonthView1.setSelectionDate(d);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jXMonthView1 = new org.jdesktop.swingx.JXMonthView();
        jLabel1 = new javax.swing.JLabel();

        jXMonthView1.setPreferredColumnCount(3);
        jXMonthView1.setTraversable(true);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SampleVisualPanel1.class, "SampleVisualPanel1.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXMonthView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jXMonthView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private org.jdesktop.swingx.JXMonthView jXMonthView1;
    // End of variables declaration//GEN-END:variables
}
