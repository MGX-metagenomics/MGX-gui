package de.cebitec.mgx.gui.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class MGXDirectory extends MGXFile {
    
    protected List<DirEntry> entries = new ArrayList<>();

    public List<DirEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<DirEntry> entries) {
        this.entries = entries;
    }
}
