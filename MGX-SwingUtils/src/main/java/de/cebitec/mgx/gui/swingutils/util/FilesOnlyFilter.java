/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.swingutils.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author sj
 */
public class FilesOnlyFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.isFile();
    }

    @Override
    public String getDescription() {
        return "Files";
    }

}
