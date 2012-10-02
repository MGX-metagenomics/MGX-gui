package de.cebitec.mgx.gui.search;

import de.cebitec.mgx.gui.datamodel.Observation;
import de.cebitec.mgx.gui.search.ComputeObservations.Layer;
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
 * TODO:
 *
 * nummerierung der observations muessen passen, auch wenn die observations zu
 * kurz sind.
 *
 *
 *
 *
 * @author pbelmann
 */
public class PaintObservations {

    private int readLength;
    private ArrayList<Observation> observations;
    private int width = 8;
    private int paddingNorth = 15;
    private Graphics graphics;
    private double scaleFactor;
    private ArrayList<Layer> layers;
    private Color[] colors = {Color.BLUE, Color.magenta, Color.BLACK,
        new Color(0, 100, 0), Color.gray, new Color(97, 73, 46),};
    private Component component;
    private int height;
private double value;
    
    
    public PaintObservations(double readLength,
            ArrayList<Layer> layers, Graphics lGraphics, double lViewWidth, Component lComponent, int lViewHeight, double value) {
        this.value = value;
        this.component = lComponent;
        this.layers = layers;
        this.scaleFactor = lViewWidth / readLength;
        this.readLength = (int) readLength;
        graphics = lGraphics;
        height = lViewHeight;
        paint(graphics);
    }

    private void paint(Graphics lGraphics) {
        this.drawRead(readLength, lGraphics);
        this.drawObservations(observations, lGraphics);
    }

    private void drawRead(int lReadLength, Graphics lGraphics) {
        lGraphics.setColor(Color.red);
        int scaledReadLength = (int) Math.round(lReadLength * scaleFactor);
        drawArrowHead(0, scaledReadLength, lGraphics, paddingNorth);
        lGraphics.fillRect(0, paddingNorth - 4, scaledReadLength - 7, width);


       

        //TODO Testen:!!
        

        


//        if (readLength <= 100) {
//
//            value /= 10.0;
//            value = Math.round(value) * 10.0;
//
//        } else if (readLength <= 1000) {
//
//            value /= 100.0;
//            value = Math.round(value) * 100.0;
//
//        } else if (readLength < 10000) {
//        }

        int factor = 0;
        while (value * factor < readLength) {

            lGraphics.setColor(Color.RED);
//            lGraphics.drawLine((int) (Math.round(value) * scaleFactor * factor), paddingNorth,
//                    (int) (Math.round(value) * scaleFactor * factor), paddingNorth - 12);
            lGraphics.drawLine((int) (Math.round(value) * scaleFactor * factor), 0,
                    (int) (Math.round(value) * scaleFactor * factor), height);


            lGraphics.setColor(Color.BLACK);

            FontMetrics fm = component.getFontMetrics(component.getFont());

            int multFactor = (int) value * factor;
            if (fm.stringWidth(" " + multFactor) + (int) (Math.round(value) * scaleFactor * factor) > scaledReadLength) {
                lGraphics.drawString(" " + (int) value * factor, ((int) (Math.round(value) * scaleFactor * factor))
                        - fm.stringWidth(" " + multFactor), paddingNorth - 5);
            } else {
                lGraphics.drawString(" "
                        + (int) value * factor, (int) (Math.round(value) * scaleFactor * factor), paddingNorth - 5);
            }

            factor++;
        }
    }

    private void drawArrowHead(int lObservationStart, int lObservationStop, Graphics lGraphics, int yPosition) {
        Polygon p = new Polygon();
        if (lObservationStart < lObservationStop) {
            p.addPoint(lObservationStop, yPosition);
            p.addPoint(lObservationStop - 7, yPosition - 5);
            p.addPoint(lObservationStop - 7, yPosition + 4);
        } else {
            p.addPoint(lObservationStop, yPosition);
            p.addPoint(lObservationStop + 7, yPosition - 5);
            p.addPoint(lObservationStop + 7, yPosition + 4);
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

                    lGraphics.fillRect(scaledObservationStart, layer.getyPosition() - 4, (scaledObservationStop - 7) - scaledObservationStart, width);
                    this.drawArrowHead(scaledObservationStart, scaledObservationStop, lGraphics, layer.getyPosition());

                    lGraphics.setColor(Color.WHITE);
                    lGraphics.drawString(Integer.toString(observationCounter), scaledObservationStart + 10, layer.getyPosition() + 4);
                } else {
                    lGraphics.fillRect(scaledObservationStop + 7, layer.getyPosition() - 4, scaledObservationStart - (scaledObservationStop + 7), width);
                    this.drawArrowHead(scaledObservationStart, scaledObservationStop, lGraphics, layer.getyPosition());
                    lGraphics.setColor(Color.WHITE);
                    lGraphics.drawString(Integer.toString(observationCounter), scaledObservationStart - 10, layer.getyPosition() + 4);
                }
                observationCounter++;
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
}
