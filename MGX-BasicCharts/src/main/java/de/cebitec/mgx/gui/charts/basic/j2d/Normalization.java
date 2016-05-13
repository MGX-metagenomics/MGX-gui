/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.charts.basic.j2d;

/**
 *
 * @author sj
 */
public enum Normalization {
    DISABLED, // default, normalize to max of all visualization groups
    ROOT, // normalize to rank with maximum assigned sequences 
    ALL;
}
