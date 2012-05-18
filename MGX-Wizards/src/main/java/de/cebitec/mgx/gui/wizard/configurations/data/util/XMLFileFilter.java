/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.data.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author pbelmann
 */
public class XMLFileFilter extends FileFilter{

    @Override
    public boolean accept(File f) {
       return f.getName().toLowerCase().endsWith(".xml") || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "XML-File(*.xml)";
    }
    
}
