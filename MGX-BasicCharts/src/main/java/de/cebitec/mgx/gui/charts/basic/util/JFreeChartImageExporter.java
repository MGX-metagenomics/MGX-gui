package de.cebitec.mgx.gui.charts.basic.util;

import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.groups.ImageExporterI;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.svg.SVGGraphics2D;

/**
 *
 * @author sj
 */
public class JFreeChartImageExporter implements ImageExporterI {

    private final JFreeChart chart;

    public JFreeChartImageExporter(JFreeChart chart) {
        this.chart = chart;
    }
    
    @Override
    public FileType[] getSupportedTypes() {
        return new FileType[]{FileType.PNG, FileType.JPEG, FileType.SVG};
    }

    @Override
    public Result export(FileType type, String fName) throws Exception {
        switch (type) {
            case PNG:
                ChartUtils.saveChartAsPNG(new File(fName), chart, 1280, 1024);
                return Result.SUCCESS;
            case JPEG:
                ChartUtils.saveChartAsJPEG(new File(fName), chart, 1280, 1024);
                return Result.SUCCESS;
            case SVG:
                SVGGraphics2D g2 = new SVGGraphics2D(1280, 768);
                chart.draw(g2, new Rectangle(0, 0, 1280, 768));
                String svgElement = g2.getSVGElement();
                try ( BufferedWriter bw = new BufferedWriter(new FileWriter(fName))) {
                    bw.write(svgElement);
                }
                return Result.SUCCESS;
            default:
                return Result.ERROR;
        }
    }
}
