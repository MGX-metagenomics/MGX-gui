/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.tracks;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sj
 */
public class ReverseIterator<T> implements Iterator<T> {

    private final T[] content;
    private int idx;

    @SuppressWarnings("unchecked")
    public ReverseIterator(List<T> content) {
        this.content = (T[]) content.toArray();
        this.idx = content.size() - 1;
    }

    @Override
    public boolean hasNext() {
        return idx > 0;
    }

    @Override
    public T next() {
        T ret = content[idx];
        idx--;
        return ret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }

}
