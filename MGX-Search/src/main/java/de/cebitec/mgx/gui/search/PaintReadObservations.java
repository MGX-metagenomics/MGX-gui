package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.datamodel.Observation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 * Malt die einzelnen Observations.
 * @author pbelmann
 */
public class PaintReadObservations {

    
    /**
     * Laenge des Reads.
     */
    private int readLength;
    
    /**
     * Observations.
     */
    private ArrayList<Observation> observations;
    
    /**
     * Die Breite der Observations und der Reads.
     */
    private final int ObservationReadWidth = 8;
    
    /**
     * Das Padding zum oberen Rand des Panels.
     */
    private final int paddingNorth = 15;
    
    /**
     * Graphics Objekt.
     */
    private Graphics graphics;
    
    /**
     * Skalierungsfaktor fuer den Read und den Observations.
     */
    private double scaleFactor;
    
    /**
     * Ebenen fuer die Reads.
     */
    private ArrayList<Layer> layers;
    
    /**
     * Array fuer die Farben, die verwendet werden bei den Reads.
     */
    private Color[] colors = {Color.BLUE, Color.magenta, Color.BLACK,
        new Color(0, 100, 0), Color.gray, new Color(97, 73, 46),};
    
    /**
     * JPanel fuer die Darstellung der Reads und Observations.
     */
    private JPanel component;
    
    /**
     * Hoehe fuer das JPanel.
     */
    private int height;
    
   /**
    * Abschnitte auf dem Read.
    */
    private double cutValue;
    
    /**
     * Zeichnen der Observations.
     * @param readLength Laenge des Read.
     * @param layers Ebenen fuer die Observations.
     * @param lGraphics Graphisobjekt. 
     * @param lComponent Komponente fuer das Darstellen der Reads.
     * @param cutValue Wert in denen der Read eingeteilt wird.
     */
    public PaintReadObservations(double readLength,
            ArrayList<Layer> layers, Graphics lGraphics,  JPanel lComponent, double cutValue) {
        this.cutValue = cutValue;
        this.component = lComponent;
        this.layers = layers;
        this.scaleFactor = ((double)lComponent.getWidth()) / readLength;
        this.readLength = (int) readLength;
        graphics = lGraphics;
        height = component.getHeight();
        paint(graphics);
    }

    /**
     * Zeichnen den Read und die Observations.
     * @param lGraphics Graphics objekt.
     */
    private void paint(Graphics lGraphics) {
        this.drawRead(readLength, lGraphics);
        this.drawObservations(observations, lGraphics);
    }

    
    /**
     * Zeichnet den Read.
     * @param lReadLength Laenge des Read.
     * @param lGraphics Graphics objekt fuer das Zeichnen.
     */
    private void drawRead(int lReadLength, Graphics lGraphics) {
        lGraphics.setColor(Color.red);
        int scaledReadLength = (int) Math.round(lReadLength * scaleFactor);
        drawArrowHead(0, scaledReadLength, lGraphics, paddingNorth);
        lGraphics.fillRect(0, paddingNorth - 4, scaledReadLength - 7, ObservationReadWidth);

        int factor = 0;
        while (cutValue * factor < readLength) {

            lGraphics.setColor(Color.RED);
            lGraphics.drawLine((int) (Math.round(cutValue) * scaleFactor * factor), 0,
                    (int) (Math.round(cutValue) * scaleFactor * factor), height);

            lGraphics.setColor(Color.BLACK);
            FontMetrics fm = component.getFontMetrics(component.getFont());

            int multFactor = (int) cutValue * factor;
            if (fm.stringWidth(" " + multFactor) + (int) (Math.round(cutValue) * scaleFactor * factor) > scaledReadLength) {
                lGraphics.drawString(" " + (int) cutValue * factor, ((int) (Math.round(cutValue) * scaleFactor * factor))
                        - fm.stringWidth(" " + multFactor), paddingNorth - 5);
            } else {
                lGraphics.drawString(" "
                        + (int) cutValue * factor, (int) (Math.round(cutValue) * scaleFactor * factor), paddingNorth - 5);
            }
            factor++;
        }
    }

    /**
     * Zeichnet den Pfeil beim Read und bei den Observations.
     * @param lSequenceStart Start der Observations
     * @param lSequenceStop Stop der Observations.
     * @param lGraphics Graphics Objekt
     * @param yPosition y Position der Ebene.
     */
    private void drawArrowHead(int lSequenceStart, int lSequenceStop, Graphics lGraphics, int yPosition) {
        Polygon p = new Polygon();
        if (lSequenceStart < lSequenceStop) {
            p.addPoint(lSequenceStop, yPosition);
            p.addPoint(lSequenceStop - 7, yPosition - 5);
            p.addPoint(lSequenceStop - 7, yPosition + 4);
        } else {
            p.addPoint(lSequenceStop, yPosition);
            p.addPoint(lSequenceStop + 7, yPosition - 5);
            p.addPoint(lSequenceStop + 7, yPosition + 4);
        }
        lGraphics.fillPolygon(p);
    }

    /**
     * Zeichnet alle Observations fuer die jeweilige Ebene.
     *
     * @param observations Observations
     * @param lGraphics Graphics
     */
    private void drawObservations(ArrayList<Observation> observations,
            Graphics lGraphics) {
       
        
        int scaledObservationStop;
        int scaledObservationStart;
        int colorIndex = 0;
        int observationCounter = 0;
        
        for (Layer layer : layers) {
            for (Observation observation : layer.getObservations()) {

                scaledObservationStop = (int) Math.round(observation.getStop() * scaleFactor);
                scaledObservationStart = (int) Math.round(observation.getStart() * scaleFactor);

                lGraphics.setColor(colors[colorIndex]);
                colorIndex = incrementColorIndex(colorIndex);
                if (scaledObservationStart < scaledObservationStop) {

                    lGraphics.fillRect(scaledObservationStart, layer.getyPosition() - 4, (scaledObservationStop - 7) - scaledObservationStart, ObservationReadWidth);
                    this.drawArrowHead(scaledObservationStart, scaledObservationStop, lGraphics, layer.getyPosition());

                    lGraphics.setColor(Color.WHITE);
                    lGraphics.drawString(Integer.toString(observationCounter), scaledObservationStart + 10, layer.getyPosition() + 4);
                } else {
                    lGraphics.fillRect(scaledObservationStop + 7, layer.getyPosition() - 4, scaledObservationStart - (scaledObservationStop + 7), ObservationReadWidth);
                    this.drawArrowHead(scaledObservationStart, scaledObservationStop, lGraphics, layer.getyPosition());
                    lGraphics.setColor(Color.WHITE);
                    lGraphics.drawString(Integer.toString(observationCounter), scaledObservationStart - 10, layer.getyPosition() + 4);
                }
                observationCounter++;
            }
        }
    }

    /**
     * Erhoet den Index fuer das Farbarray.
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
}
