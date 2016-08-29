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

    private static final Point[] VALUE_COORDINATES_2 = new Point[]{new Point(340, 420), new Point(1060, 420), new Point(700, 420)};
    private static final Point[] LABEL_COORDINATES_2 = new Point[]{new Point(650, 850), new Point(650, 900)};
    private static final Color[] fontColor2 = new Color[]{new Color(255, 0, 0), new Color(255, 255, 0), new Color(255, 127, 0)};
    private static final Point[] VALUE_COORDINATES_3 = new Point[]{new Point(696, 232), new Point(216, 730), new Point(912, 920), new Point(474, 454), new Point(880, 574), new Point(596, 856), new Point(660, 618)};
    private static final Point[] LABEL_COORDINATES_3 = new Point[]{new Point(400, 1250), new Point(400, 1300), new Point(400, 1350)};
    private static final Color[] fontColor3 = new Color[]{new Color(255, 0, 0), new Color(0, 255, 0), new Color(0, 0, 255), new Color(127, 127, 0), new Color(127, 0, 127), new Color(0, 127, 127), new Color(85, 85, 85)};
    private static final Dimension venn2 = new Dimension(1500, 1000);
    private static final Dimension venn3 = new Dimension(1500, 1500);

    private VennChart(BufferedImage img) {
        super();
        super.setBackground(Color.WHITE);
        image = img;
    }

    public static VennChart get2Venn(Collection<?> a, Collection<?> b, String labelA, String labelB) throws IOException {
        return get2Venn(CollectionUtils.subtract(a, b).size(), CollectionUtils.subtract(b, a).size(), CollectionUtils.intersection(a, b).size(), labelA, labelB);
    }

    public static VennChart get2Venn(long onlyA, long onlyB, long ab, String labelA, String labelB) throws IOException {
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
        for (int i = 0; i < VALUE_COORDINATES_2.length; i++) {
            g2d.drawImage(img, 0, 0, null);
            g2d.setPaint(getFontColor(fontColor2[i]));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
            String str = String.valueOf(list.get(i));
            g2d.drawString(str, VALUE_COORDINATES_2[i].x, VALUE_COORDINATES_2[i].y);
        }
        
        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));        
        g2d.drawString("1: " + labelA, LABEL_COORDINATES_2[0].x, LABEL_COORDINATES_2[0].y);
        
        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));        
        g2d.drawString("2: " + labelB, LABEL_COORDINATES_2[1].x, LABEL_COORDINATES_2[1].y);
        
        g2d.dispose();
        return new VennChart(img);
    }
    
    public static VennChart get3Venn(long onlyA, long onlyB, long onlyC, long ab, long ac, long bc, long abc, String labelA, String labelB, String labelC) throws IOException {
        List<Long> list = new ArrayList<>(7);
        list.add(onlyA);            //only a
        list.add(onlyB);            //only b
        list.add(onlyC);            //only c
        list.add(ab);        //ab
        list.add(ac);        //ac
        list.add(bc);        //bc
        list.add(abc);        //abc

        BufferedImage img;
        try {
            img = ImageIO.read(VennChart.class.getClassLoader().getResource("de/cebitec/mgx/gui/goldstandard/ui/charts/Venn_3er_big.png"));
        } catch (IOException ex) {
            throw new IOException("Venn image not found.", ex);
        }

        Graphics2D g2d = img.createGraphics();
        for (int i = 0; i < VALUE_COORDINATES_3.length; i++) {
            g2d.drawImage(img, 0, 0, null);
            g2d.setPaint(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
            String str = String.valueOf(list.get(i));
            g2d.drawString(str, VALUE_COORDINATES_3[i].x, VALUE_COORDINATES_3[i].y);
        }
        
        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));        
        g2d.drawString("1: " + labelA, LABEL_COORDINATES_3[0].x, LABEL_COORDINATES_3[0].y);
        
        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));        
        g2d.drawString("2: " + labelB, LABEL_COORDINATES_3[1].x, LABEL_COORDINATES_3[1].y);
        
        g2d.drawImage(img, 0, 0, null);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));        
        g2d.drawString("3: " + labelC, LABEL_COORDINATES_3[2].x, LABEL_COORDINATES_3[2].y);
        
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
        //f.add(get2Venn(a, b, "Group A", "Group B"));
        f.add(get3Venn(10, 10, 10, 5, 5, 5, 1, "Label A", "Label B", "Label C"));
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
