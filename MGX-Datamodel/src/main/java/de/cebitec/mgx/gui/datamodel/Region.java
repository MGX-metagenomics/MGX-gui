/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel;

import static de.cebitec.mgx.gui.datamodel.Reference.DATA_FLAVOR;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author belmann
 */
public class Region extends Identifiable {
    

    protected long reference_id;

    private String name;

    private String description;

    private int start;

    private int stop;

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(Region.class, "Region");

    public Region() {
        super(DATA_FLAVOR);
    }
    
    public long getReferenceId() {
        return reference_id;
    }

    public void setReference(Long reference_id) {
        this.reference_id = reference_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }
}
