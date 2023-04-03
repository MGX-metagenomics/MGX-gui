package de.cebitec.mgx.gui.goldstandard.ui.charts;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.ImageExporterI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 * @author patrick
 */
public class VennChart extends JPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    private final BufferedImage image;

    //Sorted by binary numbering. E.g. ABC = 111 == 7, AC = 101 == 5
    private static final Point[] VALUE_COORDINATES_2 = new Point[]{null, new Point(1060, 420), new Point(340, 420), new Point(700, 420)};
    private static final Point[] VALUE_COORDINATES_3 = new Point[]{null, new Point(912, 920), new Point(216, 730), new Point(596, 856), new Point(696, 232), new Point(880, 574), new Point(474, 454), new Point(660, 618)};
    private static final Point[] VALUE_COORDINATES_4 = new Point[]{null, new Point(1254, 763), new Point(870, 982), new Point(1121, 788), new Point(174, 709), new Point(386, 818), new Point(654, 848), new Point(512, 831),
        new Point(686, 166), new Point(823, 361), new Point(862, 658), new Point(862, 513), new Point(416, 381), new Point(510, 494), new Point(708, 682), new Point(598, 594)};

    //Order: A, B, C,...
    private static final Point[] LABEL_COORDINATES_2 = new Point[]{new Point(650, 850), new Point(650, 900)};
    private static final Point[] LABEL_COORDINATES_3 = new Point[]{new Point(400, 1270), new Point(400, 1340), new Point(400, 1410)};
    private static final Point[] LABEL_COORDINATES_4 = new Point[]{new Point(400, 1270), new Point(400, 1340), new Point(400, 1410), new Point(400, 1470)};
    private static final Dimension VENN_DIMENSION_2 = new Dimension(1500, 1000);
    private static final Dimension VENN_DIMENSION_3 = new Dimension(1500, 1500);
    private static final Dimension VENN_DIMENSION_4 = new Dimension(1500, 1500);

    private static final int FONTSIZE = 32;

    private VennChart(BufferedImage img) {
        super();
        super.setBackground(Color.WHITE);
        image = img;
    }

    public static VennChart get2Venn(Collection<?> a, Collection<?> b, String labelA, String labelB) throws IOException {
        return get2Venn(CollectionUtils.subtract(a, b).size(), CollectionUtils.subtract(b, a).size(), CollectionUtils.intersection(a, b).size(), labelA, labelB);
    }

    public static VennChart get2Venn(long onlyA, long onlyB, long ab, String labelA, String labelB) throws IOException {
        List<Long> list = new ArrayList<>(4);
        list.add(null);
        list.add(onlyB);            //only b
        list.add(onlyA);            //only a        
        list.add(ab);        //ab

        BufferedImage img;
        try {
            img = ImageIO.read(VennChart.class.getClassLoader().getResource("de/cebitec/mgx/gui/goldstandard/ui/charts/Venn_2er_big.png"));
        } catch (IOException ex) {
            throw new IOException("Venn image not found.", ex);
        }

        Graphics2D g2d = img.createGraphics();
        for (int i = 1; i < VALUE_COORDINATES_2.length; i++) {
            g2d.drawImage(img, 0, 0, null);
            g2d.setPaint(Color.BLACK);
            g2d.setFont(new Font("SansSerif", Font.BOLD, FONTSIZE));
            String str = String.valueOf(list.get(i));
            g2d.drawString(str, VALUE_COORDINATES_2[i].x, VALUE_COORDINATES_2[i].y);
        }

        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, FONTSIZE));
        g2d.drawString("1: " + labelA, LABEL_COORDINATES_2[0].x, LABEL_COORDINATES_2[0].y);

        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, FONTSIZE));
        g2d.drawString("2: " + labelB, LABEL_COORDINATES_2[1].x, LABEL_COORDINATES_2[1].y);

        g2d.dispose();
        return new VennChart(img);
    }

    public static VennChart get3Venn(long onlyA, long onlyB, long onlyC, long ab, long ac, long bc, long abc, String labelA, String labelB, String labelC) throws IOException {
        List<Long> list = new ArrayList<>(8);
        list.add(null);
        list.add(onlyC);            //only c
        list.add(onlyB);            //only b
        list.add(bc);        //bc
        list.add(onlyA);            //only a
        list.add(ac);        //ac        
        list.add(ab);        //ab                
        list.add(abc);        //abc

        BufferedImage img;
        try {
            img = ImageIO.read(VennChart.class.getClassLoader().getResource("de/cebitec/mgx/gui/goldstandard/ui/charts/Venn_3er_big.png"));
        } catch (IOException ex) {
            throw new IOException("Venn image not found.", ex);
        }

        Graphics2D g2d = img.createGraphics();
        for (int i = 1; i < VALUE_COORDINATES_3.length; i++) {
            g2d.drawImage(img, 0, 0, null);
            g2d.setPaint(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, FONTSIZE));
            String str = String.valueOf(list.get(i));
            g2d.drawString(str, VALUE_COORDINATES_3[i].x, VALUE_COORDINATES_3[i].y);
        }

        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, FONTSIZE));
        g2d.drawString("1: " + labelA, LABEL_COORDINATES_3[0].x, LABEL_COORDINATES_3[0].y);

        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, FONTSIZE));
        g2d.drawString("2: " + labelB, LABEL_COORDINATES_3[1].x, LABEL_COORDINATES_3[1].y);

        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, FONTSIZE));
        g2d.drawString("3: " + labelC, LABEL_COORDINATES_3[2].x, LABEL_COORDINATES_3[2].y);

        g2d.dispose();
        return new VennChart(img);
    }

    public static VennChart get4Venn(List<Long> values, String labelA, String labelB, String labelC, String labelD) throws IOException {
        BufferedImage img;
        try {
            img = ImageIO.read(VennChart.class.getClassLoader().getResource("de/cebitec/mgx/gui/goldstandard/ui/charts/Venn_4er_big.png"));
        } catch (IOException ex) {
            throw new IOException("Venn image not found.", ex);
        }

        Graphics2D g2d = img.createGraphics();
        for (int i = 1; i < VALUE_COORDINATES_4.length; i++) {
            g2d.drawImage(img, 0, 0, null);
            g2d.setPaint(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, FONTSIZE));
            String str = String.valueOf(values.get(i));
            g2d.drawString(str, VALUE_COORDINATES_4[i].x, VALUE_COORDINATES_4[i].y);
        }

        String[] labelArray = new String[]{labelA, labelB, labelC, labelD};
        for (int i = 0; i < labelArray.length; i++) {
            g2d.drawImage(img, 0, 0, null);
            g2d.setPaint(Color.BLACK);
            g2d.setFont(new Font("SansSerif", Font.BOLD, FONTSIZE));
            g2d.drawString((i + 1) + ": " + labelArray[i], LABEL_COORDINATES_4[i].x, LABEL_COORDINATES_4[i].y);
        }

        g2d.dispose();
        return new VennChart(img);
    }

    public static ImageExporterI getImageExporter(final VennChart venn) {
        return new ImageExporterI() {
            @Override
            public FileType[] getSupportedTypes() {
                return new FileType[]{FileType.PNG, FileType.JPEG};
            }

            @Override
            public ImageExporterI.Result export(FileType type, String fName) throws Exception {
                switch (type) {
                    case PNG:
                        ImageIO.write(venn.image, "png", new File(fName));
                        return ImageExporterI.Result.SUCCESS;
                    case JPEG:
                        // FIXME?
                        return ImageExporterI.Result.SUCCESS;
                    default:
                        return ImageExporterI.Result.ERROR;
                }
            }
        };
    }

//    private static void create() throws IOException {
//        JFrame f = new JFrame();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        //f.add(get2Venn(a, b, "Group A", "Group B"));
//        f.add(get3Venn(10, 10, 10, 5, 5, 5, 1, "Label A", "Label B", "Label C"));
//        f.pack();
//        f.setVisible(true);
//    }
//
//    public static void main(String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    create();
//                } catch (IOException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }
//        });
//    }
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        double factor = (double) this.getWidth() / image.getWidth();
        int newH = (int) (image.getHeight() * factor);
        if (newH > this.getHeight()) {
            factor = (double) this.getHeight() / image.getHeight();
        }
        int newW = (int) (image.getWidth() * factor);
        newH = (int) (image.getHeight() * factor);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        super.paintComponent(g2);
        g2.drawImage(image, 0, 0, newW, newH, null);
    }

    private static Color getFontColor(Color background) {
        return (background.getBlue() + background.getRed() + background.getGreen() > 254) ? Color.BLACK : Color.WHITE;
    }

}
