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

    int getMax();

    int getMin();

    int getStart();

    int getStop();

    public boolean isFwdStrand();

    /**
     * @return 1, 2, 3, -1, -2, -3 depending on the reading frame of the feature
     */
    public int getFrame();

}
