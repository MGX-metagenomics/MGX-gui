package de.cebitec.mgx.gui.goldstandard.util;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author patrick
 */
public class EvalExceptions {

    public static void printStackTrace(final Throwable t){
        try {
            SwingUtilities.invokeAndWait(new Runnable(){
                @Override
                public void run() {
                    Exceptions.printStackTrace(t);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
