package de.cebitec.mgx.gui.keggviewer;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import java.io.File;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.modules.ModuleInstall;
import org.openide.modules.Places;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

public class Installer extends ModuleInstall {

    private final static RequestProcessor RP = new RequestProcessor("KEGG-Installer", 10, true);
    public static boolean keggLoaded = false;

    @Override
    public void restored() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String cacheDir = Places.getUserDirectory().getAbsolutePath() + File.separator + "kegg" + File.separator;
                try {
                    final KEGGMaster km = new KEGGMaster(cacheDir);
                    Set<PathwayI> fetchall = km.Pathways().fetchall();
                    final CountDownLatch latch = new CountDownLatch(fetchall.size());
                    for (final PathwayI p : fetchall) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    km.Pathways().getImage(p);
                                    km.Pathways().getCoords(p);
                                } catch (KEGGException ex) {
                                    Exceptions.printStackTrace(ex);
                                } finally {
                                    latch.countDown();
                                }
                            }
                        };
                        RP.post(r);
                    }
                    latch.await();
                    keggLoaded = true;
                } catch (KEGGException | InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };

        final RequestProcessor.Task theTask = RP.create(runnable);
        final ProgressHandle ph = ProgressHandleFactory.createHandle("Fetching KEGG data", theTask);

        theTask.addTaskListener(
                new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
            }
        });

        ph.start();
        theTask.schedule(0);
    }
}
