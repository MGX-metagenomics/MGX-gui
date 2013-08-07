package de.cebitec.vamp.view.dataVisualisation;

//import de.cebitec.common.sequencetools.GeneticCode;
//import de.cebitec.common.sequencetools.GeneticCodeFactory;
//import de.cebitec.vamp.databackend.dataObjects.PersistantReference;
//import de.cebitec.vamp.util.Properties;
import de.cebitec.vamp.util.SequenceUtils;
import de.cebitec.vamp.view.dataVisualisation.abstractViewer.JRegion;
import de.cebitec.vamp.view.dataVisualisation.abstractViewer.PatternFilter;
import de.cebitec.vamp.view.dataVisualisation.abstractViewer.Region;
import de.cebitec.vamp.view.dataVisualisation.abstractViewer.SequenceBar;
//import de.cebitec.vamp.view.dialogMenus.MenuItemFactory;
//import de.cebitec.vamp.view.dialogMenus.RNAFolderI;
import excluded.PersistantReference;
import excluded.Properties;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
//import org.openide.util.Lookup;
//import org.openide.util.NbPreferences;

/**
 * Listener for highlighting areas on a sequence bar. Note that classes with a
 * HighlightAreaListener have to implement IHighlightable
 *
 * @author Rolf Hilker
 */
public class HighlightAreaListener extends MouseAdapter {

    private static final int HEIGHT = 12;
    private HashMap<Integer, List<JRegion>> specialRegionList;
    private final SequenceBar parentComponent;
    private final int baseLineY;
    private final int offsetY;
    private int startX;
    private boolean keepPainted;
    private boolean freezeRect;
    private Rectangle highlightRect;
    private boolean isFwdStrand;
    private int seqStart;
    private int seqEnd;

    /**
     * @param parentComponent the component the listener is associated to
     * @param baseLineY the baseline of the vie
     * @param offsetY the y offset from the middle, which determines where to
     * start painting the highlighting rectangle
     */
    public HighlightAreaListener(final SequenceBar parentComponent, final int baseLineY, final int offsetY) {
        this.parentComponent = parentComponent;
        this.baseLineY = baseLineY;
        this.offsetY = offsetY;
        this.startX = -1;
        this.keepPainted = false;
        this.freezeRect = false;
        this.isFwdStrand = true;
        this.specialRegionList = new HashMap<Integer, List<JRegion>>();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        this.isFwdStrand = e.getY() <= this.baseLineY;
        boolean inRect = false;
        if (highlightRect != null) {
            final int x = e.getX();
            inRect = x > highlightRect.x && x < highlightRect.x + highlightRect.width;
        }

        if (this.keepPainted && !inRect) {
            this.keepPainted = false;
            this.freezeRect = false;
            this.setHighlightRectangle(null);
        } else if (inRect) {
            this.showPopUp(e);
        }
        //highlight interval from current start to next stop codon in frame
        if (e.getButton() == MouseEvent.BUTTON1 && this.specialRegionList.containsKey(e.getX())) {
            List<Region> cdsRegions = this.calcCdsRegions(e.getX());
            this.parentComponent.setCdsRegions(cdsRegions); //pass regions to viewer for highlighting

        }
        this.showMouseMenu(e);

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            this.freezeRect = false;
            this.keepPainted = true;
            double baseWidth = this.parentComponent.getBaseWidth();
            this.startX = (int) (Math.round(e.getX() / baseWidth) * baseWidth);
            int yPos = this.baseLineY - 7;
            this.isFwdStrand = e.getY() <= this.baseLineY;
            yPos = this.isFwdStrand ? yPos - this.offsetY : yPos + this.offsetY;

            this.setHighlightRectangle(new Rectangle(this.startX, yPos, 2, HEIGHT));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.freezeRect = true;
        if (!this.keepPainted) {
            this.setHighlightRectangle(null);
            this.freezeRect = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        /*
         * update rectangle according to new mouse position & start position
         * only x value of mouse event is important!
         */
        if (!this.freezeRect) {
            double baseWidth = this.parentComponent.getBaseWidth();
            int x = (int) (Math.round(e.getX() / baseWidth) * baseWidth);
            int xPos = x <= this.startX ? x : this.startX;
            int yPos = this.baseLineY - 7;
            this.isFwdStrand = e.getY() <= this.baseLineY;
            yPos = e.getY() <= this.baseLineY ? yPos - this.offsetY : yPos + this.offsetY;

            this.setHighlightRectangle(new Rectangle(xPos, yPos, Math.abs(x - this.startX), HEIGHT));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.parentComponent.updateMouseListeners(e);
    }

    /**
     * Should be called when the bounds of the parent component changed their
     * hook. We don't want the rectangle to remain in that case. TODO: Implement
     * that rectangle moves with bounds.
     */
    public void boundsChangedHook() {
        this.keepPainted = false;
        this.freezeRect = false;
        this.setHighlightRectangle(null);
    }

    /**
     * Opens the pop up menu showing all available options for the highlighted
     * rectangle.
     * @param e method to be called after a click, so this is the mouse event
     * resulting from that click
     */
    private void showPopUp(MouseEvent e) {

        if ((e.getButton() == MouseEvent.BUTTON3) || (e.isPopupTrigger())) {
            JPopupMenu popUp = new JPopupMenu();
            //MenuItemFactory menuItemFactory = new MenuItemFactory();

            final String selSequence = this.getMarkedSequence();
            final String header = this.getHeader();
            //add copy option

		//popUp.add(menuItemFactory.getCopyItem(selSequence));
            //add copy position option
            //popUp.add(menuItemFactory.getCopyPositionItem(parentComponent.getCurrentMousePosition()));
            //add center current position option
            //popUp.add(menuItemFactory.getJumpToPosItem(this.parentComponent.getBoundsInfoManager(), parentComponent.getCurrentMousePosition()));
            //add store as fasta file option
            //popUp.add(menuItemFactory.getStoreFastaItem(selSequence, seqStart, seqEnd));
            //add calculate secondary structure option
//            final RNAFolderI rnaFolderControl = Lookup.getDefault().lookup(RNAFolderI.class);
          //  if (rnaFolderControl != null) {
         //       popUp.add(menuItemFactory.getRNAFoldItem(rnaFolderControl, selSequence, header));
         //   }


            popUp.show((JComponent) e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Sets the current rectangle both in this class and in the parent
     * component.
     *
     * @param rectangle
     */
    private void setHighlightRectangle(final Rectangle rectangle) {
        this.highlightRect = rectangle;
        this.parentComponent.setHighlightRectangle(this.highlightRect);
    }

    /**
     * Returns the highlighted sequence.
     *
     * @return the highlighted sequence
     */
    private String getMarkedSequence() {
        BoundsInfo bounds = parentComponent.getViewerBoundsInfo();
        final double baseWidth = parentComponent.getBaseWidth();
        final String seq = parentComponent.getPersistantReference().getSequence();
        final int refLength = parentComponent.getPersistantReference().getRefLength();
        int logleft = bounds.getLogLeft() - 1 + Math.round((float) ((highlightRect.x - parentComponent.getViewerHorizontalMargin()) / baseWidth));
        int logright = logleft + (int) (Math.round(highlightRect.width / baseWidth));
        logleft = logleft < 0 ? 0 : logleft;
        logleft = logleft > refLength ? refLength : logleft;
        logright = logright < 0 ? 0 : logright;
        logright = logright > refLength ? refLength : logright;
        String selSequence = seq.substring(logleft, logright);
        if (!isFwdStrand) {
            selSequence = SequenceUtils.getReverseComplement(selSequence);
        }
        this.seqStart = logleft + 1;
        this.seqEnd = logright;
        return selSequence;
    }

    /**
     * Creates the header for the highlighted sequence.
     * @return the header for the sequence
     */
    private String getHeader() {
        final String strand = isFwdStrand ? ">>" : "<<";
        return this.parentComponent.getPersistantReference().getName() + " (" + strand + " " + seqStart + "-" + seqEnd + ")";
    }

    /**
     * @return The parent sequence bar of this listener.
     */
    public SequenceBar getParent() {
        return this.parentComponent;
    }

    /**
     * Allows to add special regions within this sequence bar. These regions
     * receive additional treatment, when the mouse was clicked in one of them.
     *
     * @param jreg a JRegion, which has to be treated in a special way
     */
    public void addSpecialRegion(JRegion jreg) {
        Rectangle bounds = jreg.getBounds();
        for (int pixel = bounds.x; pixel <= bounds.x + bounds.width; ++pixel) {
            if (!this.specialRegionList.containsKey(pixel)) {
                this.specialRegionList.put(pixel, new ArrayList<JRegion>());
            }
            this.specialRegionList.get(pixel).add(jreg);
        }
    }

    /**
     * Shows the complete menu for any position on the sequence bar. This is context
     * sensitive, depending on special regions (start, stop codons and patterns).
     * @param e the mouse event which triggered the call of this method
     */
    private void showMouseMenu(MouseEvent e) {
        int xPos = e.getX();

        if (e.getButton() == MouseEvent.BUTTON3) {


            JPopupMenu popUp = new JPopupMenu();
           // MenuItemFactory menuItemFactory = new MenuItemFactory();

            //add copy position option
            //popUp.add(menuItemFactory.getCopyPositionItem(parentComponent.getCurrentMousePosition()));
            //add center current position option
            //popUp.add(menuItemFactory.getJumpToPosItem(this.parentComponent.getBoundsInfoManager(), parentComponent.getCurrentMousePosition()));

            //add copy CDS option, if on a start codon & on correct frame
            if (this.specialRegionList.containsKey(xPos)) {
                List<Region> cdsRegions = this.calcCdsRegions(xPos);
                if (!cdsRegions.isEmpty()) {
                    this.parentComponent.setCdsRegions(cdsRegions); //pass regions to viewer for highlighting
                    String refName = this.parentComponent.getPersistantReference().getName();
                    final List<String> cdsStrings = this.generateCdsString(cdsRegions);
                   // popUp.add(menuItemFactory.getStoreFastaForCdsItem(cdsStrings, cdsRegions, refName));

                    //add jump to end of CDS option
                    //popUp.add(menuItemFactory.getJumpToStopPosItem(this.parentComponent.getBoundsInfoManager(), cdsRegions));
                }
            }

            // add options for highlighted/selected sequences
            if (this.highlightRect != null) {
                final String selSequence = this.getMarkedSequence();
                final String header = this.getHeader();

                //add copy option
                //popUp.add(menuItemFactory.getCopyItem(selSequence));
                //add store as fasta file option
                //popUp.add(menuItemFactory.getStoreFastaItem(selSequence, seqStart, seqEnd));
                //add calculate secondary structure option
//                final RNAFolderI rnaFolderControl = Lookup.getDefault().lookup(RNAFolderI.class);
          //      if (rnaFolderControl != null) {
         //           popUp.add(menuItemFactory.getRNAFoldItem(rnaFolderControl, selSequence, header));
               // }
            }

            popUp.show((JComponent) e.getComponent(), xPos, e.getY());
        }
    }

    /**
     * Calculates the CDS regions for a pixel in a start codon (given by xPos).
     * @param xPos the pixel position, where the mouse was clicked
     * @return the CDS regions for a pixel in a start codon (given by xPos).
     */
    private List<Region> calcCdsRegions(int xPos) {
        List<JRegion> specialRegions = this.specialRegionList.get(xPos);

        List<Region> cdsRegions = new ArrayList<Region>();
        for (JRegion specialRegion : specialRegions) {

            if (specialRegion.getType() == Properties.START) {
                if (isFwdStrand && specialRegion.getY() < this.baseLineY) {
                    //detect stop pos of special region for fwd strand
                    Region cdsToHighlight = this.findNextStopPos(specialRegion.getStart(), parentComponent.getPersistantReference());
                    cdsRegions.add(cdsToHighlight);
                } else if (!isFwdStrand && specialRegion.getY() > this.baseLineY) {
                    //detect stop pos (which is the start pos in pixels) of special region for rev strand
                    Region cdsToHighlight = this.findNextStopPos(specialRegion.getStop(), parentComponent.getPersistantReference());
                    cdsRegions.add(cdsToHighlight);
                }
            }
        }
        return cdsRegions;
    }

    /**
     *
     * @param start the first position in the correct reading frame, on which stop codons
     *      should be detected.
     * @param reference
     * @return
     */
    private Region findNextStopPos(int start, PersistantReference reference) {

      //  GeneticCode code = GeneticCodeFactory.getGeneticCodeById(Integer.valueOf(NbPreferences.forModule(Object.class).get(Properties.SEL_GENETIC_CODE, "1")));
        List<String> stopCodons = new ArrayList<String>();
//        List<String> stopCodons = code.getStopCodons();
        List<Integer> results = new ArrayList<Integer>();

        int searchStart = isFwdStrand ? start + 3 : start - 3;
        PatternFilter patternFilter = new PatternFilter(searchStart, reference.getRefLength(), reference);
        for (String stop : stopCodons) {
            patternFilter.setPattern(stop.toLowerCase());
            int stopPos = patternFilter.findNextOccurrenceOnStrand(isFwdStrand);
            results.add(stopPos);
        }

        //find closest stop codon in correct frame on correct strand
        int closestStop = isFwdStrand ? Integer.MAX_VALUE : -1;
        for (Integer stop : results) {
            if (isFwdStrand && stop < closestStop || !isFwdStrand && stop > closestStop) {
                closestStop = stop;
            }
        }

        if (isFwdStrand) {
            return new Region(start, closestStop, isFwdStrand, Properties.CDS);
        } else {
            return new Region(closestStop, start, isFwdStrand, Properties.CDS);
        }
    }

    private List<String> generateCdsString(List<Region> cdsRegions) {
        List<String> cdsStrings = new ArrayList<String>();
        String refSeq = parentComponent.getPersistantReference().getSequence();
        for (Region cds : cdsRegions) {
            String cdsSeq = refSeq.substring(cds.getStart() - 1, cds.getStop()).toUpperCase(); //-1 because its an index, not genome pos
            if (!isFwdStrand) {
                cdsSeq = SequenceUtils.getReverseComplement(cdsSeq);
            }
            cdsStrings.add(cdsSeq);
        }
        return cdsStrings;
    }

    public void clearSpecialRegions() {
        this.specialRegionList.clear();
    }
}
