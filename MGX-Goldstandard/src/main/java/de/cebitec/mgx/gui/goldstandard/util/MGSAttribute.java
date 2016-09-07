/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.model.AttributeI;

/**
 *
 * @author pblumenk
 */
public class MGSAttribute {

    private final AttributeI attribute;
    private final int start;
    private final int stop;

    public MGSAttribute(AttributeI attribute, int start, int stop) {
        this.attribute = attribute;
        this.start = start;
        this.stop = stop;
    }

    public AttributeI getAttribute() {
        return attribute;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

}
