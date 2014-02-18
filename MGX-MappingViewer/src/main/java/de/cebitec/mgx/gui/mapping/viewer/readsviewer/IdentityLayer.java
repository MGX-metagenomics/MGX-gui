/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer.readsviewer;

import java.util.ArrayList;

/**
 *
 * @author belmann
 */
public class IdentityLayer {

    private ArrayList<Layer> layers;
    private int verticalPosition;

    public IdentityLayer() {
        this.layers = new ArrayList<>();
    }

    public int getVerticalPosition() {
        return verticalPosition;
    }

    public void setVerticalPosition(int verticalPosition) {
        this.verticalPosition = verticalPosition;
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<Layer> layers) {
        this.layers = layers;
    }

    public void addLayer(Layer layer) {
        this.layers.add(layer);
    }
}
