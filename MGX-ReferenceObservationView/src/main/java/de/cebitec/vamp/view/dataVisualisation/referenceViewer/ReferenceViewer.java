package de.cebitec.vamp.view.dataVisualisation.referenceViewer;

//import de.cebitec.vamp.databackend.connector.ProjectConnector;
//import de.cebitec.vamp.databackend.connector.ReferenceConnector;
//import de.cebitec.vamp.databackend.dataObjects.PersistantFeature;
//import de.cebitec.vamp.databackend.dataObjects.PersistantFeatureI;
//import de.cebitec.vamp.databackend.dataObjects.PersistantReference;
//import de.cebitec.vamp.util.ColorProperties;

//import de.cebitec.vamp.util.FeatureType;
import de.cebitec.vamp.util.polyTree.Node;
import de.cebitec.vamp.util.polyTree.NodeVisitor;
import de.cebitec.vamp.util.polyTree.Polytree;
import de.cebitec.vamp.view.dataVisualisation.BoundsInfoManager;
import de.cebitec.vamp.view.dataVisualisation.abstractViewer.AbstractViewer;
import de.cebitec.vamp.view.dataVisualisation.abstractViewer.PaintingAreaInfo;

import de.cebitec.vamp.view.dataVisualisation.basePanel.BasePanel;
//import de.cebitec.vamp.view.dataVisualisation.basePanel.BasePanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

//neu
import excluded.*;

/**
 * Viewer for genome sequences / chromosomes.
 *
 * @author ddoppmeier, rhilker
 */
public class ReferenceViewer extends AbstractViewer {

    private final static long serialVersionUID = 7964236;
    private static int height = 250;
    private static int FRAMEHEIGHT = 20;
    private Map<FeatureType, Integer> featureStats;
    private JFeature currentlySelectedFeature;
    private int labelMargin;
//    private ReferenceConnector refGenConnector;
    private ArrayList<JFeature> features;

    public final static String PROP_FEATURE_STATS_CHANGED = "feats changed";
    public final static String PROP_FEATURE_SELECTED = "feat selected";
    public static final String PROP_EXCLUDED_FEATURE_EVT = "excl feat evt";
    private int trackCount = 0;

    /**
     * Creates a new reference viewer.
     * @param boundsInfoManager the global bounds info manager
     * @param basePanel the base panel
     * @param refGenome the persistant reference, which is always accessible through the getReference
     *      method in any abstract viewer.
     */
    public ReferenceViewer(BoundsInfoManager boundsInfoManager, BasePanel basePanel, PersistantReference refGenome){
        super(boundsInfoManager, basePanel, refGenome);
        this.features = new ArrayList();
      //  this.refGenConnector = ProjectConnector.getInstance().getRefGenomeConnector(refGenome.getId());
        this.featureStats = new EnumMap(FeatureType.class);
        this.getExcludedFeatureTypes().add(FeatureType.UNDEFINED);
        this.showSequenceBar(true, true);
        this.labelMargin = 3;
        this.setViewerSize();
    }


    public void setSelectedFeature(JFeature feature){

        firePropertyChange(PROP_FEATURE_SELECTED, currentlySelectedFeature, feature);

        // if the currently selected feature is clicked again, de-select it
        if (currentlySelectedFeature == feature){
            currentlySelectedFeature.setSelected(false);
            currentlySelectedFeature = null;
        } else {

            // if there was a feature selected before, de-select it
            if (currentlySelectedFeature != null){
                currentlySelectedFeature.setSelected(false);
            }

            currentlySelectedFeature = feature;
            currentlySelectedFeature.setSelected(true);
        }

        //only recalculate if reading frame was switched
        if (currentlySelectedFeature == null || this.getSequenceBar().getFrameCurrFeature() != this.determineFrame(currentlySelectedFeature.getPersistantFeature())){
            this.getSequenceBar().findCodons(); //update codons for current selection
        }
    }


    @Override
    public void close(){
        super.close();
    //    this.refGenConnector = null;
        this.featureStats.clear();
        this.features.clear();
        this.getExcludedFeatureTypes().clear();
    }

    @Override
    public int getMaximalHeight() {
        return height;
    }

    @Override
    public void boundsChangedHook() {
        // TODO compute this outside of EDT if too timeconsuming
        this.createFeatures();

//        firePropertyChange(PROP_INTERVAL_CHANGED, null, getBoundsInfo());
    }

    /**
     * Creates all feature components to display in this viewer.
     */
    private void createFeatures() {
        this.removeAll();
        this.features.clear();
        this.featureStats.clear();

        if (this.hasLegend()) {
            this.add(this.getLegendLabel());
            this.add(this.getLegendPanel());
        }
        if (this.hasSequenceBar()) {
            this.add(this.getSequenceBar());
        }

	  PersistantFeature feature = new PersistantFeature(13, "11", "enum", "locus", "product",35, 50, true, FeatureType.CDS, "name");

	  feature.setFrame(1);



        //new Feature List:
        List<PersistantFeature> featureList = new ArrayList<PersistantFeature>();
	  featureList.add(feature);

		//refGenConnector.getFeaturesForRegion(
              //  getBoundsInfo().getLogLeft(), getBoundsInfo().getLogRight(), FeatureType.ANY);
        List<Polytree> featureTrees = PersistantFeature.Utils.createFeatureTrees(featureList);

        int frame = 0;
        for (Polytree featTree : featureTrees) { //this means if two roots are on different frames,
            for (Node root : featTree.getRoots()) { //all children are painted on the frame of the last root node
                frame = this.determineFrame((PersistantFeature) root);
            }
            PaintNodeVisitor paintVisitor = new PaintNodeVisitor(frame);
            featTree.bottomUp(paintVisitor);
        }

        //Correct painting order is guaranteed by the node visitor
        for (JFeature jFeature : this.features) {
            this.add(jFeature);
        }

        firePropertyChange(PROP_FEATURE_STATS_CHANGED, null, featureStats);
    }

    /**
     * Registers the feature in the viewer statistics for displaying information
     * about the currently viewed reference interval.
     * @param feature the feature to register
     */
    private void registerFeatureInStats(PersistantFeatureI feature) {
        FeatureType type = feature.getType();
        if (!this.featureStats.containsKey(type)) {
            this.featureStats.put(type, 0);
        }
        this.featureStats.put(type, this.featureStats.get(type) + 1);
    }

    /**
     * Creates a feature component for a given feature and adds it to the reference viewer.
     * @param feature the feature to add to the viewer.
     */
    private void addFeatureComponent(PersistantFeature feature) {
        int frame = feature.getFrame();
        int yCoord = this.determineYFromFrame(frame);
        PaintingAreaInfo bounds = getPaintingAreaInfo();

        if (!this.getExcludedFeatureTypes().contains(feature.getType())) {
            byte border = JFeature.BORDER_NONE;
            // get left boundary of the feature
            double phyStart = this.getPhysBoundariesForLogPos(feature.getStart()).getLeftPhysBound();
            if (phyStart < bounds.getPhyLeft()) {
                phyStart = bounds.getPhyLeft();
                border = JFeature.BORDER_LEFT;
            }

            // get right boundary of the feature
            double phyStop = this.getPhysBoundariesForLogPos(feature.getStop()).getRightPhysBound();
            if (phyStop > bounds.getPhyRight()) {
                phyStop = bounds.getPhyRight();
                border = border == JFeature.BORDER_LEFT ? JFeature.BORDER_BOTH : JFeature.BORDER_RIGHT;
            }

            // set a minimum length to be displayed, otherwise a high zoomlevel could
            // lead to dissapearing features
            double length = phyStop - phyStart;
            if (length < 3) {
                length = 3;
            }

            JFeature jFeature = new JFeature(feature, length, this, border);
            int yFrom = yCoord - (jFeature.getHeight() / 2);
            jFeature.setBounds((int) phyStart, yFrom, jFeature.getSize().width, jFeature.getHeight());
            if (currentlySelectedFeature != null) {
                if (feature.getId() == currentlySelectedFeature.getPersistantFeature().getId()) {
                    setSelectedFeature(jFeature);
                }
            }
            this.features.add(jFeature);
        }
    }

    private int determineYFromFrame(int frame){
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
    public int determineFrame(PersistantFeature feature) {
        int frame;

        if (feature.isFwdStrand()) { // forward strand
            frame = (feature.getStart() - 1) % 3 + 1;
        } else { // reverse strand. start <= stop ALWAYS! so use stop for reverse strand
            frame = (feature.getStop() - 1) % 3 - 3;
        }
        return frame;
    }

    @Override
    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;

        // draw lines for frames
        g.setColor(ColorProperties.TRACKPANEL_SCALE_LINES);
        this.drawScales(g);
    }

    /**
     * Draws the lines as orientation for each frame.
     * @param g the graphics object to paint in.
     */
    private void drawScales(Graphics2D g){
        this.drawSingleScaleLine(g, this.determineYFromFrame(1), "+1");
        this.drawSingleScaleLine(g, this.determineYFromFrame(2), "+2");
        this.drawSingleScaleLine(g, this.determineYFromFrame(3), "+3");
        this.drawSingleScaleLine(g, this.determineYFromFrame(-1), "-1");
        this.drawSingleScaleLine(g, this.determineYFromFrame(-2), "-2");
        this.drawSingleScaleLine(g, this.determineYFromFrame(-3), "-3");
    }

    /**
     * Draws a line for a frame.
     * @param g the graphics to paint on
     * @param yCord the y-coordinate to start painting at
     * @param label the frame to paint
     */
    private void drawSingleScaleLine(Graphics2D g, int yCord, String label){
        int labelHeight = g.getFontMetrics().getMaxAscent();
        int labelWidth = g.getFontMetrics().stringWidth(label);

        int maxLeft = getPaintingAreaInfo().getPhyLeft();
        int maxRight = getPaintingAreaInfo().getPhyRight();

        // draw left label
        g.drawString(label, maxLeft-labelMargin-labelWidth, yCord+ labelHeight/2);
        // draw right label
        g.drawString(label, maxRight+labelMargin, yCord+ labelHeight/2);

        // assign space for label and some extra space
        int x1 = maxLeft;
        int x2 = maxRight;

        int linewidth = 15;
        int i = x1;
        while(i<=x2-linewidth){
            g.drawLine(i, yCord, i+linewidth, yCord);
            i += 2*linewidth;
        }
        if(i<=x2){
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
     * @return The feature statistics for the currently viewed reference
     * interval.
     */
    public Map<FeatureType, Integer> getFeatureStats() {
        return this.featureStats;
    }

    /**
     * @return The currently selected feature by the user.
     */
    public JFeature getCurrentlySelectedFeature() {
        return this.currentlySelectedFeature;
    }

    /**
     * Sets the initial size of the reference viewer.
     */
    private void setViewerSize() {
        this.setPreferredSize(new Dimension(1, 230));
        this.revalidate();
    }

    /**
     * Increases count of corresponding tracks.
     * If more information is needed implement listener model
     * with possibility to get track viewers.
     */
    public void increaseTrackCount() {
        ++this.trackCount;
    }

    /**
     * Decreases count of corresponding tracks.
     * If more information is needed implement listener model
     * with possibility to get track viewers.
     */
    public void decreaseTrackCount(){
        if (this.trackCount > 0){
            --this.trackCount;
        } //nothing to do if it is already 0
    }

    /**
     * @return Number of corresponding tracks.
     */
    public int getTrackCount(){
        return this.trackCount;
    }

    /**
     * Visitor creating the <tt>JFeature</tt> to display and adding it to the
     * viewer stats for the visited feature. Also updates the frame according to
     * the <tt>frame</tt> set in the constructor.
     */
    private class PaintNodeVisitor implements NodeVisitor {

        private int frame;

        /**
         * Visitor creating the <tt>JFeature</tt> to display and adding it to
         * the viewer stats for the visited feature. Also updates the frame
         * according to the <tt>frame</tt> set in the constructor.
         */
        public PaintNodeVisitor(int frame) {
            this.frame = frame;
        }

        @Override
        public void visit(Node node) {
            if (node instanceof PersistantFeature) {
                PersistantFeature feature = (PersistantFeature) node;
                feature.setFrame(frame);
                addFeatureComponent(feature);
                registerFeatureInStats(feature);
            }
        }
    }

}
