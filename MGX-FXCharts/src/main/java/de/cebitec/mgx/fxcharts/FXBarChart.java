/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.fxcharts;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import de.cebitec.mgx.gui.attributevisualization.viewer.CategoricalViewerI;
import de.cebitec.mgx.gui.attributevisualization.viewer.ViewerI;
import de.cebitec.mgx.gui.charts.basic.customizer.BarChartCustomizer;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.util.Util;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * BarChart Visualisierung
 *
 * Top component which displays something.
 */
@ServiceProvider(service = ViewerI.class)
public final class FXBarChart extends CategoricalViewerI {

   /**
    * Aktuelle Daten zum darstellen.
    */
   private List<Pair<VisualizationGroup, Distribution>> dists;
   /**
    * Filtert die Daten.
    */
   private BarChartCustomizer customizer = null;
   /**
    * JFXPanel.
    */
   private static JFXPanel chartFxPanel;
   /**
    * BarChart fuer die Visualisierung.
    */
   private BarChart chart;
   /**
    * JPanel zum Einfuegen des charts.
    */
   private JPanel panel;


   /**
    * Konstruktor zur Initialisation.
    */
   public FXBarChart() {
	dists = new ArrayList<Pair<VisualizationGroup, Distribution>>();
	Platform.setImplicitExit(false);
   }

   /**
    * Erzeugt die Scene fuer den BarChart.
    */
   private void createScene() {
	chart = createBarChart();
	Scene scene = new Scene(chart);
	chartFxPanel.setScene(scene);

	new Timeline(
	    new KeyFrame(
	    Duration.millis(100),
	    new EventHandler<ActionEvent>() {
		 @Override
		 public void handle(ActionEvent t) {

		    setData();
		    setColor();
		    chart.layout();
		 }
	    })).play();


	chartFxPanel.repaint();
	chart.layout();
   }

   /**
    * Setzt die Daten in den BarChar.
    */
   private void setData() {
	XYChart.Series<String, Number> series;
	for (int i = 0; i < dists.size(); i++) {
	   series = new XYChart.Series<String, Number>();
	   series.setName(dists.get(i).getFirst().getName());
	   Iterator<Entry<Attribute, Number>> iter =
		 dists.get(i).getSecond().entrySet().iterator();

	   while (iter.hasNext()) {
		Entry<Attribute, Number> entry = iter.next();
		XYChart.Data<String, Number> bar = new XYChart.Data<String, Number>(entry.getKey().getValue(), entry.getValue().intValue());

		series.getData().add(bar);
	   }
	   chart.getData().add(series);
	   for (int j = 0; j < dists.get(i).getSecond().entrySet().size(); j++) {
		((XYChart.Series<String, Number>) chart.getData().get(i)).getData().get(j).getNode().setStyle("-fx-bar-fill: " + Util.convertColorToHexString(dists.get(i).getFirst().getColor()) + ";");
	   }
	}
   }

   /**
    * Erstellt den BarChart.
    *
    * @return BarChart
    */
   private BarChart createBarChart() {
	CategoryAxis xAxis = new CategoryAxis();
	String xAxisLabel = "";
	xAxis.setLabel(xAxisLabel);
	NumberAxis yAxis = new NumberAxis();

	yAxis.setAutoRanging(true);
	yAxis.setAnimated(true);
	yAxis.autosize();

	yAxis.setForceZeroInRange(true);
	xAxis.setAutoRanging(true);
	xAxis.setAnimated(true);
	xAxis.autosize();
	xAxis.setEndMargin(10);



	String yAxisLabel = getCustomizer().useFractions() ? "Fraction" : "Count";
	yAxis.setLabel(yAxisLabel);
	BarChart chart = new BarChart(xAxis, yAxis);
	chart.setLegendVisible(true);
	chart.setBarGap(1);
	chart.setCategoryGap(2);

	return chart;
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">
   private void initComponents() {
	javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
	panel.setLayout(layout);
	layout.setHorizontalGroup(
	    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    .addGap(0, 400, Short.MAX_VALUE));
	layout.setVerticalGroup(
	    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    .addGap(0, 300, Short.MAX_VALUE));
   }// </editor-fold>

   void writeProperties(java.util.Properties p) {
	// better to version settings since initial version as advocated at
	// http://wiki.apidesign.org/wiki/PropertyFiles
	p.setProperty("version", "1.0");
	// TODO store your settings
   }

   void readProperties(java.util.Properties p) {
	String version = p.getProperty("version");
	// TODO read your settings according to their version
   }

   @Override
   public JComponent getComponent() {
	return panel;
   }

   /**
    * Gibt den Namen des Diagramms zurück.
    *
    * @return Name
    */
   @Override
   public String getName() {
	return "JavaFX";
   }

   @Override
   public Class getInputType() {
	return Distribution.class;
   }

   /**
    *
    * Schnittstelle fuer die Datenbeschaffung.
    *
    * @param dists aktuelle Distributionen
    */
   @Override
   public void show(List<Pair<VisualizationGroup, Distribution>> dists) {
	this.dists = getCustomizer().filter(dists);

	panel = new JPanel();
	initComponents();
	panel.setLayout(new BorderLayout());

	chartFxPanel = new JFXPanel();
	panel.add(chartFxPanel, BorderLayout.CENTER);
	Platform.runLater(new Runnable() {
	   @Override
	   public void run() {
		createScene();
	   }
	});
   }

   /**
    * Gibt die Aktuellen Konfigurationen zurueck.
    *
    * @return Konfigurationen.
    */
   @Override
   public BarChartCustomizer getCustomizer() {
	if (customizer == null) {
	   customizer = new BarChartCustomizer();
	   customizer.setAttributeType(getAttributeType());
	}
	return customizer;
   }

   /**
    * Setzt die Farben der Kategorien.
    */
   private void setColor() {
	for (int k = 0; k < dists.size(); k++) {
	   for (Node n : chart.lookupAll(".bar-legend-symbol.default-color" + k)) {
		n.setStyle("-fx-background-color: " + Util.convertColorToHexString(dists.get(k).getFirst().getColor()) + ";");
	   }
	}
   }
}