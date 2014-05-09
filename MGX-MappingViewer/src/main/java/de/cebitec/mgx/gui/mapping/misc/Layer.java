/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.misc;

import de.cebitec.mgx.gui.mapping.sequences.JMappedSequence;
import de.cebitec.mgx.gui.mapping.sequences.MappedSequenceHolder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author belmann
 */
public class Layer {
    private int horizontalEnd = 0;
    private int verticalPosition = 0;
    private List<MappedSequenceHolder> sequences;
    private List<JMappedSequence> jComponents;

    public Layer(int verticalPosition) {
        this.verticalPosition = this.verticalPosition + verticalPosition;
        this.sequences = new ArrayList<>();
        this.jComponents = new ArrayList<>();
    }

    public List<JMappedSequence> getJComponents() {
        return jComponents;
    }

    public void setJComponents(List<JMappedSequence> jComponents) {
        this.jComponents = jComponents;
    }

    public void addJComponent(JMappedSequence jSequence) {
        this.jComponents.add(jSequence);
    }

    public List<MappedSequenceHolder> getSequences() {
        return sequences;
    }

    public void addSequence(MappedSequenceHolder sequence) {
        this.sequences.add(sequence);
    }

    public void setSequences(List<MappedSequenceHolder> sequences) {
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
