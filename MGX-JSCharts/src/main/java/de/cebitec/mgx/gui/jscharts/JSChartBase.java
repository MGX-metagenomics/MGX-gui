/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.jscharts;

import de.cebitec.mgx.api.misc.Visualizable;
import de.cebitec.mgx.common.visualization.AbstractViewer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JComponent;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public abstract class JSChartBase<T extends Visualizable> extends AbstractViewer<T> {

    private final JSPanel panel = new JSPanel();

    @Override
    public final JComponent getComponent() {
        return panel;
    }

    protected void setHTML(String html) {
        panel.display(html);
    }

    protected String loadResourceFile(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is))) {
                return buffer.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    final class JSPanel extends JFXPanel {

        private WebView webView;
        private WebEngine webEngine;

        public JSPanel() {
            Platform.runLater(() -> {
                initialiseJavaFXScene();
            });
        }

        public void display(String html) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try (PrintWriter out = new PrintWriter("/tmp/foo.html")) {
                        out.println(html);
                    } catch (FileNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    webEngine.loadContent(html);
                }
            });

        }

        private void initialiseJavaFXScene() {
            webView = new WebView();
            webEngine = webView.getEngine();
            webEngine.setJavaScriptEnabled(true);
            Scene scene = new Scene(webView);
            setScene(scene);
        }
    }

}
