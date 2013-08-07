package de.cebitec.vamp.view.dataVisualisation.abstractViewer;

//import de.cebitec.vamp.databackend.dataObjects.PersistantReference;
//import de.cebitec.vamp.util.Properties;
import de.cebitec.vamp.view.dataVisualisation.BoundsInfo;
import de.cebitec.vamp.view.dataVisualisation.HighlightAreaListener;
import excluded.PersistantReference;
import excluded.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
//import org.openide.util.NbPreferences;

/**
 * @author Rolf Hilker
 *
 * Manages the detection and highlighting of regions in a sequence bar.
 * It currently supports start and stop codons and exact sequence region highlighting.
 */
public class RegionManager {

    private SequenceBar regionVisualizer; //component that visualizes the regions
    private AbstractViewer parentViewer; //the viewer, in which the sequence bar is embedded
    private StartCodonFilter codonFilter;
    private PatternFilter patternFilter;
    private HighlightAreaListener highlightListener;
    private Preferences pref;
    private List<Region> cdsRegions;

    /**
     * Manages the detection and highlighting of regions in a sequence bar. It
     * currently supports start and stop codons and exact sequence region
     * highlighting.
     * @param regionVisualizer the sequence bar, on which the regions are shown
     * @param parentViewer the viewer, in which the sequence bar is embedded
     * @param refGen the reference genome
     * @param highlightListener the listener for highlighting a sequence of choice and
     *          displaying a corresponding menu. It is needed here, because all regions
     *          detected by this manager play a special role for this listener.
     */
    public RegionManager(SequenceBar regionVisualizer, AbstractViewer parentViewer,
                PersistantReference refGen, HighlightAreaListener highlightListener) {

        this.regionVisualizer = regionVisualizer;
        this.parentViewer = parentViewer;
        this.highlightListener = highlightListener;
        this.cdsRegions = new ArrayList();

        BoundsInfo bounds = parentViewer.getBoundsInfo();
        this.codonFilter = new StartCodonFilter(bounds.getLogLeft(), bounds.getLogRight(), refGen);
        this.patternFilter = new PatternFilter(bounds.getLogLeft(), bounds.getLogRight(), refGen);
        this.initPrefListener();
    }

    /**
     * Updates the sequence bar according to the genetic code chosen. After
     * changing the genetic code, no start codons are be selected anymore.
     */
    private void initPrefListener() {
       // this.pref = NbPreferences.forModule(Object.class);
        //keine Initialisation von NbPreferences
//	 this.pref.addPreferenceChangeListener(new PreferenceChangeListener() {
//
//            @Override
//            public void preferenceChange(PreferenceChangeEvent evt) {
//                if (evt.getKey().equals(Properties.SEL_GENETIC_CODE)) {
//                    RegionManager.this.codonFilter.resetCodons();
//                    RegionManager.this.findCodons();
//                }
//            }
//        });
    }

    /**
     * Calculates which start codons should be highlighted and updates the gui.
     * @param i the index of the codon to update
     * @param isSelected true, if the codon should be selected
     */
    public void showStartCodons(final int i, final boolean isSelected) {
        this.codonFilter.setStartCodonSelected(i, isSelected);
        this.findCodons();
    }

    /**
     * Calculates which stop codons should be highlighted and updates the gui.
     * @param i the index of the codon to update
     * @param isSelected true, if the codon should be selected
     */
    public void showStopCodons(final int i, final boolean isSelected) {
        this.codonFilter.setStopCodonSelected(i, isSelected);
        this.findCodons();
    }

    /**
     * Returns if the codon with the index i is currently selected.
     * @param i the index of the codon
     * @return true, if the codon with the index i is currently selected
     */
    public boolean isStartCodonShown(final int i) {
        return this.codonFilter.isStartCodonSelected(i);
    }

    /**
     * Detects the occurences of the given pattern in the currently shown interval
     * or the next occurence of the pattern in the genome.
     * @param pattern Pattern to search for
     * @return the closest position of the next occurrence of the pattern
     */
    public int showPattern(String pattern) {
        this.patternFilter.setPattern(pattern);
        return this.findPattern();
    }

    /**
     * If the list of CDS regions is not empty, then they are transformed into a
     * JRegion and displayed in the regionVisualizer (SequenceBar).
     */
    public void showCdsRegions() {
        this.regionVisualizer.removeAll(Properties.CDS);

        if (!this.cdsRegions.isEmpty()) {
            for (Region region : this.cdsRegions) {
                JRegion cdsJRegion = this.regionVisualizer.transformRegionToJRegion(region);
                this.regionVisualizer.add(cdsJRegion);
            }
        }
        this.regionVisualizer.repaint();
    }

    /**
     * Identifies the codons according to the currently selected codons to show
     * and adds JRegions for highlighting into the sequence bar.
     */
    public void findCodons() {
        //create the list of component types, that should be removed (only patterns)
        List<Byte> typeList = new ArrayList();
        typeList.add(Properties.START);
        typeList.add(Properties.STOP);
        this.regionVisualizer.removeAll(typeList);
        this.highlightListener.clearSpecialRegions();
        this.codonFilter.setInterval(this.parentViewer.getBoundsInfo().getLogLeft(), this.parentViewer.getBoundsInfo().getLogRight());
        this.regionVisualizer.determineFeatureFrame();
        byte frameCurrFeature = this.regionVisualizer.getFrameCurrFeature();

        this.codonFilter.setCurrFeatureData(frameCurrFeature);
        List<Region> codonHitsToHighlight = this.codonFilter.findRegions();
        for (Region region : codonHitsToHighlight) {

            JRegion cdsJRegion = this.regionVisualizer.transformRegionToJRegion(region);
            this.highlightListener.addSpecialRegion(cdsJRegion);
            this.regionVisualizer.add(cdsJRegion);
        }
        this.regionVisualizer.repaint();
    }

    /**
     * Identifies the currently in this object stored pattern in the genome sequence.
     * @return position of the next occurence of the pattern from the current position on.
     */
    public int findPattern() {
        //create the list of component types, that should be removed (only patterns)
        this.regionVisualizer.removeAll(Properties.PATTERN);
        this.patternFilter.setInterval(this.parentViewer.getBoundsInfo().getLogLeft(), this.parentViewer.getBoundsInfo().getLogRight());

        List<Region> patternHitsToHighlight = this.patternFilter.findRegions();
        for (Region region : patternHitsToHighlight) {

            JRegion patternRegion = this.regionVisualizer.transformRegionToJRegion(region);
            this.regionVisualizer.add(patternRegion);
        }
        this.regionVisualizer.repaint();

        if (patternHitsToHighlight.isEmpty()){
            return this.patternFilter.findNextOccurrence();
        } else {
            return -2;
        }
    }

    /**
     * Identifies next (closest) occurrence from either forward or reverse strand of a pattern
     * in the current reference genome.
     * @return the position of the next occurrence of the pattern
     */
    public int findNextPatternOccurrence(){
        return this.patternFilter.findNextOccurrence();
    }

    public void setCdsRegions(List<Region> cdsRegions) {
        if (this.cdsRegions.containsAll(cdsRegions)) {
            this.cdsRegions.removeAll(cdsRegions);
        } else {
            this.cdsRegions = cdsRegions;
        }
        this.showCdsRegions();
    }
}
