package de.cebitec.mgx.gui.tableview;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datafactories.TreeFactory;
import de.cebitec.mgx.gui.viewer.api.AbstractViewer;
import de.cebitec.mgx.gui.viewer.api.CustomizableI;
import de.cebitec.mgx.gui.viewer.api.ViewerI;
import de.cebitec.mgx.gui.visgroups.VGroupManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jfree.svg.SVGGraphics2D;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author sj
 */
@ServiceProvider(service = ViewerI.class)
public class TreeTableView extends AbstractViewer<TreeI<Long>> implements ImageExporterI.Provider, CustomizableI {

    private JXTable table;
    private TableViewCustomizer cust = null;

    @Override
    public JComponent getComponent() {
        return table;
    }

    @Override
    public String getName() {
        return "Krona Table View";
    }

    @Override
    public boolean canHandle(AttributeTypeI valueType) {
        return valueType.getStructure() == AttributeTypeI.STRUCTURE_HIERARCHICAL
                && VGroupManager.getInstance().getActiveGroups().size() == 1;
    }

    @Override
    public Class<?> getInputType() {
        return TreeI.class;
    }

    @Override
    public void show(List<Pair<GroupI, TreeI<Long>>> dists) {

        final TreeI<Long> tree = dists.get(0).getSecond();

        // convert data to KRONA style
        TreeI<Long> kronaTree = TreeFactory.createKRONATree(tree);
        kronaTree = TreeFactory.filter(kronaTree, getCust().getFilterEntries());

        /*
         *  handle unaligned trees, where equal depth does not mean equal
         *  rank. caused by constructs like "suborder", which is _sometimes_
         *  present between order and whatever comes below.
         * 
         *  instead, rely on attribute types as defined by deepest node and
         *  align everything else to it, filling gaps if needed.
         * 
         */
        // setup column names, based on unfiltered tree
        AttributeTypeI[] longestPath = TreeFactory.getLongestPath(tree);
        String[] columns = new String[1 + longestPath.length];
        int i = 0;
        columns[i++] = "Count"; // first column
        for (AttributeTypeI at : longestPath) {
            columns[i++] = at.getName();
        }

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Long.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        setupRowData(model, longestPath, kronaTree.getRoot());
        cust.setModel(model); // for tsv export

        table = new JXTable(model);
        table.setFillsViewportHeight(true);
        for (TableColumn tc : table.getColumns()) {
            if (0 != tc.getModelIndex()) {
                tc.setMinWidth(20);
                tc.setPreferredWidth(40);
                tc.setWidth(40);
            }
        }
        table.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping()});
        table.setSortable(false);
    }

    @Override
    public JComponent getCustomizer() {
        return getCust();
    }

    private TableViewCustomizer getCust() {
        if (cust == null) {
            cust = new TableViewCustomizer();
            cust.kronaMode();
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
                        BufferedImage tableImage = new BufferedImage(1280, 1024, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2 = tableImage.createGraphics();
                        hdr.printAll(g2);
                        g2.translate(0, hdr.getHeight());
                        table.printAll(g2);
                        ImageIO.write(tableImage, "png", new File(fName));
                        return ImageExporterI.Result.SUCCESS;
                    case JPEG:
                        BufferedImage bi = new BufferedImage(1280, 1024, BufferedImage.TYPE_INT_RGB);
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
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fName))) {
                            bw.write(svgElement);
                        }
                        return ImageExporterI.Result.SUCCESS;
                    default:
                        return ImageExporterI.Result.ERROR;
                }
            }
        };
    }

    private void setupRowData(DefaultTableModel model, AttributeTypeI[] aTypes, NodeI<Long> node) {
        Object[] rowData = new Object[1 + aTypes.length];
        int pos = 0;

        // add the nodes content (i.e. sequence count) to the first column
        rowData[pos++] = node.getContent();

        NodeI<Long>[] path = node.getPath();

        for (AttributeTypeI at : aTypes) {
            String value = "";
            for (NodeI<Long> tmp : path) {
                if (tmp.getAttribute().getAttributeType().getName().equals(at.getName())) {
                    value = tmp.getAttribute().getValue();
                    break;
                }
            }
            rowData[pos++] = value;
        }

        model.addRow(rowData);

        // recurse for child nodes
        if (!node.isLeaf()) {
            for (NodeI<Long> child : node.getChildren()) {
                setupRowData(model, aTypes, child);
            }
        }
    }
}
