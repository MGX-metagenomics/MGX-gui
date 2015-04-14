/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.charts.basic.util;

import java.text.NumberFormat;
import java.util.Locale;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.util.LogFormat;

/**
 *
 * @author sjaenick
 */
public class LogAxis {

    public static TickUnitSource createLogTickUnits(Locale locale) {
        TickUnits units = new TickUnits();
        NumberFormat numberFormat = new LogFormat();
        units.add(new NumberTickUnit(0.05, numberFormat, 2));
        units.add(new NumberTickUnit(0.1, numberFormat, 10));
        units.add(new NumberTickUnit(0.2, numberFormat, 2));
        units.add(new NumberTickUnit(0.5, numberFormat, 5));
        units.add(new NumberTickUnit(1, numberFormat, 10));
        units.add(new NumberTickUnit(2, numberFormat, 10));
        units.add(new NumberTickUnit(3, numberFormat, 15));
        units.add(new NumberTickUnit(4, numberFormat, 20));
        units.add(new NumberTickUnit(5, numberFormat, 25));
        units.add(new NumberTickUnit(6, numberFormat));
        units.add(new NumberTickUnit(7, numberFormat));
        units.add(new NumberTickUnit(8, numberFormat));
        units.add(new NumberTickUnit(9, numberFormat));
        units.add(new NumberTickUnit(10, numberFormat));
        return units;
    }
}
