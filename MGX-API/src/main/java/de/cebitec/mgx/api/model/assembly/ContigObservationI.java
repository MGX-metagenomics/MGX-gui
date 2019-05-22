/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model.assembly;

/**
 *
 * @author sj
 */
public abstract class ContigObservationI {

    public abstract int getStart();

    public abstract int getStop();

    public abstract ContigAttributeI getAttribute();

}
