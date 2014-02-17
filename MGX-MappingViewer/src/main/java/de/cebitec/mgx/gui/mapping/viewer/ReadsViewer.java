/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer;

import de.cebitec.mgx.gui.mapping.sequences.MappedSequenceHolder;
import de.cebitec.mgx.gui.mapping.sequences.JMappedSequence;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfoManager;
import de.cebitec.mgx.gui.mapping.viewer.positions.PaintingAreaInfo;
import de.cebitec.mgx.gui.mapping.loader.Loader;
import de.cebitec.mgx.gui.mapping.misc.ColorProperties;
import de.cebitec.mgx.gui.mapping.viewer.positions.panel.ReadsBasePanel;
import de.cebitec.mgx.gui.mapping.sequences.ReferenceHolder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author belmann
 */
public class ReadsViewer extends AbstractViewer<MappedSequence> {

    public ArrayList<MappedSequenceHolder> reads;
    private static int height = 1000;
    private HashMap<Integer, List<JMappedSequence>> jReads;
    private static final Logger log = Logger.getLogger(TopComponentViewer.class.getName());
    private int labelMargin = 3;

    public ReadsViewer(BoundsInfoManager boundsInfoManager, ReadsBasePanel basePanel, ReferenceHolder refGenome, Loader loader) {
        super(boundsInfoManager, basePanel, refGenome, loader);
        reads = new ArrayList();
        jReads = new LinkedHashMap<>();
        super.setVerticalMargin(10);
        super.setHorizontalMargin(40);
        this.setViewerSize();

        for (int i = 0; i < 100; i++) {
            layersMap.put(i, new ArrayList<Layer>());
        }
    }

    @Override
    protected int getMaximalHeight() {
        return height;
    }

    @Override
    public void changeToolTipText(int logPos) {
    }

    @Override
    public void boundsChangedHook() {
    }

    private void setViewerSize() {
        this.setPreferredSize(new Dimension(1, 230));
        this.revalidate();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        Iterator<Integer> iter = this.yCoordMap.keySet().iterator();

        while (iter.hasNext()) {
            Integer identity = iter.next();
            Integer yCoord = yCoordMap.get(identity);
            this.drawIdentityLine(g, ColorProperties.IDENTITY_LINES, yCoord + this.verticalMargin, identity.toString(), ColorProperties.IDENTITY_COLORS[identity]);
        }
    }

    private void addReadComponent(MappedSequenceHolder read) {
        int yCoord = read.getLayer();

        PaintingAreaInfo bounds = getPaintingAreaInfo();

//        if (!this.getExcludedFeatureTypes().contains(feature.getType())) {
//        byte border = JRegion.BORDER_NONE;
        // get left boundary of the feature
        double phyStart = this.getPhysBoundariesForLogPos(read.getStart()).getLeftPhysBound();
        if (phyStart < bounds.getPhyLeft()) {
            phyStart = bounds.getPhyLeft();
//            border = JRegion.BORDER_LEFT;
        }

        // get right boundary of the feature
        double phyStop = this.getPhysBoundariesForLogPos(read.getStop()).getRightPhysBound();


        if (phyStop > bounds.getPhyRight()) {
            phyStop = bounds.getPhyRight();
//            border = border == JRegion.BORDER_LEFT ? JRegion.BORDER_BOTH : JRegion.BORDER_RIGHT;
        }
        // set a minimum length to be displayed, otherwise a high zoomlevel could
        // lead to dissapearing features
        double length = phyStop - phyStart;
        if (length < 3) {
            length = 3;
        }


        if (phyStart < bounds.getPhyRight() && phyStop > bounds.getPhyLeft()) {
            JMappedSequence jRead = new JMappedSequence(read, length);
            int yFrom = yCoord - (jRead.getHeight() / 2);
            jRead.setBounds((int) phyStart, yFrom, jRead.getSize().width, jRead.getHeight());

            if (jReads.containsKey(read.getIdentity())) {
                this.jReads.get(read.getIdentity()).add(jRead);
            } else {
                ArrayList<JMappedSequence> list = new ArrayList<>();
                list.add(jRead);
                jReads.put(read.getIdentity(), list);
            }
//            this.jReads.put(j, jRead);
        }
    }
    public int maxHeight = 0;

    private synchronized void resetSequences(Iterator<MappedSequence> iter) {
        reads.clear();
        MappedSequence seq;
        layersMap.clear();
        this.yCoordMap.clear();
        MappedSequenceHolder read = null;

        while (iter.hasNext()) {
            seq = iter.next();
            read = computePositionIdentity(new MappedSequenceHolder(seq.getStart(), seq.getStop(), seq.getIdentity()));
            reads.add(read);
        }
        try {
            createSequences();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        this.repaint();
    }
    private Map<Integer, ArrayList<Layer>> layersMap = new LinkedHashMap<>();

    private MappedSequenceHolder computePositionIdentity(MappedSequenceHolder read) {
        ArrayList<Layer> layers = layersMap.get(read.getIdentity());
        if (!layersMap.containsKey(read.getIdentity())) {
            layers = new ArrayList<Layer>();
            layersMap.put(read.getIdentity(), layers);
        }

        boolean found = false;
        for (Layer layer : layers) {
            if (layer.end < read.getStart()) {
                read.setLayer(layer.position);
                layer.setEnd(read.getStop());
                found = true;
                break;
            }
        }

        if (!found) {
            Layer layer;
            if (layers.isEmpty()) {
                layer = new Layer(0);
                read.setLayer(0);
                layer.setEnd(read.getStop());
            } else {
                layer = new Layer(layers.get(layers.size() - 1).position + 1);
                read.setLayer(layer.position);
                layer.setEnd(read.getStop());
            }
            layers.add(layer);
            layersMap.put(read.getIdentity(), layers);
        }
        return read;
    }
    private int marginToNextIdentity = 5;
    private HashMap<Integer, Integer> yCoordMap = new LinkedHashMap<Integer, Integer>();

    protected void createSequences() {
        this.removeAll();
        this.repaint();
        jReads.clear();

        for (MappedSequenceHolder read : reads) {
            this.addReadComponent(read);
        }
        int offset = 0;
        ArrayList<Color> color = new ArrayList<Color>(Arrays.asList(ColorProperties.IDENTITY_COLORS));

        for (int i = 100; i > -1; i--) {
            if (jReads.containsKey(i)) {
                yCoordMap.put(i, offset);
                offset += marginToNextIdentity;
                int layer = 0;
                for (JMappedSequence jRead : jReads.get(i)) {
                    Rectangle bounds = jRead.getBounds();
                    bounds.setLocation(bounds.getLocation().x, bounds.getLocation().y + offset + this.verticalMargin);
                    jRead.setBounds(bounds);
                    jRead.setBorder(BorderFactory.createLineBorder(color.get(i)));
                    add(jRead);
                    if (layer < jRead.getSequence().getLayer()) {
                        layer = jRead.getSequence().getLayer();
                    }
                }
                offset += this.marginToNextIdentity + layer;
            }
        }

        this.repaint();
    }

    /**
     * Draws a line for a frame.
     *
     * @param g the graphics to paint on
     * @param yCord the y-coordinate to start painting at
     * @param label the frame to paint
     */
    private void drawIdentityLine(Graphics2D g, Color lineColor, int yCord, String label, Color stringColor) {
        int labelHeight = g.getFontMetrics().getMaxAscent();
        int labelWidth = g.getFontMetrics().stringWidth(label);

        int maxLeft = getPaintingAreaInfo().getPhyLeft();
        int maxRight = getPaintingAreaInfo().getPhyRight();

        g.setColor(stringColor);

        // draw left label
        g.drawString(label, maxLeft - labelMargin - labelWidth, yCord + labelHeight);
        // draw right label
        g.drawString(label, maxRight + labelMargin, yCord + labelHeight);

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
    protected void beforeLoadingSequences() {
    }

    @Override
    public void afterLoadingSequences(Iterator<MappedSequence> iter) {
        this.resetSequences(iter);
    }

    @Override
    protected void adjustAreaInfoHook() {
        paintingAreaInfo.setForwardLow(this.getSize().height / 2 - 1);
        paintingAreaInfo.setReverseLow(this.getSize().height / 2 + 1);
    }

    public class Layer {

        int end = 0;

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
        int position = 0;

        public Layer(int position) {
            this.position = this.position + position;
        }
    }
}
