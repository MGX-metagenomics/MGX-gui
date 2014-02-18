/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer.readsviewer;

import de.cebitec.mgx.gui.mapping.sequences.MappedSequenceHolder;
import de.cebitec.mgx.gui.mapping.sequences.JMappedSequence;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfoManager;
import de.cebitec.mgx.gui.mapping.viewer.positions.PaintingAreaInfo;
import de.cebitec.mgx.gui.mapping.loader.Loader;
import de.cebitec.mgx.gui.mapping.misc.ColorProperties;
import de.cebitec.mgx.gui.mapping.viewer.positions.panel.ReadsBasePanel;
import de.cebitec.mgx.gui.mapping.sequences.ReferenceHolder;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.BorderFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author belmann
 */
public class ReadsViewer extends AbstractViewer<MappedSequence> {

    private static int VIEW_HEIGHT = 1000;
    private static final int LABEL_MARGIN = 3;
    private Map<Integer, IdentityLayer> layersMap;
    private static final int MARGIN_TO_NEXT_IDENTITY_LAYER = 5;
    private static final int MARGIN_TO_NEXT_IDENTITY_BORDER = 5;

    public ReadsViewer(BoundsInfoManager boundsInfoManager, ReadsBasePanel basePanel, ReferenceHolder refGenome, Loader loader) {
        super(boundsInfoManager, basePanel, refGenome, loader);
        super.setVerticalMargin(10);
        super.setHorizontalMargin(40);
        setViewerSize();
        layersMap = new HashMap<>();
    }

    @Override
    protected int getMaximalHeight() {
        return VIEW_HEIGHT;
    }

    @Override
    public void changeToolTipText(int logPos) {
    }

    @Override
    public void close() {
        super.close();
        this.layersMap.clear();
    }

    @Override
    public void boundsChangedHook() {
        synchronized (this) {
            this.removeAll();
        }
    }

    private void setViewerSize() {
        this.setPreferredSize(new Dimension(1, 230));
        this.revalidate();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        Iterator<Integer> iter = this.layersMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer identity = iter.next();
            Integer yCoord = layersMap.get(identity).getVerticalPosition();
            drawIdentityLine(g, ColorProperties.IDENTITY_LINES, yCoord + this.verticalMargin, identity.toString(),
                    ColorProperties.IDENTITY_COLORS[identity]);
        }
    }

    private JMappedSequence transformMappedSequenceToJComponent(MappedSequenceHolder mappedSequence) {
        int yCoord = mappedSequence.getLayer();
        PaintingAreaInfo bounds = getPaintingAreaInfo();

        // get left boundary of the feature
        double phyStart = this.getPhysBoundariesForLogPos(mappedSequence.getStart()).getLeftPhysBound();
        if (phyStart < bounds.getPhyLeft()) {
            phyStart = bounds.getPhyLeft();
        }

        // get right boundary of the feature
        double phyStop = this.getPhysBoundariesForLogPos(mappedSequence.getStop()).getRightPhysBound();


        if (phyStop > bounds.getPhyRight()) {
            phyStop = bounds.getPhyRight();
        }
        // set a minimum length to be displayed, otherwise a high zoomlevel could
        // lead to dissapearing features
        double length = phyStop - phyStart;
        if (length < 3) {
            length = 3;
        }

        if (phyStart < bounds.getPhyRight() && phyStop > bounds.getPhyLeft()) {
            JMappedSequence jMappedSequence = new JMappedSequence(length);
            int yFrom = yCoord - (jMappedSequence.getHeight() / 2);
            jMappedSequence.setBounds((int) phyStart, yFrom, jMappedSequence.getSize().width,
                    jMappedSequence.getHeight());
            return jMappedSequence;
        }
        return null;
    }

    private void setInLayer(MappedSequenceHolder mappedSequence) {
        ArrayList<Layer> layers;
        if (!layersMap.containsKey(mappedSequence.getIdentity())) {
            layers = new ArrayList<>();
            IdentityLayer identityLayer = new IdentityLayer();
            identityLayer.setLayers(layers);
            layersMap.put(mappedSequence.getIdentity(), identityLayer);
        } else {
            layers = layersMap.get(mappedSequence.getIdentity()).getLayers();
        }

        for (Layer layer : layers) {
            if (layer.getHorizontalEnd() < mappedSequence.getStart()) {
                mappedSequence.setLayer(layer.getVerticalPosition());
                layer.setHorizontalEnd(mappedSequence.getStop());
                layer.addSequence(mappedSequence);
                IdentityLayer identityLayer = new IdentityLayer();
                identityLayer.setLayers(layers);
                layersMap.put(mappedSequence.getIdentity(), identityLayer);
                return;
            }
        }

        Layer layer;
        if (layers.isEmpty()) {
            layer = new Layer(0);
            mappedSequence.setLayer(0);
        } else {
            layer = new Layer(layers.get(layers.size() - 1).getVerticalPosition() + 1);
            mappedSequence.setLayer(layer.getVerticalPosition());
        }
        layer.setHorizontalEnd(mappedSequence.getStop());
        layer.addSequence(mappedSequence);
        layers.add(layer);
        IdentityLayer identityLayer = new IdentityLayer();
        identityLayer.setLayers(layers);
        layersMap.put(mappedSequence.getIdentity(), identityLayer);
    }

    private void createJComponents() {
        this.removeAll();

        Iterator<Integer> iter = layersMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer identity = iter.next();
            boolean found = false;
            for (Layer layer : layersMap.get(identity).getLayers()) {
                for (MappedSequenceHolder mappedSequence : layer.getSequences()) {
                    JMappedSequence jMappedSequence = transformMappedSequenceToJComponent(mappedSequence);
                    if (jMappedSequence != null) {
                        layer.addJComponent(jMappedSequence);
                        found = true;
                    }

                }
            }
            if (!found) {
                iter.remove();
            }
        }
        int offset = 0;
        for (int i = 100; i > -1; i--) {
            if (layersMap.containsKey(i)) {
                layersMap.get(i).setVerticalPosition(offset);
                offset += MARGIN_TO_NEXT_IDENTITY_LAYER;
                int layerPosition = 0;
                for (Layer layer : layersMap.get(i).getLayers()) {
                    for (JMappedSequence jMappedSequence : layer.getJComponents()) {
                        Rectangle bounds = jMappedSequence.getBounds();
                        bounds.setLocation(bounds.getLocation().x, bounds.getLocation().y + offset + this.verticalMargin);
                        jMappedSequence.setBounds(bounds);
                        jMappedSequence.setBorder(BorderFactory.createLineBorder(ColorProperties.IDENTITY_COLORS[i]));
                        add(jMappedSequence);
                        if (layerPosition < layer.getVerticalPosition()) {
                            layerPosition = layer.getVerticalPosition();
                        }
                    }
                }
                offset += MARGIN_TO_NEXT_IDENTITY_BORDER + layerPosition;
            }
        }
        this.repaint();
    }

    /**
     * Draws a line for a frame.
     *
     * @param g the graphics to paint on
     * @param lineColor Color for Frame lines
     * @param yCord the y-coordinate to start painting at
     * @param label the frame to paint
     * @param stringColor Color for Identity strings
     */
    private void drawIdentityLine(Graphics2D g, Color lineColor, int yCord, String label, Color stringColor) {
        int labelHeight = g.getFontMetrics().getMaxAscent();
        int labelWidth = g.getFontMetrics().stringWidth(label);

        int maxLeft = getPaintingAreaInfo().getPhyLeft();
        int maxRight = getPaintingAreaInfo().getPhyRight();

        g.setColor(stringColor);

        // draw left label
        g.drawString(label, maxLeft - LABEL_MARGIN - labelWidth, yCord + labelHeight);
        // draw right label
        g.drawString(label, maxRight + LABEL_MARGIN, yCord + labelHeight);

        // assign space for label and some extra space
        int x1 = maxLeft;
        int x2 = maxRight;

        g.setColor(lineColor);

        int linewidth = 15;
        int i = x1;
        while (i <= x2 - linewidth) {
            g.drawLine(i, yCord, i + linewidth, yCord);
            i += 2 * linewidth;
        }
        if (i <= x2) {
            g.drawLine(i, yCord, x2, yCord);
        }
    }

    @Override
    public void afterLoadingSequences(Iterator<MappedSequence> iter) {
        layersMap.clear();
        MappedSequence seq;
        while (iter.hasNext()) {
            seq = iter.next();
            setInLayer(new MappedSequenceHolder(seq.getStart(), seq.getStop(), seq.getIdentity()));
        }
        try {
            createJComponents();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        this.repaint();
    }

    @Override
    protected void adjustAreaInfoHook() {
        paintingAreaInfo.setForwardLow(this.getSize().height / 2 - 1);
        paintingAreaInfo.setReverseLow(this.getSize().height / 2 + 1);
    }
}
