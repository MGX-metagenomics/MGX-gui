
package de.cebitec.mgx.api.misc;

import java.util.concurrent.Callable;

/**
 *
 * @author sjaenick
 */
public abstract class Fetcher<T> implements Callable<T> {
    
    protected abstract T doInBackground() throws Exception;
    
    @Override
    public T call() throws Exception {
        return doInBackground();
    }
    
}
