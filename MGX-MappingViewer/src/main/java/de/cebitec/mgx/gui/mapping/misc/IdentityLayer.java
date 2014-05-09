/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.misc;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author belmann
 */
public class IdentityLayer {

    private List<Layer> layers;
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

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public void addLayer(Layer layer) {
        this.layers.add(layer);
    }
}
