/* 
 * Copyright 2014 Nils Hoffmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cebitec.mgx.gui.devel;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.geom.AffineTransform.getTranslateInstance;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import static java.lang.System.getProperty;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "Window",
        id = "net.nilshoffmann.svg.actions.SvgScreenshot"
)
@ActionRegistration(
        displayName = "#CTL_SvgScreenshot"
)
@ActionReferences({
    //only register this action with a keyboard shortcut
    @ActionReference(path = "Shortcuts", name = "D-F10")
})
@Messages("CTL_SvgScreenshot=Svg Screenshot")
public final class SvgScreenshot implements ActionListener {

    public static final String PREF_PAINTMODE = "SvgScreenshot.paintMode";
    public static final String PREF_OUTPUTDIRECTORY = "SvgScreenshot.outputDirectory";
    public static final String DEFAULT_OUTPUTDIRECTORY = "NetBeansScreenshots";

    public static enum PaintMode {

        PAINT, PRINT
    };

    @Override
    public void actionPerformed(ActionEvent e) {
        final PaintMode paintMode = PaintMode.valueOf(
                NbPreferences.forModule(SvgScreenshot.class).get(
                        PREF_PAINTMODE, PaintMode.PAINT.name()
                )
        );
        final File outputDir = new File(
                NbPreferences.forModule(SvgScreenshot.class).get(
                        PREF_OUTPUTDIRECTORY,
                        new File(getProperty("user.home"), DEFAULT_OUTPUTDIRECTORY).getAbsolutePath()
                )
        );

        SwingWorker<Window, Void> w = new SwingWorker<Window, Void>() {
            @Override
            protected Window doInBackground() throws Exception {
                Thread.sleep(3000);

                //retrieve the active window component
                KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                Window window = kfm.getActiveWindow();

                SVGGraphics2D g2d = new SVGGraphics2D(window.getWidth(), window.getHeight());

                g2d.setTransform(getTranslateInstance(0, 0));
                //make sure everything is up to date
                window.invalidate();
                window.revalidate();
                switch (paintMode) {
                    case PRINT:
                        window.print(g2d);
                        break;
                    default:
                        window.paint(g2d);
                }
                RequestProcessor.getDefault().post(new FileWriter(outputDir, g2d));
                return window;
            }
        };
        w.execute();

    }

    private static int i = 1;

    private class FileWriter implements Runnable {

        private final File outputDirectory;
        private final SVGGraphics2D g2d;

        FileWriter(File outputDirectory, SVGGraphics2D g2d) {
            this.outputDirectory = outputDirectory;
            this.g2d = g2d;
        }

        @Override
        public void run() {
            ProgressHandle ph = ProgressHandle.createHandle("Saving SVG screenshot");
            try {
                ph.start();
                ph.progress("Writing SVG screenshot file...");

                File f = saveGraphics(g2d, outputDirectory, "shot_");
                i++;
                ph.progress("Done!");
                StatusDisplayer.getDefault().setStatusText("Wrote SVG screenshot to " + f.getAbsolutePath());
            } finally {
                ph.finish();
            }
        }

        private File saveGraphics(SVGGraphics2D g2d, File outputDirectory, String fileNamePrefix) {
            outputDirectory.mkdirs();

            File file = new File(outputDirectory, fileNamePrefix + i + ".svg");
            while (file.exists()) {
                i++;
                file = new File(outputDirectory, fileNamePrefix + i + ".svg");
            }
            Logger.getLogger(SvgScreenshot.class.getName()).log(Level.INFO, "Saving screenshot to {0}", file);
            try (Writer out = new OutputStreamWriter(new FileOutputStream(file))) {
                out.write(g2d.getSVGElement());
                g2d.dispose();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            return file;
        }

    }
}
