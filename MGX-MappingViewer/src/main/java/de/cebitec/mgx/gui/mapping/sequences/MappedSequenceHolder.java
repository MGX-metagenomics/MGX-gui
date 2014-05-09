/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.sequences;

/**
 *
 * @author belmann
 */
public class MappedSequenceHolder extends ISequenceHolder {

    private int layer = 0;
    private int identity = -1;

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public MappedSequenceHolder(int lStart, int lStop, int lIdentity) {
        super(lStart, lStop);
        this.identity = lIdentity;
    }
}
