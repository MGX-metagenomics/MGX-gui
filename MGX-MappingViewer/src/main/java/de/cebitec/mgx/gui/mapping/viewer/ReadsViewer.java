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
import de.cebitec.mgx.gui.mapping.viewer.positions.panel.ReadsBasePanel;
import de.cebitec.mgx.gui.mapping.sequences.ReferenceHolder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
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
    private ArrayList<JMappedSequence> jReads;
    private static final Logger log = Logger.getLogger(TopComponentViewer.class.getName());

    public ReadsViewer(BoundsInfoManager boundsInfoManager, ReadsBasePanel basePanel, ReferenceHolder refGenome, Loader loader) {
        super(boundsInfoManager, basePanel, refGenome, loader);
        reads = new ArrayList();
        jReads = new ArrayList();
        super.setVerticalMargin(10);
        super.setHorizontalMargin(40);
        this.setViewerSize();
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
        this.setBorder(BorderFactory.createLineBorder(Color.GREEN));

//        SwingWorker worker = new SwingWorker<Void, Void>() {
//            @Override
//            protected Void doInBackground() throws Exception {
//
//                try {
//                    createSequences();
//                } catch (Exception e) {
//                }
//
//
//                return null;
//            }
//        };
//        worker.execute();


//        log.info("draw something!!");

//        SwingWorker worker = new SwingWorker<Void, Void>() {
//            @Override
//            protected Void doInBackground() throws Exception {
//
//                try {
//                    createSequences();
//                } catch (Exception e) {
//                }
//
//
//                return null;
//            }
//        };
//        worker.execute();





//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void setViewerSize() {
        this.setPreferredSize(new Dimension(1, 230));
        this.revalidate();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);


    }
    int position = 10;

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

            this.jReads.add(jRead);
        }
    }
    public int maxHeight = 0;
    private ArrayList<Layer> layers = new ArrayList<>();

    private synchronized void resetSequences(Iterator<MappedSequence> iter) {
        reads.clear();
        position = 0;
        MappedSequence seq;
        layers.clear();
        MappedSequenceHolder read = null;

        while (iter.hasNext()) {
            seq = iter.next();
//            if (seq.getIdentity() == 100) {
                read = computePosition(new MappedSequenceHolder(seq.getStart(), seq.getStop()));
//            }
           
            reads.add(read);
        }
        try {
            createSequences();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        this.repaint();
    }

    private MappedSequenceHolder computePosition(MappedSequenceHolder read) {


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
        }
        return read;
    }

    protected void createSequences() {
        this.removeAll();
        this.repaint();
        jReads.clear();
//        System.out.println("ReadsViewer:" + super.paintingAreaInfo.toString());

        for (MappedSequenceHolder read : reads) {
            if (maxHeight < read.getLayer()) {
                maxHeight = read.getLayer();
            }
            this.addReadComponent(read);
        }
        for (JMappedSequence jRead : jReads) {
            jRead.setBorder(BorderFactory.createLineBorder(Color.BLUE));
//            if (jRead.read.getLayer() > this.maxHeight - 10) {
//                jRead.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
//            } else {
//               
//            }
            add(jRead);
        }
        this.repaint();
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

    private static boolean overlaps(MappedSequenceHolder r1, MappedSequenceHolder r2) {
        return (r1.getStart() >= r2.getStart() && r1.getStart() <= r2.getStop())
                || (r1.getStop() >= r2.getStart() && r1.getStop() <= r2.getStop())
                || (r1.getStart() <= r2.getStart() && r1.getStop() >= r2.getStop())
                || (r1.getStop() <= r2.getStart() && r1.getStart() >= r2.getStop());
    }
}
