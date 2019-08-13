/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.swingutils;

import java.awt.Color;
import java.awt.Shape;

/**
 *
 * @author sj
 */
public interface ShapeBase extends Shape, Comparable<ShapeBase> {

    String getToolTipText();

    Color getColor();

}
