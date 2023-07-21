package de.cebitec.mgx.gui.tableview;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.gui.swingutils.DecimalFormatRenderer;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.seqexporter.SeqExporter;
import de.cebitec.mgx.gui.viewer.api.AbstractViewer;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import de.cebitec.mgx.gui.vizfilter.LongToDouble;
import de.cebitec.mgx.gui.vizfilter.SortOrder;
import de.cebitec.mgx.gui.vizfilter.ToFractionFilter;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jfree.svg.SVGGraphics2D;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class TableViewer extends AbstractViewer<DistributionI<Long>> implements CustomizableI, ImageExporterI.Provider, SequenceExporterI.Provider {

    private JXTable table;
    private TableViewCustomizer cust = null;
    private List<Pair<GroupI, DistributionI<Double>>> dists;
    private AttributeTypeI curAttrType;

    @Override
    public JComponent getComponent() {
        return table;
    }

    @Override
    public String getName() {
        return "Table View";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        curAttrType = valueType;
        getCust().enableLineageSelection(curAttrType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL);
        return true;
    }

    @Override
    public Class<?> getInputType() {
        return DistributionI.class;
    }

    @Override
    public void show(final List<Pair<GroupI, DistributionI<Long>>> in) {

        //
        // check whether assigned reads should be displayed as counts or fractions
        //
        if (getCust().useFractions()) {
            VisFilterI<DistributionI<Long>, DistributionI<Double>> fracFilter = new ToFractionFilter();
            dists = fracFilter.filter(in);
        } else {
            dists = new LongToDouble().filter(in);
        }

        //
        // exclude filter must be applied _AFTER_ converting to fractions
        //
        dists = getCust().filter(dists);

        Set<AttributeI> allAttrs = new HashSet<>();
        int numColumns = dists.size() + 1;
        String[] columns = new String[numColumns];
        int i = 0;
        columns[i++] = getAttributeType().getName(); // first column
        for (Pair<GroupI, DistributionI<Double>> p : dists) {
            columns[i++] = p.getFirst().getDisplayName();
            allAttrs.addAll(p.getSecond().keySet());
        }

        Map<AttributeI, String> lineages = new HashMap<>();
        if (getCust().showLineage() && curAttrType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL) {

            List<Pair<GroupI, TreeI<Long>>> hierarchies;
            try {
                hierarchies = VGroupManager.getInstance().getHierarchies();
            } catch (ConflictingJobsException ex) {
                Exceptions.printStackTrace(ex);
                return;
            }

            for (AttributeI attr : allAttrs) {
                for (Pair<GroupI, TreeI<Long>> p : hierarchies) {
                    if (!lineages.containsKey(attr)) {
                        TreeI<Long> tree = p.getSecond();
                        for (NodeI<Long> node : tree.getNodes()) {
                            if (node.getAttribute().equals(attr) && !lineages.containsKey(attr)) {
                                int depth = node.getDepth();
                                NodeI[] lin = new NodeI[depth + 1];
                                lin[depth] = node;
                                while (!node.isRoot()) {
                                    node = node.getParent();
                                    lin[--depth] = node;
                                }
                                List<String> lineageString = new ArrayList<>(depth + 1);
                                for (NodeI n : lin) {
                                    lineageString.add(n.getAttribute().getValue());
                                }
                                String l = String.join("; ", lineageString.toArray(new String[]{}));
                                lineages.put(attr, l);
                                break;
                            }
                        }
                    }
                }
            }
        }

        SortOrder<Double> order = new SortOrder<>(SortOrder.DESCENDING);
        dists = order.filter(dists);

        final boolean useFractions = getCust().useFractions();

        DefaultTableModel model = new DefaultTableModel(columns, 0) {

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    default:
                        return useFractions ? Double.class : Long.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (AttributeI a : allAttrs) {
            Object[] rowData = new Object[numColumns];
            if (getCust().showLineage()) {
                rowData[0] = lineages.get(a);
            } else {
                rowData[0] = a.getValue();
            }
            int col = 1;
            for (Pair<GroupI, DistributionI<Double>> p : dists) {
                DistributionI<Double> d = p.getSecond();
                rowData[col++] = d.containsKey(a)
                        ? getCust().useFractions() ? d.get(a) : d.get(a).longValue()
                        : 0;
            }
            model.addRow(rowData);
        }

        cust.setModel(model); // for tsv export

        table = new JXTable(model) {

            //
            // # 162 -ignore setPreferredSize() so scrollbars work
            //
            @Override
            public void setPreferredSize(Dimension preferredSize) {
            }

        };
        table.setDefaultRenderer(Double.class, new DecimalFormatRenderer());
        table.setFillsViewportHeight(true);
        for (TableColumn tc : table.getColumns()) {
            if (0 != tc.getModelIndex()) {
                tc.setMinWidth(20);
                tc.setPreferredWidth(40);
                tc.setWidth(40);
            }
        }
        table.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});

        // sorter
        TableRowSorter<TableModel> sorter = new TableRowSorter<>();
        table.setRowSorter(sorter);
        sorter.setModel(model);

        String matchText = getCust().getMatchText();
        if (!matchText.isEmpty()) {
            sorter.setRowFilter(RowFilter.regexFilter(".*" + matchText + ".*", 0));
        }
    }

    @Override
    public JComponent getCustomizer() {
        return getCust();
    }

    private TableViewCustomizer getCust() {
        if (cust == null) {
            cust = new TableViewCustomizer();
        }
        cust.setAttributeType(getAttributeType());
        return cust;
    }

    @Override
    public ImageExporterI getImageExporter() {
        return new ImageExporterI() {

            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG, FileType.JPEG, FileType.SVG};
            }

            @Override
            public ImageExporterI.Result export(FileType type, String fName) throws Exception {
                JTableHeader hdr = table.getTableHeader();

                switch (type) {
                    case PNG:
                        BufferedImage tableImage = new BufferedImage(1280, 1024, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = tableImage.createGraphics();
                        hdr.printAll(g2);
                        g2.translate(0, hdr.getHeight());
                        table.printAll(g2);
                        ImageIO.write(tableImage, "png", new File(fName));
                        return ImageExporterI.Result.SUCCESS;
                    case JPEG:
                        BufferedImage bi = new BufferedImage(1280, 1024, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = bi.createGraphics();
                        hdr.printAll(g2d);
                        g2d.translate(0, hdr.getHeight());
                        table.printAll(g2d);
                        ImageIO.write(bi, "jpg", new File(fName));
                        return ImageExporterI.Result.SUCCESS;
                    case SVG:
                        SVGGraphics2D svg = new SVGGraphics2D(1280, 1024);
                        hdr.printAll(svg);
                        svg.translate(0, hdr.getHeight());
                        table.printAll(svg);
                        String svgElement = svg.getSVGElement();
                        try ( BufferedWriter bw = new BufferedWriter(new FileWriter(fName))) {
                            bw.write(svgElement);
                        }
                        return Result.SUCCESS;
                    default:
                        return Result.ERROR;
                }
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public SequenceExporterI[] getSequenceExporters() {
        List<SequenceExporterI> ret = new ArrayList<>(dists.size());
        Set<String> seenGeneNames = new HashSet<>();
        for (Pair<GroupI, DistributionI<Double>> p : dists) {
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
}
