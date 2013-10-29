/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.reference.referenceView;

import de.cebitec.mgx.gui.reference.dataVisualisation.BoundsInfo;

/**
 *
 * @author belmann
 */
public interface ICurrentBounds {

    public BoundsInfo getCurrentBounds();

    public void setCurrentBounds(BoundsInfo bounds);
}