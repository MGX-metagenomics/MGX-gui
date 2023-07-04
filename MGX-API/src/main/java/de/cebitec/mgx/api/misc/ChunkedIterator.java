/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.cebitec.mgx.api.misc;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
// e.g. contigI, contigdtolist, contigdto
public abstract class ChunkedIterator<T, U, V> implements Iterator<T> {

    private final MGXMasterI master;
    private U dtoList;
    private Iterator<V> iter;
    
    public ChunkedIterator(MGXMasterI master, U dtoList) {
        this.master = master;
        this.dtoList = dtoList;
        this.iter = chunkIterator();
    }

    @Override
    public final boolean hasNext() {

        // an exception has occurred previously
        if (iter == null) {
            return false;
        }

        if (iter.hasNext()) {
            // current iterator still has data
            return true;
        } else {
            // end of current data chunk
            if (isLastChunk(dtoList)) {
                return false;
            } else {
                // attempt to fetch next chunk of data
                try {
                    dtoList = nextChunk();
                    iter = chunkIterator();
                    return iter.hasNext();
                } catch (MGXException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    dtoList = null;
                    iter = null;
                    throw new RuntimeException(ex);
                }

            }
        }
    }

    public final U currentChunk() {
        return dtoList;
    }

    public abstract U nextChunk() throws MGXException;

    public abstract Iterator<V> chunkIterator();

    public abstract T convert(V v);

    public abstract boolean isLastChunk(U u);

    protected final MGXMasterI getMaster() {
        return master;
    }

    @Override
    public final T next() {
        V v = iter.next();
        return convert(v);
    }

    @Override
    public final void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }
}
