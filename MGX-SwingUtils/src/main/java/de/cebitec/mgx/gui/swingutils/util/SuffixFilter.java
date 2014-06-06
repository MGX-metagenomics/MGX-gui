package de.cebitec.mgx.gui.swingutils.util;

import de.cebitec.mgx.api.groups.FileType;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author sjaenick
 */
public class SuffixFilter extends FileFilter {

    private final FileType ft;

    public SuffixFilter(FileType ft) {
        this.ft = ft;
    }

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return true;
        }
        for (String suffix : ft.getSuffices()) {
            if (pathname.getName().endsWith("." + suffix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return ft.getDescription();
    }

    public FileType getType() {
        return ft;
    }
}
