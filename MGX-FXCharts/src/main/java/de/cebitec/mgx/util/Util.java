/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.util;

/**
 *
 * Util Klasse
 *
 * @author belmann
 */
public class Util {


   /**
    *
    * Konvertiert Color to Hex String
    *
    * @param c Color
    * @return String Repräsentation des Hex Werts.
    */
   public static String convertColorToHexString(java.awt.Color c) {
	String str = Integer.toHexString(c.getRGB() & 0xFFFFFF);
	return ("#" + "000000".substring(str.length()) + str.toUpperCase());
   }
}
