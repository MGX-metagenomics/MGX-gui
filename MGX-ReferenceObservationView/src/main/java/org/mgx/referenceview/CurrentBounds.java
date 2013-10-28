/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mgx.referenceview;

import de.cebitec.vamp.view.dataVisualisation.BoundsInfo;

/**
 *
 * @author belmann
 */
public interface CurrentBounds  {
    
    public BoundsInfo getBounds();

    public void setBounds(BoundsInfo bounds);
}