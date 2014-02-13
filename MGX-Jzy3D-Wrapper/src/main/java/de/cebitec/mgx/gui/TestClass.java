//package de.cebitec.mgx.gui;
//
//import org.jzy3d.chart.Chart;
//import org.jzy3d.colors.Color;
//import org.jzy3d.colors.ColorMapper;
//import org.jzy3d.colors.colormaps.ColorMapRainbow;
//import org.jzy3d.maths.Coord3d;
//import org.jzy3d.plot3d.primitives.MultiColorScatter;
//import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;
//import org.jzy3d.ui.ChartLauncher;
//
///**
// *
// * @author sj
// */
//public class TestClass {
//
//    public static void main(String[] args) {
//        int size = 100000;
//        float x;
//        float y;
//        float z;
//
//        Coord3d[] points = new Coord3d[size];
//
//        for (int i = 0; i < size; i++) {
//            x = (float) Math.random() - 0.5f;
//            y = (float) Math.random() - 0.5f;
//            z = (float) Math.random() - 0.5f;
//            points[i] = new Coord3d(x, y, z);
//        }
//
//        MultiColorScatter scatter = new MultiColorScatter(points, new ColorMapper(new ColorMapRainbow(), -0.5f, 0.5f));
//
//        Chart chart = new Chart();
//        chart.getAxeLayout().setMainColor(Color.WHITE);
//        chart.getView().setBackgroundColor(Color.BLACK);
//        chart.getScene().add(scatter);
//        scatter.setLegend(new ColorbarLegend(scatter, chart.getView().getAxe().getLayout(), Color.WHITE, null));
//        scatter.setLegendDisplayed(true);
//
//        ChartLauncher.openChart(chart);
//    }
//}
