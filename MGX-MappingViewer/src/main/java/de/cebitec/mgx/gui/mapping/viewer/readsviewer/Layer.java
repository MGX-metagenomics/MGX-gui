/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.viewer.readsviewer;

import de.cebitec.mgx.gui.mapping.sequences.JMappedSequence;
import de.cebitec.mgx.gui.mapping.sequences.MappedSequenceHolder;
import java.util.ArrayList;

/**
 *
 * @author belmann
 */
public class Layer {
    private int horizontalEnd = 0;
    private int verticalPosition = 0;
    private ArrayList<MappedSequenceHolder> sequences;
    private ArrayList<JMappedSequence> jComponents;

    public Layer(int verticalPosition) {
        this.verticalPosition = this.verticalPosition + verticalPosition;
        this.sequences = new ArrayList<>();
        this.jComponents = new ArrayList<>();
    }

    public ArrayList<JMappedSequence> getJComponents() {
        return jComponents;
    }

    public void setJComponents(ArrayList<JMappedSequence> jComponents) {
        this.jComponents = jComponents;
    }

    public void addJComponent(JMappedSequence jSequence) {
        this.jComponents.add(jSequence);
    }

    public ArrayList<MappedSequenceHolder> getSequences() {
        return sequences;
    }

    public void addSequence(MappedSequenceHolder sequence) {
        this.sequences.add(sequence);
    }

    public void setSequences(ArrayList<MappedSequenceHolder> sequences) {
        this.sequences = sequences;
    }

    public int getHorizontalEnd() {
        return horizontalEnd;
    }

    public void setHorizontalEnd(int end) {
        this.horizontalEnd = end;
    }

    public int getVerticalPosition() {
        return verticalPosition;
    }

    public void setVerticalPosition(int position) {
        this.verticalPosition = position;
    }
    
}
