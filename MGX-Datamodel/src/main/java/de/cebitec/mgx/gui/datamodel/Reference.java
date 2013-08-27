/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel;

import static de.cebitec.mgx.gui.datamodel.DNAExtract.DATA_FLAVOR;
import java.awt.datatransfer.DataFlavor;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author belmann
 */
public class Reference extends Identifiable {

    private String name;

    private int length;
    
    private String filePath;

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(Reference.class, "Reference");

    public Reference() {
        super(DATA_FLAVOR);
    }
    
    public String getFile() {
        return filePath;
    }

    public void setFile(String filePath) {
        this.filePath = filePath;
    }
    
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

