/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.datamodel.Observation;
import java.util.ArrayList;

/**
 * Berechnet die Position der der Observations.
 *
 *
 * @author pbelmann
 */
public class OrderedObservations {

    /**
     * Gibt die Laenge des Reads an.
     */
    private double readLength;
    
    /**
     * Observations
     */
    private ArrayList<Observation> observations;
   
    /**
     * Abstand nach oben zum Rand.
     */
    private final int paddingNorth = 15;
    
    /**
     * Abstand zwischen den einzelnen Observations.
     */
    private final int paddingBetween = 10;
    
    /**
     * Alle Observations aufgeteilt in die einzelnen Layer.
     */
    private ArrayList<Layer> layers;
    
    /**
     * Konstruktor.
     * @param lReadLength Laenge des Reads.
     * @param lObservations Observations, die angeordnet werden sollen.
     */
    public OrderedObservations(double lReadLength,
            ArrayList<Observation> lObservations) {

        this.readLength = lReadLength;
        this.observations = lObservations;
        paint();
    }

    /**
     * Zeichnet die Observations.
     */
    private void paint() {
        computeObservations(observations);
    }

    /**
     * Gibt die Laenge des Reads wider.
     * @return Laenge des Reads.
     */
    public double getReadLength() {
        return readLength;
    }

    /**
     * Zeichnet alle Observations fuer die jeweilige Ebene.
     *
     * @param observations Observations die angeordnet werden sollen.
     * @param lGraphics Graphics der jeweiligen Komponente.
     */
    private void computeObservations(ArrayList<Observation> observations) {

        int yPosition = paddingNorth + paddingBetween;
        layers = new ArrayList<Layer>();
        layers.add(new Layer(yPosition));
        boolean isObservationSet = false;

        for (Observation observation : observations) {

            isObservationSet = false;
            for (Layer layer : layers) {
                if (layer.fits(observation)) {
                    layer.addObservation(observation);
                    isObservationSet = true;
                    break;
                }
            }
            if (!isObservationSet) {
                Layer layer =
                        new Layer(layers.get(layers.size() - 1).getyPosition() + paddingBetween);
                layer.addObservation(observation);
                layers.add(layer);
            }
        }
    }

   

    /**
     * Gibt alle Ebenen wieder.
     *
     * @return Ebene
     */
    public ArrayList<Layer> getLayers() {
        return layers;
    }
}
