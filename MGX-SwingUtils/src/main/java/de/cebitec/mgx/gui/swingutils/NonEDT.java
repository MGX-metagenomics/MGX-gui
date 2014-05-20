package de.cebitec.mgx.gui.swingutils;

import java.awt.EventQueue;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class NonEDT {

    public static void invoke(final Runnable run) {
        if (EventQueue.isDispatchThread()) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    run.run();
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    super.done();
                }
            };
            worker.execute();
        } else {
            run.run();
        }
    }

    public static void invokeAndWait(final Runnable run) {
        if (EventQueue.isDispatchThread()) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    run.run();
                    return null;
                }
            };
            worker.execute();
            try {
                worker.get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            run.run();
        }
    }
}
