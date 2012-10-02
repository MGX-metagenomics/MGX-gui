/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.search;


import de.cebitec.mgx.gui.datamodel.Observation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JPanel;
/**
 *
 * @author pbelmann
 */
public class ComputeObservations {
    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author pbelmann
 */


    private int readLength;
    private ArrayList<Observation> observations;
    private int paddingNorth = 15;
    private int paddingBetween =10;
//    private Graphics graphics;
//    private double scaleFactor;
    private ArrayList<Layer> layers;
    private Color[] colors = {Color.BLUE, Color.magenta, Color.BLACK,
        new Color(0, 100, 0), Color.gray, new Color(97, 73, 46),};

    public ComputeObservations(int lReadLength,
            ArrayList<Observation> lObservations,
            int lPaddingBetween, 
           
       double lSeqLength) {

//           lObservations.clear();
//        
//        for(int i=0;i<20;i++){
//        Observation ob = new Observation();
//        ob.setStart(1);
//        ob.setStop((int)lSeqLength);
//        lObservations.add(ob);
//        }

//        this.scaleFactor = lViewWidth / lSeqLength;
  
        this.readLength = lReadLength;
        this.observations = lObservations;

//        graphics = lGraphics;
        paint();
    }

    private void paint() {
//        this.getScaledRead(readLength);
        this.computeObservations(observations);
    }

//    private int scaledReadLength;
    
    public int getReadLength() {
//        scaledReadLength = (int) Math.round(readLength * scaleFactor);
       
    return  readLength;
    }


    /**
     * Zeichnet alle Observations fuer die jeweilige Ebene.
     *
     * @param observations Observations
     * @param lGraphics Graphics
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
     * erhoet den Index fuer das Farbarray.
     *
     * @param oldIndex alter Index
     * @return neuer Index
     */
    private int incrementColorIndex(int oldIndex) {
        if (oldIndex == colors.length - 1) {
            return 0;
        } else {
            return ++oldIndex;
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

    /**
     * Klasse stellt eine Ebene mit den Observations dar.
     */
    public class Layer {

        /**
         * Liste von Observations, die in der Ebene gezeichnet werden sollen.
         */
        private ArrayList<Observation> observations;
        /**
         * y Position der Ebene.
         */
        private int yPosition;

        /**
         * Konstruktor fuer die Ebene.
         *
         * @param lYPosition y Position in der die Observations gezeichnet
         * werden sollen.
         */
        public Layer(int lYPosition) {
            observations = new ArrayList<Observation>();
            yPosition = lYPosition;
        }

        /**
         * Ueberprueft, ob die Observation in die Ebene passt.
         *
         * @param newObservation neue Observation
         * @return Boolean ob es passt oder nicht.
         */
        public boolean fits(Observation newObservation) {

            for (Observation observation : getObservations()) {
                if (newObservation.getStart() > newObservation.getStop()) {
                    if (observation.getStart() < observation.getStop()) {

                        // ---->
                        // <----
                        if (observation.getStop() >= newObservation.getStop()
                                && observation.getStart() <= newObservation.getStart()) {
                            return false;
                        }
                    } else {

                        // <----
                        // <----
                        if (observation.getStop() <= newObservation.getStart()
                                && observation.getStart() >= newObservation.getStop()) {
                            return false;
                        }
                    }
                } else {
                    if (observation.getStart() < observation.getStop()) {

                        // ---->
                        // ---->
                        if (observation.getStart() <= newObservation.getStop()
                                && observation.getStop() >= newObservation.getStart()) {
                            return false;
                        }
                    } else {

                        // <----
                        // ---->
                        if (observation.getStop() <= newObservation.getStop()
                                && observation.getStart() >= newObservation.getStart()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        /**
         *
         * Fuegt eine Observation zu der Ebene hinzu.
         *
         * @param lObservation Observation
         */
        public void addObservation(Observation lObservation) {
            getObservations().add(lObservation);
        }

        /**
         * Gibt die y Position der Ebene wieder.
         *
         * @return yPosition
         */
        public int getyPosition() {
            return yPosition;
        }

        /**
         * @return the observations
         */
        public ArrayList<Observation> getObservations() {
            return observations;
        }
    }


}




