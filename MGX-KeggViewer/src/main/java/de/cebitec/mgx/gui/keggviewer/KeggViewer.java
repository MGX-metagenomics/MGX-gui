package de.cebitec.mgx.gui.keggviewer;

import de.cebitec.mgx.gui.attributevisualization.viewer.CategoricalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.ConflictingJobsException;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.util.FileType;
import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import de.cebitec.mgx.kegg.pathways.model.ECNumberFactory;
import de.cebitec.mgx.kegg.pathways.paint.KEGGPanel;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.openide.modules.Places;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class KeggViewer extends CategoricalViewerI {

    private KEGGPanel panel;
    private KEGGMaster master;
    private KeggCustomizer customizer;

    public KeggViewer() {
        String cacheDir = Places.getUserDirectory().getAbsolutePath() + File.separator + "kegg" + File.separator;
        try {
            master = KEGGMaster.getInstance(cacheDir);
            panel = new KEGGPanel(master);
            customizer = new KeggCustomizer(master);
        } catch (KEGGException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return new ImageExporterI() {

            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG};
            }

            @Override
            public boolean export(FileType type, String fName) throws Exception {
                try {
                    panel.save(new File(fName));
                    return true;
                } catch (IOException ex) {
                    return false;
                }
            }
        };
    }

    @Override
    public String getName() {
        return "KEGG Pathways";
    }

    @Override
    public Class getInputType() {
        return Distribution.class;
    }
    private final Pattern ecNumber = Pattern.compile("\\d+[.](-|\\d+)[.](-|\\d+)[.](-|\\d+)");

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
        if (customizer.getSelectedPathway() == null) {
            return;
        }
        try {
            panel.setPathway(customizer.getSelectedPathway(), dists.size());

            int idx = 0;
            for (Pair<VisualizationGroup, Distribution> p : dists) {
                VisualizationGroup group = p.getFirst();
                Distribution dist = p.getSecond();
                for (Entry<Attribute, Number> e : dist.entrySet()) {
                    Matcher matcher = ecNumber.matcher(e.getKey().getValue());
                    if (matcher.find()) {
                        ECNumberI ec = ECNumberFactory.fromString(e.getKey().getValue().substring(matcher.start(), matcher.end()));
                        String description = "<html><b>" + e.getKey().getValue() + "</b><br><hr>"
                                + group.getName() + ": " + e.getValue().toString() + " hits</html>";
                        panel.addData(idx, ec, group.getColor(), description);
                    }
                }
                idx++;
            }
        } catch (KEGGException ex) {
            Exceptions.printStackTrace(ex);
        }

    }
    private final static RequestProcessor RP = new RequestProcessor("KEGG-Viewer", 35, true);

    private Set<PathwayI> selectPathways() throws ConflictingJobsException, KEGGException {
        final Set<ECNumberI> ecNumbers = new HashSet<>();
        for (Pair<VisualizationGroup, Distribution> p : VGroupManager.getInstance().getDistributions()) {
            Distribution dist = p.getSecond();
            for (Entry<Attribute, Number> e : dist.entrySet()) {
                Matcher matcher = ecNumber.matcher(e.getKey().getValue());
                if (matcher.find()) {
                    try {
                        ECNumberI ec = ECNumberFactory.fromString(e.getKey().getValue().substring(matcher.start(), matcher.end()));
                        ecNumbers.add(ec);
                    } catch (KEGGException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        final CountDownLatch latch = new CountDownLatch(1);
        final Set<PathwayI> ret = Collections.synchronizedSet(new HashSet<PathwayI>());
        RP.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ret.addAll(master.Pathways().getMatchingPathways(ecNumbers));
                } catch (KEGGException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public KeggCustomizer getCustomizer() {
        SwingWorker<Set<PathwayI>, Void> sw = new SwingWorker<Set<PathwayI>, Void>() {
            @Override
            protected Set<PathwayI> doInBackground() throws Exception {
                return selectPathways();
            }

            @Override
            protected void done() {
                try {
                    customizer.restrictPathways(get());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                super.done();
            }
        };
        sw.execute();

        return customizer;
    }

    @Override
    public boolean canHandle(AttributeType valueType) {
        return Installer.keggLoaded && super.canHandle(valueType) && valueType.getName().equals("EC_number");
    }

    @Override
    public void dispose() {
        RP.shutdownNow();
        super.dispose();
    }
}
