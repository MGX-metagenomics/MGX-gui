package de.cebitec.mgx.gui.statistics.clustering;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.statistics.clustering.dendro.Dendrogram;
import de.cebitec.mgx.gui.swingutils.DelayedPlot;
import de.cebitec.mgx.gui.viewer.api.AbstractViewer;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import de.cebitec.mgx.newick.NewickParser;
import de.cebitec.mgx.newick.NodeI;
import de.cebitec.mgx.newick.ParserException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class HClustPlot2 extends AbstractViewer<DistributionI<Long>> implements CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {

    private DelayedPlot cPanel = null;
    private final HClustCustomizer customizer = new HClustCustomizer();
    private Dendrogram display;
    private String newickString = null;
    private List<Pair<GroupI, DistributionI<Double>>> data;

    @Override
    public JComponent getComponent() {
        return cPanel;
    }

    @Override
    public String getName() {
        return "Clustering";
    }

    @Override
    public void show(final List<Pair<GroupI, DistributionI<Long>>> dists) {

        cPanel = new DelayedPlot();
        data = new LongToDouble().filter(dists);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                MGXMasterI m = dists.get(0).getSecond().getMaster();
                return m.Statistics().Clustering(data, customizer.getDistanceMethod(), customizer.getAgglomeration());
            }

            @Override
            protected void done() {
                try {
                    DelayedPlot wp = HClustPlot2.this.cPanel;
                    newickString = get();
                    NodeI root = NewickParser.parse(newickString);
                    display = new Dendrogram();
                    display.showTree(root, 20);
                    wp.setTarget(display, getImageExporter());
                    wp.repaint();
                    customizer.setNewickString(newickString);
                } catch (InterruptedException | ExecutionException | ParserException ex) {
                    HClustPlot2.this.cPanel.setTarget(null, null);
                    String message = ex.getMessage();
                    if (message.contains(":")) {
                        message = message.substring(message.lastIndexOf(":") + 1);
                    }
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Clustering failed: " + message, NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                }
                super.done();
            }
        };
        worker.execute();
    }

    @Override
    public JComponent getCustomizer() {
        return customizer;
    }

    @Override
    public Class getInputType() {
        return DistributionI.class;
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
                switch (type) {
                    case PNG:
                        BufferedImage bi = new BufferedImage(display.getSize().width, display.getSize().height, BufferedImage.TYPE_INT_ARGB);
                        Graphics g = bi.createGraphics();
                        display.print(g);
                        g.dispose();
                        try {
                            ImageIO.write(bi, "png", new File(fName));
                        } catch (IOException e) {
                            return Result.ERROR;
                        }
                        return Result.SUCCESS;

                    case JPEG:
                        BufferedImage bi2 = new BufferedImage(display.getSize().width, display.getSize().height, BufferedImage.TYPE_INT_ARGB);
                        Graphics g2 = bi2.createGraphics();
                        display.print(g2);
                        g2.dispose();
                        try {
                            ImageIO.write(bi2, "jpg", new File(fName));
                        } catch (IOException e) {
                            return Result.ERROR;
                        }
                        return Result.SUCCESS;
                    case SVG:
                        SVGGraphics2D svg = new SVGGraphics2D(display.getWidth(), display.getHeight());
                        display.print(svg);
                        String svgElement = svg.getSVGElement();
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fName))) {
                            bw.write(svgElement);
                        }
                        return Result.SUCCESS;
                    default:
                        return Result.ABORT;
                }

            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(data.size());
        Set<String> seenGeneNames = new HashSet<>();
        for (Pair<GroupI, DistributionI<Double>> p : data) {
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
    public boolean canHandle(AttributeTypeI valueType) {
        try {
            return valueType.getValueType() == AttributeTypeI.VALUE_DISCRETE
                    && VGroupManager.getInstance().getActiveGroups().size() > 1
                    && VGroupManager.getInstance().getDistributions().size() > 1;
        } catch (ConflictingJobsException ex) {
            return false;
        }
    }

    @Override
    public void setAttributeType(AttributeTypeI aType) {
        super.setAttributeType(aType);
        super.setTitle("Clustering based on " + aType.getName());
    }
}
