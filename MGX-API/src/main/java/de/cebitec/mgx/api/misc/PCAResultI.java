/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.misc;

import java.util.List;

/**
 *
 * @author sj
 */
public interface PCAResultI {

    void addLoading(Point p);

    void addPoint(Point p);

    List<Point> getDatapoints();

    List<Point> getLoadings();

    double[] getVariances();
    
}
