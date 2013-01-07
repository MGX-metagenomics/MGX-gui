
package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.datamodel.Observation;
import java.util.ArrayList;
import java.util.List;


 /**
     * Klasse stellt eine Ebene mit passend angeordneten Observations dar.
     */
    public class Layer {

        /**
         * Liste von Observations, die in der Ebene gezeichnet werden sollen.
         */
        private List<Observation> observations;
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
            observations = new ArrayList<>();
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
         * 
         * Gibt die Observations wider.
         * @return the observations
         */
        public List<Observation> getObservations() {
            return observations;
        }
    }
