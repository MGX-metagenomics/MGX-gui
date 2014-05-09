package de.cebitec.mgx.gui.mapping.sequences;

/**
 *
 * @author belmann
 */
public abstract class ISequenceHolder implements Comparable<ISequenceHolder> {

    private int start;
    private int stop;

    public ISequenceHolder(int start, int stop) {
        this.start = start;
        this.stop = stop;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }
    
    @Override
    public int compareTo(ISequenceHolder feature) {
        if (getStart() < feature.getStart()) {
            return -1;
        } else if (getStart() > feature.getStart()) {
            return 1;
        }
        return 0;
    }
}
