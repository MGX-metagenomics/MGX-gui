package de.cebitec.mgx.gui.keggviewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.modules.ModuleInstall;
import org.openide.modules.Places;
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
                keggLoaded = copyDB(cacheDir);
//                try {
//                    final KEGGMaster km = KEGGMaster.getInstance(cacheDir);
//                    Set<PathwayI> fetchall = km.Pathways().fetchall();
//                    final CountDownLatch latch = new CountDownLatch(fetchall.size() * 2);
//                    final Reference<Boolean> success = new Reference<>(Boolean.TRUE);
//
//                    final Set<ECNumberI> ecNumbers = Collections.synchronizedSet(new HashSet<ECNumberI>());
//                    for (final PathwayI p : fetchall) {
//                        Runnable r1 = new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    km.Pathways().fetchImageFromServer(p);
//                                } catch (KEGGException ex) {
//                                    Exceptions.printStackTrace(ex);
//                                    success.setValue(false);
//                                } finally {
//                                    latch.countDown();
//                                }
//                            }
//                        };
//                        RP.post(r1);
//
//                        Runnable r2 = new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Map<ECNumberI, Set<Rectangle>> coords = km.Pathways().getCoords(p);
//                                    ecNumbers.addAll(coords.keySet());
//                                } catch (KEGGException ex) {
//                                    Exceptions.printStackTrace(ex);
//                                    success.setValue(false);
//                                } finally {
//                                    latch.countDown();
//                                }
//                            }
//                        };
//                        RP.post(r2);
//                    }
//                    try {
//                        latch.await();
//                    } catch (InterruptedException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
//
//                    final CountDownLatch latch2 = new CountDownLatch(ecNumbers.size());
//                    for (final ECNumberI ec : ecNumbers) {
//                        Runnable r3 = new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    km.Pathways().getMatchingPathways(ec);
//                                } catch (KEGGException ex) {
//                                    Exceptions.printStackTrace(ex);
//                                    success.setValue(false);
//                                } finally {
//                                    latch2.countDown();
//                                }
//                            }
//                        };
//                        RP.post(r3);
//                    }
//                    try {
//                        latch2.await();
//                    } catch (InterruptedException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
//
//                    keggLoaded = success.getValue();
//                } catch (KEGGException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
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

    private boolean copyDB(String targetDir) {
        String target = targetDir + File.separator + "kegg.mv.db";
        if (new File(target).exists()) {
            new File(target).delete();
        }
        
        if (!new File(targetDir).exists()) {
            new File(targetDir).mkdirs();
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/gui/keggviewer/kegg.mv.db")) {
            try (FileOutputStream rOut = new FileOutputStream(target)) {

                byte[] buffer = new byte[1024];

                int bytesRead = is.read(buffer);
                while (bytesRead >= 0) {
                    rOut.write(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }

                rOut.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Installer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        Logger.getLogger(Installer.class.getName()).log(Level.INFO, "KEGG database successfully installed.");
        return true;
    }
}
