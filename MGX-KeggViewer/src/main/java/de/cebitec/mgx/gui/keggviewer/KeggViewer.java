package de.cebitec.mgx.gui.keggviewer;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.viewer.api.CategoricalViewerI;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import de.cebitec.mgx.kegg.pathways.model.ECNumberFactory;
import de.cebitec.mgx.kegg.pathways.paint.KEGGPanel;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.Places;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class KeggViewer extends CategoricalViewerI<Long> implements CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {

    private KEGGPanel panel;
    private KEGGMaster master;
    private KeggCustomizer customizer;
    private List<Pair<GroupI, DistributionI<Long>>> data;
    private VGroupManagerI vmgr = null;
    private boolean initOk = true;
    //
    private static boolean messageShown = false;

    public KeggViewer() {
        File userDir = Places.getUserDirectory() != null ? Places.getUserDirectory() : new File(System.getProperty("java.io.tmpdir"));
        String cacheDir = userDir.getAbsolutePath() + File.separator + "kegg" + File.separator;
        try {
            master = KEGGMaster.getInstance(cacheDir);
            panel = new KEGGPanel(master);
            customizer = new KeggCustomizer();
        } catch (KEGGException ex) {
            Exceptions.printStackTrace(ex);
            initOk = false;
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
                return new FileType[]{FileType.PNG, FileType.JPEG, FileType.SVG};
            }

            @Override
            public Result export(FileType type, String fName) throws Exception {
                if (!messageShown) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Publication of a KEGG pathway map requires "
                            + "permission from KEGG, which can be requested at https://www.kegg.jp/feedback/copyright.html.");
                    DialogDisplayer.getDefault().notify(nd);
                    messageShown = true;
                }

                switch (type) {
                    case PNG:
                        panel.savePNG(new File(fName));
                        return Result.SUCCESS;
                    case JPEG:
                        panel.saveJPEG(new File(fName));
                        return Result.SUCCESS;
                    case SVG:
                        panel.saveSVG(new File(fName));
                        return Result.SUCCESS;
                }
                return Result.ERROR;
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(data.size());
        Set<String> seenGeneNames = new HashSet<>();
        for (Pair<GroupI, DistributionI<Long>> p : data) {
            if (p.getSecond().getTotalClassifiedElements() > 0) {
                if (p.getFirst().getContentClass().equals(SeqRunI.class)) {
                    SequenceExporterI exp = new SeqExporter<>((GroupI<SeqRunI>) p.getFirst(), p.getSecond());
                    ret.add(exp);
                } else if (p.getFirst().getContentClass().equals(AssembledSeqRunI.class)) {
                    SequenceExporterI exp = new SeqExporter<>((GroupI<AssembledSeqRunI>) p.getFirst(), p.getSecond(), seenGeneNames);
                    ret.add(exp);
                }
            }
        }
        return ret.toArray(new SequenceExporterI[]{});
    }

    @Override
    public String getName() {
        return "KEGG Pathways";
    }

    @Override
    public Class<?> getInputType() {
        return DistributionI.class;
    }

    private final static Pattern ecNumber = Pattern.compile("\\d+[.](-|\\d+)[.](-|\\d+)[.](-|\\d+)");

    @Override
    public void show(List<Pair<GroupI, DistributionI<Long>>> in) {

        data = in;

        if (customizer.getSelectedPathway() == null) {
            return;
        }
        try {
            panel.setPathway(customizer.getSelectedPathway(), data.size());

            int idx = 0;
            for (Pair<GroupI, DistributionI<Long>> p : data) {
                GroupI group = p.getFirst();
                DistributionI<Long> dist = p.getSecond();
                for (Entry<AttributeI, Long> e : dist.entrySet()) {
                    Matcher matcher = ecNumber.matcher(e.getKey().getValue());
                    if (matcher.find()) {
                        ECNumberI ec = ECNumberFactory.fromString(e.getKey().getValue().substring(matcher.start(), matcher.end()));
                        String description = "<html><b>" + e.getKey().getValue() + "</b><br><hr>"
                                + group.getDisplayName() + ": " + NumberFormat.getInstance(Locale.US).format(e.getValue()) + " hits</html>";
                        panel.addData(idx, ec, group.getColor(), description);
                    }
                }
                idx++;
            }
        } catch (KEGGException ex) {
            Exceptions.printStackTrace(ex);
        }

    }
    private final static RequestProcessor reqProcessor = new RequestProcessor("KEGG-Viewer", 35, true);

    public Set<PathwayI> selectPathways() throws ConflictingJobsException, KEGGException {

        if (vmgr == null) {
            vmgr = VGroupManager.getInstance();
        }

        return customizer.selectPathways(master, vmgr, reqProcessor);
    }
//        final Set<ECNumberI> ecNumbers = new HashSet<>();
//        for (Pair<VisualizationGroupI, DistributionI> p : VGroupManager.getInstance().getDistributions()) {
//            DistributionI dist = p.getSecond();
//            for (Entry<AttributeI, Number> e : dist.entrySet()) {
//                Matcher matcher = ecNumber.matcher(e.getKey().getValue());
//                if (matcher.find()) {
//                    try {
//                        ECNumberI ec = ECNumberFactory.fromString(e.getKey().getValue().substring(matcher.start(), matcher.end()));
//                        ecNumbers.add(ec);
//                    } catch (KEGGException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
//                }
//            }
//        }
//        final Set<PathwayI> ret = Collections.synchronizedSet(new HashSet<PathwayI>());
//        RequestProcessor.Task task = RP.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    ret.addAll(master.Pathways().getMatchingPathways(ecNumbers));
//                } catch (KEGGException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }
//        });
//        task.waitFinished();
//        return ret;
//    }

    @Override
    public KeggCustomizer getCustomizer() {

        if (vmgr == null) {
            vmgr = VGroupManager.getInstance();
        }

        SwingWorker<Set<PathwayI>, Void> sw = new SwingWorker<Set<PathwayI>, Void>() {
            @Override
            protected Set<PathwayI> doInBackground() throws Exception {
                return customizer.selectPathways(master, vmgr, reqProcessor);
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
    public boolean canHandle(AttributeTypeI valueType) {
        return initOk && super.canHandle(valueType) && valueType.getName().equals("EC_number");
    }

    @Override
    public void dispose() {
        reqProcessor.shutdownNow();
        super.dispose();
    }

    // required to implant VGroupManager test instance during unit tests
    void setVGroupManager(VGroupManagerI mgr) {
        this.vmgr = mgr;
    }
}
