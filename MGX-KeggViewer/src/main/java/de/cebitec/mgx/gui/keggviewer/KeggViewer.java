package de.cebitec.mgx.gui.keggviewer;

import de.cebitec.mgx.gui.attributevisualization.viewer.CategoricalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.ImageExporterI;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.util.FileChooserUtils;
import de.cebitec.mgx.gui.util.FileType;
import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.model.ECNumberFactory;
import de.cebitec.mgx.kegg.pathways.paint.KEGGPanel;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.Places;
import org.openide.util.Exceptions;
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
            master = new KEGGMaster(cacheDir);
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
            public void export() {
                String fname = FileChooserUtils.selectNewFilename(new FileType[]{FileType.PNG});
                if (fname == null) {
                    return;
                }

                try {
                    panel.save(new File(fname));
                } catch (IOException ex) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Error: " + ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                    return;
                }
                NotifyDescriptor nd = new NotifyDescriptor.Message("Chart saved to " + fname, NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
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
    private Pattern ecNumber = Pattern.compile("\\d+[.](-|\\d+)[.](-|\\d+)[.](-|\\d+)");

    @Override
    public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
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
                        panel.addData(idx, ec, group.getColor(), e.getValue().intValue());
                    }
                }
                idx++;
            }
        } catch (KEGGException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    @Override
    public JComponent getCustomizer() {
        return customizer;
    }

    @Override
    public boolean canHandle(AttributeType valueType) {
        return Installer.keggLoaded && super.canHandle(valueType) && valueType.getName().equals("EC_Number");
    }
}
