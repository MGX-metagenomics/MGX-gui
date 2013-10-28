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
public interface ICurrentBounds {

    public BoundsInfo getCurrentBounds();

    public void setCurrentBounds(BoundsInfo bounds);
}