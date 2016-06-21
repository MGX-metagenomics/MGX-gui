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
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.commons.collections4.CollectionUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author patrick
 */
public class VennChart extends JPanel {

    private BufferedImage image;

    private static final Point[] fontCoordinates2 = new Point[]{new Point(340, 420), new Point(1060, 420), new Point(700, 420)};
    private static final Color[] fontColor2 = new Color[]{new Color(255, 0, 0), new Color(255, 255, 0), new Color(255, 127, 0)};
    private static final Dimension venn2 = new Dimension(1500, 1000);

    private VennChart(BufferedImage img) {
        super();
        super.setBackground(Color.WHITE);
        image = img;
    }

    public static VennChart get2Venn(Collection<?> a, Collection<?> b) throws IOException {
        return get2Venn(CollectionUtils.subtract(a, b).size(), CollectionUtils.subtract(b, a).size(), CollectionUtils.intersection(a, b).size());
    }

    public static VennChart get2Venn(long onlyA, long onlyB, long ab) throws IOException {
        List<Long> list = new ArrayList<>(3);
        list.add(onlyA);            //only a
        list.add(onlyB);            //only b
        list.add(ab);        //ab

        BufferedImage img;
        try {
            img = ImageIO.read(VennChart.class.getClassLoader().getResource("de/cebitec/mgx/gui/goldstandard/ui/charts/Venn_2er_big.png"));
        } catch (IOException ex) {
            throw new IOException("Venn image not found.", ex);
        }

        Graphics2D g2d = img.createGraphics();
        for (int i = 0; i < fontCoordinates2.length; i++) {
            g2d.drawImage(img, 0, 0, null);
            g2d.setPaint(getFontColor(fontColor2[i]));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
            String str = String.valueOf(list.get(i));
            g2d.drawString(str, fontCoordinates2[i].x, fontCoordinates2[i].y);
        }
        g2d.dispose();
        return new VennChart(img);
    }

    static ImageExporterI getImageExporter(final VennChart venn) {
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
                        return ImageExporterI.Result.SUCCESS;
                    default:
                        return ImageExporterI.Result.ERROR;
                }
            }
        };
    }

    private static void create() throws IOException {
        Set<Integer> a = new HashSet<>();
        a.add(1);
        a.add(2);
        a.add(3);
        a.add(4);
        Set<Integer> b = new HashSet<>();
        b.add(5);
        b.add(6);
        b.add(3);
        b.add(4);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(get2Venn(a, b));
        f.pack();
        f.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    create();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

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