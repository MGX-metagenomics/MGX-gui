package de.cebitec.mgx.gui.wizard.habitat;

import org.jxmapviewer.viewer.GeoPosition;


/**
 *
 * @author sjaenick
 */
public class Location {

    private GeoPosition geoLoc;
    private String name;

    public Location(GeoPosition pgeoLoc, String pname) {
        geoLoc = pgeoLoc;
        name = pname;
    }

    /**
     * @return the geoLoc
     */
    public GeoPosition getGeoLoc() {
        return geoLoc;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}
