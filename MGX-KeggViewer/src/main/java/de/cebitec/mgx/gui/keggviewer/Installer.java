package de.cebitec.mgx.gui.keggviewer;

import de.cebitec.mgx.gui.util.Reference;
import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import java.awt.Rectangle;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.modules.ModuleInstall;
import org.openide.modules.Places;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

public class Installer extends ModuleInstall {

    private final static RequestProcessor RP = new RequestProcessor("KEGG-Installer", 35, true);
    public static boolean keggLoaded = false;

    @Override
    public void restored() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String cacheDir = Places.getUserDirectory().getAbsolutePath() + File.separator + "kegg" + File.separator;
                try {
                    final KEGGMaster km = KEGGMaster.getInstance(cacheDir);
                    Set<PathwayI> fetchall = km.Pathways().fetchall();
                    final CountDownLatch latch = new CountDownLatch(fetchall.size() * 2);
                    final Reference<Boolean> success = new Reference<>(Boolean.TRUE);

                    final Set<ECNumberI> ecNumbers = Collections.synchronizedSet(new HashSet<ECNumberI>());
                    for (final PathwayI p : fetchall) {
                        Runnable r1 = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    km.Pathways().fetchImageFromServer(p);
                                } catch (KEGGException ex) {
                                    Exceptions.printStackTrace(ex);
                                    success.setValue(false);
                                } finally {
                                    latch.countDown();
                                }
                            }
                        };
                        RP.post(r1);

                        Runnable r2 = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Map<ECNumberI, Set<Rectangle>> coords = km.Pathways().getCoords(p);
                                    ecNumbers.addAll(coords.keySet());
                                } catch (KEGGException ex) {
                                    Exceptions.printStackTrace(ex);
                                    success.setValue(false);
                                } finally {
                                    latch.countDown();
                                }
                            }
                        };
                        RP.post(r2);
                    }
                    try {
                        latch.await();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    final CountDownLatch latch2 = new CountDownLatch(ecNumbers.size());
                    for (final ECNumberI ec : ecNumbers) {
                        Runnable r3 = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    km.Pathways().getMatchingPathways(ec);
                                } catch (KEGGException ex) {
                                    Exceptions.printStackTrace(ex);
                                    success.setValue(false);
                                } finally {
                                    latch2.countDown();
                                }
                            }
                        };
                        RP.post(r3);
                    }
                    try {
                        latch2.await();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    keggLoaded = success.getValue();
                } catch (KEGGException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };

        final RequestProcessor.Task theTask = RP.create(runnable);
        final ProgressHandle ph = ProgressHandleFactory.createHandle("Fetching/Validating KEGG data", theTask);

        theTask.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
            }
        });

        ph.start();
        theTask.schedule(0);
    }
}
