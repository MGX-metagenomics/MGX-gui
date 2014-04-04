package de.cebitec.mgx.gui.groups;

import de.cebitec.mgx.gui.util.FileType;

/**
 *
 * @author sjaenick
 */
public interface ImageExporterI {
    
    public FileType[] getSupportedTypes();

    public boolean export(FileType type, String fName) throws Exception;
}
