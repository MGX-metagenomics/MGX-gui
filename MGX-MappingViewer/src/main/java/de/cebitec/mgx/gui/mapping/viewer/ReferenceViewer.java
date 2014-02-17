package de.cebitec.mgx.gui.mapping.viewer;

import de.cebitec.mgx.gui.mapping.misc.ColorProperties;
import de.cebitec.mgx.gui.mapping.sequences.ReferenceHolder;
import de.cebitec.mgx.gui.mapping.sequences.ISequenceHolder;
import de.cebitec.mgx.gui.mapping.sequences.RegionHolder;
import de.cebitec.mgx.gui.mapping.sequences.JRegion;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfoManager;
import de.cebitec.mgx.gui.mapping.viewer.positions.PaintingAreaInfo;
import de.cebitec.mgx.gui.mapping.viewer.positions.panel.SequenceBar;
import de.cebitec.mgx.gui.mapping.loader.Loader;
import de.cebitec.mgx.gui.mapping.viewer.positions.panel.ReferenceBasePanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Viewer for genome sequences / chromosomes.
 *
 * @author ddoppmeier, rhilker
 */
public class ReferenceViewer extends AbstractViewer<Region> {

    private final static long serialVersionUID = 7964236;
    private static int height = 250;
    private static int FRAMEHEIGHT = 20;
    private int labelMargin;
    private ArrayList<JRegion> features;
    public final static String PROP_FEATURE_STATS_CHANGED = "feats changed";
    public final static String PROP_FEATURE_SELECTED = "feat selected";
    public static final String PROP_EXCLUDED_FEATURE_EVT = "excl feat evt";
    protected ArrayList<de.cebitec.mgx.gui.datamodel.Region> list;
    private SequenceBar seqBar;

    /**
     * Creates a new reference viewer.
     *
     * @param boundsInfoManager the global bounds info manager
     * @param basePanel the base panel
     * @param referenceHolder the persistant reference, which is always
     * accessible through the getReference method in any abstract viewer.
     */
    public ReferenceViewer(BoundsInfoManager boundsInfoManager, ReferenceBasePanel basePanel, ReferenceHolder referenceHolder, Loader loader) {
        super(boundsInfoManager, basePanel, referenceHolder, loader);
        this.features = new ArrayList();
//        this.showSequenceBar(true, true);

//        if (showSeqBar) {
        this.seqBar = new SequenceBar(this, referenceHolder);
        super.centerSeqBar = true;
//        } else {
//            seqBar = null;
//        }
        this.updatePhysicalBounds();


        this.labelMargin = 3;
        list = new ArrayList();
        this.list = new ArrayList<>();
        this.setViewerSize();
    }

    @Override
    public void close() {
        super.close();
        this.features.clear();
    }

    @Override
    public int getMaximalHeight() {
        return height;
    }

    @Override
    public void boundsChangedHook() {
        this.seqBar.boundsChanged();
    }

    /**
     * Creates all feature components to display in this viewer.
     */
    protected void createSequences() {
//        System.out.println("ReferenceViewer:" + super.paintingAreaInfo.toString());
        this.removeAll();
        this.features.clear();

        if (this.hasLegend()) {
            this.add(this.getLegendLabel());
            this.add(this.getLegendPanel());
        }
//        if (this.hasSequenceBar()) {
        this.add(this.seqBar);
//        }

        List<RegionHolder> featureList = new ArrayList<>();
        boolean isForward = true;
        int start = 0;
        int stop = 0;
        for (Region reg : list) {

            if (reg.getStart() > reg.getStop()) {
                start = reg.getStop();
                stop = reg.getStart();
                isForward = false;
            } else {
                start = reg.getStart();
                stop = reg.getStop();
                isForward = true;
            }

            RegionHolder feature = new RegionHolder(reg.getId(), start, stop, isForward, reg.getName());
            featureList.add(feature);
        }
        //refGenConnector.getFeaturesForRegion(
        //  getBoundsInfo().getLogLeft(), getBoundsInfo().getLogRight(), FeatureType.ANY);

        for (RegionHolder feature : featureList) {
            feature.setFrame(this.determineFrame(feature));
            this.addFeatureComponent(feature);
        }


//        List<Polytree> featureTrees = PersistantFeature.Utils.createFeatureTrees(featureList);
//
//        int frame = 0;
//
//        for (Polytree featTree : featureTrees) { //this means if two roots are on different frames,
//            for (Node root : featTree.getRoots()) { //all children are painted on the frame of the last root node
//                frame = this.determineFrame((PersistantFeature) root);
//            }
//            PaintNodeVisitor paintVisitor = new PaintNodeVisitor(frame);
//            featTree.bottomUp(paintVisitor);
//        }

        //Correct painting order is guaranteed by the node visitor
        for (JRegion jFeature : this.features) {
            this.add(jFeature);
        }

//        firePropertyChange(PROP_FEATURE_STATS_CHANGED, null, featureStats);
    }

    /**
     * Creates a feature component for a given feature and adds it to the
     * reference viewer.
     *
     * @param feature the feature to add to the viewer.
     */
    private void addFeatureComponent(RegionHolder feature) {
        int frame = feature.getFrame();
        int yCoord = this.determineYFromFrame(frame);
        PaintingAreaInfo bounds = getPaintingAreaInfo();

//        if (!this.getExcludedFeatureTypes().contains(feature.getType())) {
        byte border = JRegion.BORDER_NONE;
        // get left boundary of the feature
        double phyStart = this.getPhysBoundariesForLogPos(feature.getStart()).getLeftPhysBound();
        if (phyStart < bounds.getPhyLeft()) {
            phyStart = bounds.getPhyLeft();
            border = JRegion.BORDER_LEFT;
        }

        // get right boundary of the feature
        double phyStop = this.getPhysBoundariesForLogPos(feature.getStop()).getRightPhysBound();
        if (phyStop > bounds.getPhyRight()) {
            phyStop = bounds.getPhyRight();
            border = border == JRegion.BORDER_LEFT ? JRegion.BORDER_BOTH : JRegion.BORDER_RIGHT;
        }

        // set a minimum length to be displayed, otherwise a high zoomlevel could
        // lead to dissapearing features
        double length = phyStop - phyStart;
        if (length < 3) {
            length = 3;
        }

        JRegion jFeature = new JRegion(feature, length, this, border);
        int yFrom = yCoord - (jFeature.getHeight() / 2);
        jFeature.setBounds((int) phyStart, yFrom, jFeature.getSize().width, jFeature.getHeight());
        this.features.add(jFeature);
//        }
    }

    private int determineYFromFrame(int frame) {
        int result;
        int offset = Math.abs(frame) * FRAMEHEIGHT;

        if (frame < 0) {
            result = this.getPaintingAreaInfo().getReverseLow();
            result += offset;
        } else {
            result = this.getPaintingAreaInfo().getForwardLow();
            result -= offset;
        }
        return result;
    }

    /**
     * @param feature feature whose frame has to be determined
     * @return 1, 2, 3, -1, -2, -3 depending on the reading frame of the feature
     */
    public int determineFrame(RegionHolder feature) {
        int frame;

        if (feature.isFwdStrand()) { // forward strand
            frame = (feature.getStart() - 1) % 3 + 1;
        } else { // reverse strand. start <= stop ALWAYS! so use stop for reverse strand
            frame = (feature.getStop() - 1) % 3 - 3;
        }
        return frame;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        // draw lines for frames
        g.setColor(ColorProperties.FRAME_LINES);
        this.drawScales(g);
    }

    /**
     * Draws the lines as orientation for each frame.
     *
     * @param g the graphics object to paint in.
     */
    private void drawScales(Graphics2D g) {
        this.drawSingleScaleLine(g, this.determineYFromFrame(1), "+1");
        this.drawSingleScaleLine(g, this.determineYFromFrame(2), "+2");
        this.drawSingleScaleLine(g, this.determineYFromFrame(3), "+3");
        this.drawSingleScaleLine(g, this.determineYFromFrame(-1), "-1");
        this.drawSingleScaleLine(g, this.determineYFromFrame(-2), "-2");
        this.drawSingleScaleLine(g, this.determineYFromFrame(-3), "-3");
    }

    /**
     * Draws a line for a frame.
     *
     * @param g the graphics to paint on
     * @param yCord the y-coordinate to start painting at
     * @param label the frame to paint
     */
    private void drawSingleScaleLine(Graphics2D g, int yCord, String label) {
        int labelHeight = g.getFontMetrics().getMaxAscent();
        int labelWidth = g.getFontMetrics().stringWidth(label);

        int maxLeft = getPaintingAreaInfo().getPhyLeft();
        int maxRight = getPaintingAreaInfo().getPhyRight();

        // draw left label
        g.drawString(label, maxLeft - labelMargin - labelWidth, yCord + labelHeight / 2);
        // draw right label
        g.drawString(label, maxRight + labelMargin, yCord + labelHeight / 2);

        // assign space for label and some extra space
        int x1 = maxLeft;
        int x2 = maxRight;

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
    public void changeToolTipText(int logPos) {
        if (this.isMouseOverPaintingRequested()) {
            this.setToolTipText(String.valueOf(logPos));
        } else {
            this.setToolTipText("");
        }
    }

    /**
     * Sets the initial size of the reference viewer.
     */
    private void setViewerSize() {
        this.setPreferredSize(new Dimension(1, 230));
        this.revalidate();
    }

//    @Override
    public void resetSequences(Iterator<Region> iter) {
        list.clear();
        while (iter.hasNext()) {
            list.add((Region) iter.next());
        }
        createSequences();
        this.repaint();
    }

    @Override
    protected void beforeLoadingSequences() {
    }

    @Override
    public void afterLoadingSequences(Iterator<Region> iter) {
        this.resetSequences(iter);
    }

    @Override
    protected void adjustAreaInfoHook() {

        if (centerSeqBar) {
            int y1 = this.getSize().height / 2 - seqBar.getSize().height / 2;
            int y2 = this.getSize().height / 2 + seqBar.getSize().height / 2;
            seqBar.setBounds(0, y1, paintingAreaInfo.getPhyRight(), seqBar.getSize().height);
            paintingAreaInfo.setForwardLow(y1 - 1);
            paintingAreaInfo.setReverseLow(y2 + 1);
        } else {
            seqBar.setBounds(0, 20, this.getSize().width, seqBar.getSize().height);
            paintingAreaInfo.setForwardLow(20 - 1);
            paintingAreaInfo.setReverseLow(seqBar.getSize().height + 21);
        }
    }
}
