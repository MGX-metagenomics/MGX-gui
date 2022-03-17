/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

/**
 *
 * @author sjaenick
 */
public interface LocationI {

    public int getMax();

    public int getMin();

    public int getStart();

    public int getStop();

    public int getLength();

    /**
     * @return 1, 2, 3, -1, -2, -3 depending on the reading frame of the feature
     */
    public int getFrame();

}
