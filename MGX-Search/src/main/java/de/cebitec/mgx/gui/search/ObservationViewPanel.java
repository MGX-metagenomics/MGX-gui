package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.datamodel.Sequence;
import java.awt.Graphics2D;
import java.util.Collection;

/**
 *
 * @author sj
 */
public class ObservationViewPanel extends javax.swing.JPanel {
    
    private final static int BORDER = 5;
    private int seqLen;

    /**
     * Creates new form ObservationViewPanel
     */
    public ObservationViewPanel() {
        initComponents();
    }
    
    public void setSequence(Sequence seq) {
        seqLen = seq.getLength();
        readName.setText(seq.getName() + " ("+seq.getLength()+"bp)");
    }
    
    public void setObservations(Collection<Observation> obs) {
        readName.setText(readName.getText() + " numObs: "+obs.size());
//        Graphics2D gfx = (Graphics2D) obsview.getGraphics();
//        assert gfx != null;
//        int height = obsview.getHeight();
//        int width = obsview.getWidth();
//        
//        int midheight = height/2;
//        gfx.drawLine(BORDER, midheight, width - BORDER, midheight);
//        gfx.drawString("1", BORDER - 3, midheight + 5);
//        gfx.drawString(String.valueOf(seqLen), width - BORDER - 3, midheight + 5);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        readName = new javax.swing.JLabel();
        obsview = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        org.openide.awt.Mnemonics.setLocalizedText(readName, org.openide.util.NbBundle.getMessage(ObservationViewPanel.class, "ObservationViewPanel.readName.text")); // NOI18N

        javax.swing.GroupLayout obsviewLayout = new javax.swing.GroupLayout(obsview);
        obsview.setLayout(obsviewLayout);
        obsviewLayout.setHorizontalGroup(
            obsviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 432, Short.MAX_VALUE)
        );
        obsviewLayout.setVerticalGroup(
            obsviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(obsview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(readName)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(readName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(obsview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel obsview;
    private javax.swing.JLabel readName;
    // End of variables declaration//GEN-END:variables
}
