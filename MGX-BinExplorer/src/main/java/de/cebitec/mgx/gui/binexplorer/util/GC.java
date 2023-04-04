/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.binexplorer.util;

/**
 *
 * @author sj
 */
public class GC {

    private GC() {
    }

    public static float gc(String sequence) {
        int gc = 0;
        for (byte b : sequence.getBytes()) {
            switch (b) {
                case 'G':
                case 'C':
                    gc++;
            }
        }
        return gc * 100f / sequence.length();
    }

    // GC skew = (G − C)/(G + C) 
    public static float gcSkew(String sequence) {
        float g = 0;
        float c = 0;
        for (byte b : sequence.getBytes()) {
            switch (b) {
                case 'G':
                    g++;
                    break;
                case 'C':
                    c++;
                    break;
            }
        }

        if (g + c == 0) {
            return 0;
        }
        return (g - c) / (g + c);
    }
}
