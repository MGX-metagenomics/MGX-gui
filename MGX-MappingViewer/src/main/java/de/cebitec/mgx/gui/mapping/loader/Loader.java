/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.loader;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.mapping.viewer.positions.BoundsInfo;
import de.cebitec.mgx.gui.mapping.viewer.AbstractViewer;
import java.util.logging.Logger;



/**
 *
 * @author belmann
 */
public abstract class Loader 
{

    private static final Logger log = Logger.getLogger(Loader.class.getName());
    protected AbstractViewer viewer;
    volatile BoundsInfo bounds;
    volatile private long currentTime;
    protected MGXMaster master;
    protected Reference reference;

    public Loader(MGXMaster master,  Reference reference) {
        currentTime = System.nanoTime();
        this.master = master;
        this.reference = reference;
        
    }

    abstract public void startWorker(AbstractViewer viewer); 
}
