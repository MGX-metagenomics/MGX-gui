package de.cebitec.mgx.gui.util;

import java.util.Iterator;

/**
 *
 * @author sj
 */
public abstract class BaseIterator<T, U> implements Iterator<U> {

    protected final Iterator<T> iter;

    public BaseIterator(Iterator<T> iter) {
        this.iter = iter;
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }
}
