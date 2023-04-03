package de.cebitec.mgx.gui.search.ui;

import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SequenceI;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sj
 */
public class ObservationView extends javax.swing.JPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates new form ObservationView
     */
    public ObservationView() {
        super();
        initComponents();
        drawArea.add(view, BorderLayout.CENTER);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
    }
    private final View view = new View();

    public void show(SequenceI seq, ObservationI[] obs, final String searchTerm) {
        readName.setText(seq.getName() + " (" + seq.getLength() + "bp)");
        view.setData(seq, obs, searchTerm);
        repaint();
    }

    public void clear() {
        readName.setText("");
        view.clear();
        repaint();
    }

    private static class View extends JComponent {

        @Serial
        private static final long serialVersionUID = 1L;

        private SequenceI seq;
        private ObservationI[] obs;
        private String selectedTerm;
        private final static int borderWidth = 15;
        private final List<ObservationArrow> arrows = new ArrayList<>();
        //
        private final static Color lighterGray = new Color(210, 210, 210);

        public View() {
            setBackground(Color.WHITE);
            ToolTipManager.sharedInstance().registerComponent(this);
            ToolTipManager.sharedInstance().setDismissDelay(5000);
        }

        public void clear() {
            setData(null, null, null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            arrows.clear();
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            if (seq == null || obs == null || obs.length == 0) {
                g2.dispose();
                return;
            }

            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            rh.put(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g2.setRenderingHints(rh);

            int height = getHeight();
            int width = getWidth();

            int seqLen = seq.getLength();

            g2.setColor(Color.BLACK);

            // horizontal line representing the sequence itself
            g2.draw(new Line2D.Double(borderWidth, height - borderWidth, width - borderWidth, height - borderWidth));
            // left boundary
            g2.draw(new Line2D.Double(borderWidth, height - borderWidth - 3, borderWidth, height - borderWidth + 3));
            g2.drawString("0", borderWidth - 3, height - borderWidth + 14); //1bp position
            // right boundary
            g2.draw(new Line2D.Double(width - borderWidth, height - borderWidth - 3, width - borderWidth, height - borderWidth + 3));
            if (seqLen < 100) {
                g2.drawString(String.valueOf(seqLen), width - borderWidth - 8, height - borderWidth + 14); // XXbp position
            } else if (seqLen < 1000) {
                g2.drawString(String.valueOf(seqLen), width - borderWidth - 11, height - borderWidth + 14); // XXXbp position
            } else {
                g2.drawString(String.valueOf(seqLen), width - borderWidth - 13, height - borderWidth + 14); // XXXXbp position
            }

            float paintableX = getWidth() - 2 * borderWidth; // width of the sequence in px
            float scaleX = paintableX / (1f * seqLen); // internal scale factor

            int layerY = height - borderWidth - 17;

            Font f = g2.getFont();
            Font boldFont = new Font(f.getFontName(), Font.BOLD, f.getSize());
            Font normalFont = new Font(f.getFontName(), Font.PLAIN, f.getSize());

            for (ObservationI o : obs) {
                int scaledStart = FastMath.round(o.getStart() * scaleX);
                int scaledStop = FastMath.round(o.getStop() * scaleX);

                g2.setColor(Color.BLACK);

                if (o.getAttributeName().equals(selectedTerm)) {
                    g2.setFont(boldFont);
                } else {
                    g2.setFont(normalFont);
                }

                int obsLen;
                ObservationArrow arrow;
                if (o.getStart() < o.getStop()) {
                    obsLen = o.getStop() - o.getStart();
                    int scaledLen = FastMath.round(obsLen * scaleX);
                    arrow = new ObservationArrow(o, borderWidth + scaledStart, layerY, scaledLen);
                    g2.drawString(o.getAttributeTypeName() + ": " + o.getAttributeName(), borderWidth + FastMath.min(scaledStart, scaledStop), layerY);
                } else {
                    obsLen = o.getStart() - o.getStop();
                    int scaledLen = FastMath.round(obsLen * scaleX);
                    arrow = new ObservationArrow(o, borderWidth + scaledStop, layerY, scaledLen);
                    g2.drawString(o.getAttributeTypeName() + ": " + o.getAttributeName(), 10 + borderWidth + FastMath.min(scaledStart, scaledStop), layerY);
                }
                g2.fill(arrow);
                arrows.add(arrow);

                //g2.draw(new Line2D.Double(borderWidth + scaledStart, layerY, borderWidth + scaledStop, layerY));
                layerY -= 24;
            }

            Composite oldComp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

            g2.setColor(Color.LIGHT_GRAY);
            for (ObservationArrow r : arrows) {
                Shape shadow = AffineTransform.getTranslateInstance(4, 3).createTransformedShape(r);
                g2.fill(shadow);
            }
            g2.setColor(lighterGray);
            for (ObservationArrow r : arrows) {
                Shape shadow = AffineTransform.getTranslateInstance(4, 3).createTransformedShape(r);
                g2.draw(shadow);
            }

            //g2.setComposite(oldComp);
            // draw arrows (and borders)
            g2.setColor(Color.GREEN);
            for (ObservationArrow r : arrows) {
                g2.fill(r);
            }
            g2.setColor(Color.DARK_GRAY);
            for (ObservationArrow r : arrows) {
                g2.draw(r);
            }

            g2.dispose();
        }

        @Override
        public String getToolTipText(MouseEvent m) {
            Point loc = m.getPoint();
            for (ObservationArrow a : arrows) {
                if (a.getBounds().contains(loc)) {
                    return a.getToolTipText();
                }
            }
            return null;
        }

        public void setData(SequenceI s, ObservationI[] o, String term) {
            seq = s;
            obs = o;
            selectedTerm = term;
            arrows.clear();

            if (obs != null) {
                // create layers for the observations
                createLayers(obs);
                setMinimumSize(new Dimension(getWidth(), 2 * borderWidth + 30 + layers.size() * 15));
                setPreferredSize(new Dimension(getWidth(), 2 * borderWidth + 30 + layers.size() * 15));
                setMaximumSize(new Dimension(getWidth(), 2 * borderWidth + 30 + layers.size() * 15));
            }
            repaint();
        }
        private final List<Layer> layers = new ArrayList<>();

        private void createLayers(ObservationI[] obs) {
            layers.clear();
            for (ObservationI o : obs) {
                boolean placed = false;
                for (Layer l : layers) {
                    if (l.add(o)) { // layer accepts this observation
                        placed = true;
                        break;
                    }
                }
                if (!placed) {
                    Layer newLayer = new Layer();
                    newLayer.add(o);
                    layers.add(newLayer);
                }
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
                .addComponent(drawArea, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawArea;
    private javax.swing.JLabel readName;
    // End of variables declaration//GEN-END:variables
}
