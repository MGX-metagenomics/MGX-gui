/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.sequences;

import java.awt.Dimension;
import javax.swing.JComponent;

/**
 *
 * @author belmann
 */
public class JMappedSequence extends JComponent {
    public JMappedSequence(MappedSequenceHolder mappedSequence, double lLength) {
        super();
        this.setSize(new Dimension((int) lLength, HEIGHT));
    }
}
