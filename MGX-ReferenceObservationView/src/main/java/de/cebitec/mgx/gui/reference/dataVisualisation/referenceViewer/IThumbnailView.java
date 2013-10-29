package de.cebitec.mgx.gui.reference.dataVisualisation.referenceViewer;

//import de.cebitec.vamp.controller.ViewController;
//import de.cebitec.vamp.databackend.dataObjects.PersistantFeature;
import excluded.PersistantFeature;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * @author dkramer
 */
public interface IThumbnailView {
    /**
     * This method is used after selecting an feature for which all tracks for a given reference should be viewed in Thumbnails.
     * @param feature
     * @param refViewer the currently viewed ReferenceViewer
     */
    public void addFeatureToList(PersistantFeature feature, ReferenceViewer refViewer);

    public void showThumbnailView(ReferenceViewer refViewer);

//    public void showThumbnailView(ReferenceViewer refViewer, ViewController con);

    public void removeAllFeatures(ReferenceViewer refViewer);

    public void removeCertainFeature(PersistantFeature feature);

    public void showPopUp(PersistantFeature feature, ReferenceViewer refViewer, MouseEvent e, JPopupMenu popUp);

    public void showTablePopUp(JTable table, ReferenceViewer refViewer, MouseEvent e);

}
