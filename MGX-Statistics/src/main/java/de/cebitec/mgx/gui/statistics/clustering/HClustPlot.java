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
import de.cebitec.mgx.gui.swingutils.DelayedPlot;
import de.cebitec.mgx.gui.viewer.api.AbstractViewer;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.svg.SVGDocument;

/**
 *
 * @author sjaenick
 */
@ServiceProvider(service = ViewerI.class)
public class HClustPlot extends AbstractViewer<DistributionI<Long>> implements CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {

    private DelayedPlot cPanel = null;
    private final HClustCustomizer customizer = new HClustCustomizer();
    private String newickString = null;
    private String svgString = null;
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

        SwingWorker<Pair<String, String>, Void> worker = new SwingWorker<Pair<String, String>, Void>() {
            @Override
            protected Pair<String, String> doInBackground() throws Exception {
                MGXMasterI m = dists.get(0).getSecond().getMaster();
                String newick = m.Statistics().Clustering(data, customizer.getDistanceMethod(), customizer.getAgglomeration());
                String svg = m.Statistics().newickToSVG(newick);
                return new Pair<>(newick, svg);
            }

            @Override
            protected void done() {
                try {
                    DelayedPlot wp = HClustPlot.this.cPanel;
                    Pair<String, String> result = get();
                    newickString = result.getFirst();
                    svgString = result.getSecond();
                    JSVGCanvas jsvg = new JSVGCanvas();
                    String parser = XMLResourceDescriptor.getXMLParserClassName();
                    SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
                    SVGDocument document = factory.createSVGDocument("", new ByteArrayInputStream(svgString.getBytes("UTF-8")));
                    jsvg.setSVGDocument(document);
                    wp.getParent().add(jsvg);
                    wp.setTarget(null, getImageExporter());
                    wp.repaint();

                    customizer.setNewickString(newickString);
                    
                } catch (InterruptedException | ExecutionException ex) {
                    HClustPlot.this.cPanel.setTarget(null, null);
                    String message = ex.getMessage();
                    if (message.contains(":")) {
                        message = message.substring(message.lastIndexOf(":") + 1);
                    }
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Clustering failed: " + message, NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                } catch (UnsupportedEncodingException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
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
    public Class<?> getInputType() {
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
                        TranscoderInput ti = new TranscoderInput(new ByteArrayInputStream(svgString.getBytes("UTF-8")));
                        try (OutputStream PNGOutStream = new FileOutputStream(fName)) {
                            TranscoderOutput outPNG = new TranscoderOutput(PNGOutStream);
                            PNGTranscoder pngConv = new PNGTranscoder();
                            pngConv.transcode(ti, outPNG);
                            PNGOutStream.flush();
                        } catch (IOException e) {
                            return Result.ERROR;
                        }
                        return Result.SUCCESS;

                    case JPEG:
                        TranscoderInput input_svg = new TranscoderInput(new ByteArrayInputStream(svgString.getBytes("UTF-8")));
                        try (OutputStream JPEGOutStream = new FileOutputStream(fName)) {
                            TranscoderOutput outJPEG = new TranscoderOutput(JPEGOutStream);
                            JPEGTranscoder jpegConv = new JPEGTranscoder();
                            jpegConv.transcode(input_svg, outJPEG);
                            JPEGOutStream.flush();
                        } catch (IOException e) {
                            return Result.ERROR;
                        }
                        return Result.SUCCESS;
                    case SVG:
                        try (FileWriter fw = new FileWriter(fName)) {
                            fw.write(svgString);
                        } catch (IOException e) {
                            return Result.ERROR;
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
