package de.cebitec.mgx.gui.search.ui;

import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.datamodel.Sequence;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author sj
 */
public class ObservationView extends javax.swing.JPanel {

    private Border unselectedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    private Border selectedBorder = BorderFactory.createRaisedBevelBorder();

    /**
     * Creates new form ObservationView
     */
    public ObservationView() {
        initComponents();
        drawArea.add(view, BorderLayout.CENTER);
    }
    private final View view = new View();

    public void show(Sequence seq, Observation[] obs, boolean selected) {
        readName.setText(seq.getName() + " (" + seq.getLength() + "bp)");
        setBorder(selected ? selectedBorder : unselectedBorder);
        view.setData(seq, obs, null);
    }

    public void show(Sequence seq, String msg, boolean selected) {
        readName.setText(seq.getName() + " (" + seq.getLength() + "bp)");
        setBorder(selected ? selectedBorder : unselectedBorder);
        view.setData(seq, null, msg);
    }

    private class View extends JComponent {

        private Sequence seq;
        private Observation[] obs;
        private String message;
        //
        private final static int borderWidth = 10;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            rh.put(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g2.setRenderingHints(rh);

            int height = getHeight();
            int width = getWidth();

            int midX = width / 2;
            int midY = height / 2;

            int seqLen = seq.getLength();

            g2.setColor(Color.BLACK);

            // horizontal line representing the sequence itself
            g2.draw(new Line2D.Double(borderWidth, midY, width - borderWidth, midY));
            // left boundary
            g2.draw(new Line2D.Double(borderWidth, midY - 5, borderWidth, midY + 5));
            g2.drawString("1", borderWidth - 3, midY - 7); //0bp position
            // right boundary
            g2.draw(new Line2D.Double(width - borderWidth, midY - 5, width - borderWidth, midY + 5));
            if (seqLen < 100) {
                g2.drawString(String.valueOf(seqLen), width - borderWidth - 8, midY - 7); // XXbp position
            } else if (seqLen < 1000) {
                g2.drawString(String.valueOf(seqLen), width - borderWidth - 11, midY - 7); // XXXbp position
            } else {
                g2.drawString(String.valueOf(seqLen), width - borderWidth - 13, midY - 7); // XXXXbp position
            }

            if (obs == null) {
                g2.drawString(message, midX - 60, midY - 40);
                g2.dispose();
                return;
            }

            int paintableX = width - 2 * borderWidth;
            int scaleX = paintableX / seqLen;

            int layerY = midY - 5;
            for (Observation o : obs) {
                int scaledStart = o.getStart() * scaleX;
                int scaledStop = o.getStop() * scaleX;

                g2.draw(new Line2D.Double(borderWidth + scaledStart, layerY, borderWidth + scaledStop, layerY));
                layerY -= 5;
            }
            g2.dispose();

        }

        public void setData(Sequence s, Observation[] o, String msg) {
            seq = s;
            obs = o;
            message = msg;
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

        readName = new javax.swing.JLabel();
        drawArea = new javax.swing.JPanel();

        readName.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(readName, org.openide.util.NbBundle.getMessage(ObservationView.class, "ObservationView.readName.text")); // NOI18N
        readName.setOpaque(true);

        drawArea.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(drawArea, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(readName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(readName, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(drawArea, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawArea;
    private javax.swing.JLabel readName;
    // End of variables declaration//GEN-END:variables
}
