package de.cebitec.mgx.gui.wizard.habitatold;

import org.jdesktop.swingx.mapviewer.GeoPosition;

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
     * @param geoLoc the geoLoc to set
     */
    public void setGeoLoc(GeoPosition geoLoc) {
        this.geoLoc = geoLoc;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
