/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import java.awt.Polygon;

/**
 *
 * @author sj
 */
public class Arrow extends Polygon {

    public final static int FORWARD = 1;
    public final static int REVERSE = 2;

    /*
     *                  4
     *                  |\
     *     2------------3 \
     *     |               \5
     *     |               /
     *     1------------7 /
     *                  |/
     *                  6
     *
     *        2    
     *       /| 
     *      / 3-------------4   
     *    1/                |
     *     \                |
     *      \ 6-------------5
     *       \|
     *        7
     *
     */
    public Arrow(int direction, int length) {

        int height = 14;
        int mid = height / 2;

        if (direction == FORWARD) {
            this.addPoint(0, mid + 4);       // 1
            this.addPoint(0, mid - 4);       // 2
            this.addPoint(length - 8, mid - 4);   // 3
            this.addPoint(length - 8, mid - 8);   // 4
            this.addPoint(length, mid); //5
            this.addPoint(length - 8, mid + 8); //6
            this.addPoint(length - 8, mid + 4); //7
            this.addPoint(0, mid + 4); // 8/1
        } else if (direction == REVERSE) {
            this.addPoint(0, mid);  // 1
            this.addPoint(5, mid - 8); //2
            this.addPoint(5, mid - 4); //3
            this.addPoint(length, mid - 4); //4
            this.addPoint(length, mid + 4); //5
            this.addPoint(5, mid + 4); //6
            this.addPoint(5, mid + 8); //7
            this.addPoint(0, mid); //8
        } else {
            throw new IllegalArgumentException();
        }

    }

}
