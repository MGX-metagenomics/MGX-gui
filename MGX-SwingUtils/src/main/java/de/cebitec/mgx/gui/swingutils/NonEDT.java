package de.cebitec.mgx.gui.swingutils;

import de.cebitec.mgx.gui.pool.MGXPool;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class NonEDT {

    public static void invoke(final Runnable run) {
        MGXPool.getInstance().submit(run);
    }

    public static void invokeAndWait(final Runnable run) {
        Future<?> result = MGXPool.getInstance().submit(run);
        try {
            result.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
