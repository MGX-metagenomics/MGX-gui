package de.cebitec.mgx.gui.keggviewer;

import de.cebitec.mgx.gui.pool.MGXPool;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.modules.ModuleInstall;
import org.openide.modules.Places;

public class Installer extends ModuleInstall {

    public static boolean keggLoaded = false;

    @Override
    public void restored() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final ProgressHandle ph = ProgressHandle.createHandle("Fetching/Validating KEGG data");
                ph.start();
                String cacheDir = Places.getUserDirectory().getAbsolutePath() + File.separator + "kegg" + File.separator;
                keggLoaded = copyDB(cacheDir);
                ph.finish();
            }
        };
        
        MGXPool.getInstance().execute(runnable);
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

                byte[] buffer = new byte[4096];

                int bytesRead = is.read(buffer);
                while (bytesRead >= 0) {
                    rOut.write(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }

                rOut.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Installer.class.getPackage().getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        Logger.getLogger(Installer.class.getPackage().getName()).log(Level.INFO, "KEGG database successfully installed.");
        return true;
    }
}
