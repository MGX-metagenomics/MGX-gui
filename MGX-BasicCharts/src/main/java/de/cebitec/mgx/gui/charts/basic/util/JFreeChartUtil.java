package de.cebitec.mgx.gui.charts.basic.util;

import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class JFreeChartUtil {

//    public static void saveSVG(JFreeChart chart, File f) {
//        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
//        Document document = domImpl.createDocument(null, "svg", null);
//        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
//        svgGenerator.getGeneratorContext().setPrecision(6);
//        chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, 800, 600), null);
//        boolean useCSS = true;
//        Writer out;
//        try {
//            out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
//            svgGenerator.stream(out, useCSS);
//        } catch (FileNotFoundException | UnsupportedEncodingException | SVGGraphics2DIOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//    }

    public static void savePNG(JFreeChart chart, File f) {
        try {
            ChartUtilities.saveChartAsPNG(f, chart, 800, 600);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void saveJPEG(JFreeChart chart, File f) {
        try {
            ChartUtilities.saveChartAsJPEG(f, chart, 800, 600);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
