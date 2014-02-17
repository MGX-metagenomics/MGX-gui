/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.sequences;

/**
 *
 * @author belmann
 */
public class MappedSequenceHolder implements ISequenceHolder {

    private int start;
    private int stop;
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

    @Override
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public MappedSequenceHolder(int lStart, int lStop, int lIdentity) {
        this.start = lStart;
        this.identity = lIdentity;
        this.stop = lStop;
    }
}
