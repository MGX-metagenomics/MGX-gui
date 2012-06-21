/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.utilities;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filtert fuer den FileChooser die XMLDateien heraus.
 *
 * @author pbelmann
 */
public class XMLFileFilter extends FileFilter {

    /**
     * Akzeptiert nur XML DateiTypen.
     *
     * @param f File
     * @return Akzeptiert oder nicht.
     */
    @Override
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".xml") || f.isDirectory();
    }

    /**
     * Gibt die Beschreibung des DateiTyps zurueck.
     *
     * @return
     */
    @Override
    public String getDescription() {
        return "XML File (*.xml)";
    }
}
