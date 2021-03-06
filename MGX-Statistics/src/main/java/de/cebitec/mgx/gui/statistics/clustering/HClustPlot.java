//package de.cebitec.mgx.gui.statistics.clustering;
//
//import de.cebitec.mgx.api.MGXMasterI;
//import de.cebitec.mgx.api.groups.ConflictingJobsException;
//import de.cebitec.mgx.api.groups.FileType;
//import de.cebitec.mgx.api.groups.ImageExporterI;
//import de.cebitec.mgx.api.groups.SequenceExporterI;
//import de.cebitec.mgx.api.groups.VisualizationGroupI;
//import de.cebitec.mgx.api.misc.DistributionI;
//import de.cebitec.mgx.api.misc.Pair;
//import de.cebitec.mgx.api.model.AttributeTypeI;
//import de.cebitec.mgx.gui.seqexporter.SeqExporter;
//import de.cebitec.mgx.gui.statistics.clustering.model.DendrogramBuilder;
//import de.cebitec.mgx.gui.statistics.clustering.model.ITreeBuilder;
//import de.cebitec.mgx.gui.statistics.clustering.view.DendrogramDisplay;
//import de.cebitec.mgx.gui.swingutils.DelayedPlot;
//import de.cebitec.mgx.gui.viewer.api.AbstractViewer;
//import de.cebitec.mgx.gui.viewer.api.CustomizableI;
//import de.cebitec.mgx.gui.viewer.api.ViewerI;
//import de.cebitec.mgx.gui.visgroups.VGroupManager;
//import de.cebitec.mgx.gui.vizfilter.LongToDouble;
//import de.cebitec.mgx.newick.NewickParser;
//import de.cebitec.mgx.newick.NodeI;
//import de.cebitec.mgx.newick.ParserException;
//import java.io.BufferedOutputStream;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import javax.swing.JComponent;
//import javax.swing.SwingWorker;
//import org.openide.DialogDisplayer;
//import org.openide.NotifyDescriptor;
//import org.openide.util.lookup.ServiceProvider;
//import prefuse.svg.SVGDisplaySaver;
//
///**
// *
// * @author sjaenick
// */
//@ServiceProvider(service = ViewerI.class)
//public class HClustPlot extends AbstractViewer<DistributionI<Long>> implements CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {
//
//    private final static String NODE_NAME_KEY = "nodeName";
//    private final static String X_COORD = "x";
//    private DelayedPlot cPanel = null;
//    private final HClustCustomizer customizer = new HClustCustomizer();
//    private DendrogramDisplay display;
//    private String newickString = null;
//    private List<Pair<VisualizationGroupI, DistributionI<Double>>> data;
//
//    @Override
//    public JComponent getComponent() {
//        return cPanel;
//    }
//
//    @Override
//    public String getName() {
//        return "Clustering";
//    }
//
//    @Override
//    public void show(final List<Pair<VisualizationGroupI, DistributionI<Long>>> dists) {
//
//        cPanel = new DelayedPlot();
//        data = new LongToDouble().filter(dists);
//
//        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
//            @Override
//            protected String doInBackground() throws Exception {
//                MGXMasterI m = dists.get(0).getSecond().getMaster();
//                return m.Statistics().Clustering(data, customizer.getDistanceMethod(), customizer.getAgglomeration());
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    DelayedPlot wp = HClustPlot.this.cPanel;
//                    newickString = get();
//                    NodeI root = NewickParser.parse(newickString);
//
//                    ITreeBuilder builder = new DendrogramBuilder(NODE_NAME_KEY, X_COORD, root);
//                    display = new DendrogramDisplay(builder);
//                    wp.setTarget(display, null);
//                    customizer.setNewickString(newickString);
//                } catch (InterruptedException | ExecutionException | ParserException ex) {
//                    HClustPlot.this.cPanel.setTarget(null, null);
//                    String message = ex.getMessage();
//                    if (message.contains(":")) {
//                        message = message.substring(message.lastIndexOf(":") + 1);
//                    }
//                    NotifyDescriptor nd = new NotifyDescriptor.Message("Clustering failed: " + message, NotifyDescriptor.ERROR_MESSAGE);
//                    DialogDisplayer.getDefault().notify(nd);
//                }
//                super.done();
//            }
//        };
//        worker.execute();
//    }
//
//    @Override
//    public JComponent getCustomizer() {
//        return customizer;
//    }
//
//    @Override
//    public Class getInputType() {
//        return DistributionI.class;
//    }
//
//    @Override
//    public ImageExporterI getImageExporter() {
//        return new ImageExporterI() {
//
//            @Override
//            public FileType[] getSupportedTypes() {
//                return new FileType[]{FileType.PNG, FileType.JPEG, FileType.SVG};
//            }
//
//            @Override
//            public Result export(FileType type, String fName) throws Exception {
//                switch (type) {
//                    case PNG:
//                    case JPEG:
//                        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fName))) {
//                            if (display.saveImage(os, type.getSuffices()[0].toUpperCase(), 2)) {
//                                return Result.SUCCESS;
//                            }
//                            return Result.ERROR;
//                        }
//                    case SVG:
//                        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fName))) {
//                            if (SVGDisplaySaver.saveSVG(display, os, 2)) {
//                                return Result.SUCCESS;
//                            }
//                            return Result.ERROR;
//                        }
//                    default:
//                        return Result.ABORT;
//                }
//
//            }
//        };
//    }
//
//    @Override
//    public SequenceExporterI[] getSequenceExporters() {
//        List<SequenceExporterI> ret = new ArrayList<>(data.size());
//        for (Pair<VisualizationGroupI, DistributionI<Double>> p : data) {
//            if (p.getSecond().getTotalClassifiedElements() > 0) {
//                SequenceExporterI exp = new SeqExporter<>(p.getFirst(), p.getSecond());
//                ret.add(exp);
//            }
//        }
//        return ret.toArray(new SequenceExporterI[]{});
//    }
//
//    @Override
//    public boolean canHandle(AttributeTypeI valueType) {
//        try {
//            return valueType.getValueType() == AttributeTypeI.VALUE_DISCRETE
//                    && VGroupManager.getInstance().getActiveVisualizationGroups().size() > 1
//                    && VGroupManager.getInstance().getDistributions().size() > 1;
//        } catch (ConflictingJobsException ex) {
//            return false;
//        }
//    }
//
//    @Override
//    public void setAttributeType(AttributeTypeI aType) {
//        super.setAttributeType(aType);
//        super.setTitle("Clustering based on " + aType.getName());
//    }
//}
