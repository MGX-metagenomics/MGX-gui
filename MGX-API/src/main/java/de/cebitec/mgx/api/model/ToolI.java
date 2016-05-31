/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class ToolI extends Identifiable<ToolI> {
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(ToolI.class, "ToolI");

    public ToolI(MGXMasterI m) {
        super(m, DATA_FLAVOR);
    }

    public abstract String getAuthor();

    public abstract ToolI setAuthor(String author);

    public abstract String getDescription();

    public abstract ToolI setDescription(String description);

    public abstract String getName();

    public abstract ToolI setName(String name);

    public abstract String getUrl();

    public abstract ToolI setUrl(String url);

    public abstract Float getVersion();

    public abstract ToolI setVersion(Float version);

    public abstract String getXML();

    public abstract ToolI setXML(String xmlData);

    @Override
    public int compareTo(ToolI o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public abstract boolean equals(Object object);
    
}
